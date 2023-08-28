package com.binarios.gestionticket.service;

import com.binarios.gestionticket.dto.request.TicketDTO;
import com.binarios.gestionticket.dto.response.PersonResponseDTO;
import com.binarios.gestionticket.dto.response.TicketResponseDTO;
import com.binarios.gestionticket.entities.Attachment;
import com.binarios.gestionticket.entities.Person;
import com.binarios.gestionticket.entities.Ticket;
import com.binarios.gestionticket.enums.PersonRole;
import com.binarios.gestionticket.enums.TicketStatus;
import com.binarios.gestionticket.repositories.AttachmentRepository;
import com.binarios.gestionticket.repositories.PersonRepository;
import com.binarios.gestionticket.repositories.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AttachmentService attachmentService;

    private final PersonRepository personRepository;
    private final AttachmentRepository attachmentRepository;


    public TicketService(TicketRepository ticketRepository, AttachmentService attachmentService, PersonRepository personRepository,
                         AttachmentRepository attachmentRepository) {
        this.ticketRepository = ticketRepository;
        this.attachmentService = attachmentService;
        this.personRepository = personRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public TicketResponseDTO saveTicket(TicketDTO ticketDTO, MultipartFile file) throws Exception {

        Ticket ticket = new Ticket();
        ticket.setName(ticketDTO.getName());
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setStatus(TicketStatus.CLOSED);
        Optional<Person> client = personRepository.findById(ticketDTO.getClient_id());
        //Optional<Person> assignedTech = personRepository.findById(ticketDTO.getAssignedTech_id());

        Optional<Person> admin = personRepository.findByRole(PersonRole.ADMIN);

        ticket.setClient(client.orElse(null));
        //ticket.setAssignedTech(assignedTech.orElse(null));wha
        ticket.setAdmin(admin.orElse(null));


        //Save the attachment
        Collection<Attachment> attachments = new ArrayList<>();
        attachments.add(attachmentService.saveFile(file));

        // Convert Collection to List
        List<Attachment> attachmentList = new ArrayList<>(attachments);

        //Setting the attachment field
        ticket.setAttachments(attachmentList);


        Ticket savedTicket = ticketRepository.save(ticket);

        // Create a new PersonDTO and set the ID and generated username
        TicketResponseDTO createdTicketDTO = new TicketResponseDTO();
        createdTicketDTO.setId(savedTicket.getId());
        createdTicketDTO.setName(savedTicket.getName());
        createdTicketDTO.setDescription(savedTicket.getDescription());
        createdTicketDTO.setStatus(savedTicket.getStatus());
        createdTicketDTO.setAdmin(mapPersonToPersonResponseDTO(savedTicket.getAdmin()));
        if (Objects.nonNull(savedTicket.getAssignedTech())) {
            createdTicketDTO.setAssignedTech(mapPersonToPersonResponseDTO(savedTicket.getAssignedTech()));
        }
        createdTicketDTO.setClient(mapPersonToPersonResponseDTO(savedTicket.getClient()));
        createdTicketDTO.setAttachments(savedTicket.getAttachments());

        return createdTicketDTO;
    }

    public Collection<TicketResponseDTO> getAllTickets() {
        Collection<Ticket> tickets = ticketRepository.findAll();
        Collection<TicketResponseDTO> ticketResponseDTOS = new ArrayList<>();

        for (Ticket ticket : tickets) {
            TicketResponseDTO createdTicketDTO = new TicketResponseDTO();
            createdTicketDTO.setId(ticket.getId());
            createdTicketDTO.setName(ticket.getName());
            createdTicketDTO.setDescription(ticket.getDescription());
            createdTicketDTO.setStatus(ticket.getStatus());
            createdTicketDTO.setClient(mapPersonToPersonResponseDTO(ticket.getClient()));
            if (Objects.nonNull(ticket.getAssignedTech())) {
                createdTicketDTO.setAssignedTech(mapPersonToPersonResponseDTO(ticket.getAssignedTech()));
            }
            //createdTicketDTO.setAssignedTech(mapPersonToPersonResponseDTO(ticket.getAssignedTech()));
            createdTicketDTO.setAdmin(mapPersonToPersonResponseDTO(ticket.getAdmin()));
            createdTicketDTO.setAttachments(ticket.getAttachments());
            ticketResponseDTOS.add(createdTicketDTO);
        }
        return ticketResponseDTOS;
    }

    public TicketResponseDTO editTicket(Long id, TicketDTO ticketDTO, MultipartFile file) throws Exception {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new Exception("There is no ticket with this id"));

        ticket.setName(ticketDTO.getName());
        ticket.setDescription(ticketDTO.getDescription());
        //ticket.setStatus(ticketDTO.getStatus());

        Optional<Person> client = personRepository.findById(ticketDTO.getClient_id());
        //Optional<Person> admin = personRepository.findById(ticketDTO.getAdmin_id());

        ticket.setClient(client.orElse(null));
        //ticket.setAdmin(admin.orElse(null));

        // Save the attachment if it exists
        if (!file.isEmpty()) {
            Attachment attachment = attachmentService.saveFile(file);
            ticket.getAttachments().add(attachment);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        // Create a new TicketResponseDTO and set the fields
        TicketResponseDTO createdTicketDTO = new TicketResponseDTO();
        createdTicketDTO.setId(savedTicket.getId());
        createdTicketDTO.setName(savedTicket.getName());
        createdTicketDTO.setDescription(savedTicket.getDescription());
        createdTicketDTO.setStatus(savedTicket.getStatus());
        createdTicketDTO.setAdmin(mapPersonToPersonResponseDTO(savedTicket.getAdmin()));
        if (Objects.nonNull(savedTicket.getAssignedTech())) {
            createdTicketDTO.setAssignedTech(mapPersonToPersonResponseDTO(savedTicket.getAssignedTech()));
        }
        //createdTicketDTO.setAssignedTech(mapPersonToPersonResponseDTO(savedTicket.getAssignedTech()));
        createdTicketDTO.setClient(mapPersonToPersonResponseDTO(savedTicket.getClient()));
        createdTicketDTO.setAttachments(savedTicket.getAttachments());

        return createdTicketDTO;
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null); // Use orElse(null) to handle the case where the ticket doesn't exist

        if (ticket != null) {
            // Delete the attachments associated with the ticket
            // Delete each attachment here (implementation depends on your Attachment class and repository)
            // Replace with your actual attachment repository method
            attachmentRepository.deleteAll(ticket.getAttachments());

            // Now, delete the ticket itself
            ticketRepository.delete(ticket);
        }
    }

    public TicketResponseDTO assignTicket(Long ticketId, Long techId) throws Exception {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        Person tech = personRepository.findById(techId).orElse(null);
        if (ticket == null) {
            throw new Exception("There is now ticket with this id");
        }
        if (tech == null || !tech.getRole().name().equals(PersonRole.TECH.name())) {
            throw new Exception("There is now tech with this id");
        }
        if (Objects.nonNull(ticket.getAssignedTech())) {

        }
        ticket.setAssignedTech(tech);
        ticketRepository.save(ticket);

        // Create a new TicketResponseDTO and set the fields to give back in response
        TicketResponseDTO createdTicketDTO = new TicketResponseDTO();
        createdTicketDTO.setId(ticket.getId());
        createdTicketDTO.setName(ticket.getName());
        createdTicketDTO.setDescription(ticket.getDescription());
        createdTicketDTO.setStatus(ticket.getStatus());
        createdTicketDTO.setAdmin(mapPersonToPersonResponseDTO(ticket.getAdmin()));
        if (Objects.nonNull(ticket.getAssignedTech())) {
            createdTicketDTO.setAssignedTech(mapPersonToPersonResponseDTO(ticket.getAssignedTech()));
        }
        //createdTicketDTO.setAssignedTech(mapPersonToPersonResponseDTO(ticket.getAssignedTech()));
        createdTicketDTO.setClient(mapPersonToPersonResponseDTO(ticket.getClient()));
        createdTicketDTO.setAttachments(ticket.getAttachments());

        return createdTicketDTO;
    }


    //updateTicketStatus

    public TicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus newStatus) throws Exception {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new Exception("Ticket not found with ID: " + ticketId));

        // Ensure the status transition is valid (optional step, based on your business rules)
        if (!isValidStatusTransition(ticket.getStatus(), newStatus)) {
            throw new Exception("Invalid status transition.");
        }

        ticket.setStatus(newStatus);
        Ticket updatedTicket = ticketRepository.save(ticket);


        // Create a new TicketResponseDTO and set the fields to return in the response
        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setId(updatedTicket.getId());
        responseDTO.setName(updatedTicket.getName());
        responseDTO.setDescription(updatedTicket.getDescription());
        responseDTO.setStatus(updatedTicket.getStatus());
        responseDTO.setAdmin(mapPersonToPersonResponseDTO(updatedTicket.getAdmin()));
        if (Objects.nonNull(updatedTicket.getAssignedTech())) {
            responseDTO.setAssignedTech(mapPersonToPersonResponseDTO(updatedTicket.getAssignedTech()));
        }
        responseDTO.setClient(mapPersonToPersonResponseDTO(updatedTicket.getClient()));
        responseDTO.setAttachments(updatedTicket.getAttachments());

        return responseDTO;
    }

    public List<Attachment> getAttachmentsByTicketId(Long ticketId) throws Exception {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) {
            throw new Exception("There is no ticket with this id : " + ticketId);
        }
        return ticket.getAttachments();
    }


    // Helper method to check if the status transition is valid (optional)
    private boolean isValidStatusTransition(TicketStatus currentStatus, TicketStatus newStatus) {
        // Implement your business logic to determine whether the transition is valid
        // For example, you might define specific allowed transitions based on business rules.
        // You can also consider using an enum method to handle this validation.
        // For demonstration purposes, we'll allow any status transitions here.
        return true;

    }

    public Collection<TicketResponseDTO> getCreatedTickets(Long personId) throws Exception {
        Person person = personRepository.findById(personId).orElseThrow(() -> new Exception("there is no Person with the id : " + personId));
        Collection<Ticket> tickets = ticketRepository.findTicketsByPersonId(personId);
        Collection<TicketResponseDTO> ticketResponseDTOS = new ArrayList<>();

        for (Ticket ticket : tickets) {
            TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
            ticketResponseDTO.setId(ticket.getId());
            ticketResponseDTO.setName(ticket.getName());
            ticketResponseDTO.setStatus(ticket.getStatus());
            ticketResponseDTO.setDescription(ticket.getDescription());
            ticketResponseDTO.setAdmin(mapPersonToPersonResponseDTO(ticket.getAdmin()));
            ticketResponseDTO.setClient(mapPersonToPersonResponseDTO(ticket.getClient()));
            if (Objects.nonNull(ticket.getAssignedTech())) {
                ticketResponseDTO.setAssignedTech(mapPersonToPersonResponseDTO(ticket.getAssignedTech()));
            }
            //ticketResponseDTO.setAssignedTech(mapPersonToPersonResponseDTO(ticket.getAssignedTech()));
            ticketResponseDTO.setAttachments(ticket.getAttachments());
            ticketResponseDTOS.add(ticketResponseDTO);
        }


        return ticketResponseDTOS;
    }

    private PersonResponseDTO mapPersonToPersonResponseDTO(Person person) {
        PersonResponseDTO personResponseDTO = new PersonResponseDTO();
        personResponseDTO.setId(person.getId());
        personResponseDTO.setUsername(person.getUsername());
        personResponseDTO.setRole(person.getRole().toString()); // Assuming you want to convert the enum to a string
        personResponseDTO.setEmail(person.getEmail());
        personResponseDTO.setPhoneNumber(person.getPhoneNumber());
        personResponseDTO.setBirthDate(person.getBirthDate());
        personResponseDTO.setFullName(person.getFullName());
        if (Objects.nonNull(person.getGroup())) {
            personResponseDTO.setGroup(person.getGroup().getId());
        }
        if (Objects.nonNull(person.getSpecialite())) {
            personResponseDTO.setSpecialite(person.getSpecialite());
        }
        personResponseDTO.setActive(person.isActive());

        return personResponseDTO;
    }


}