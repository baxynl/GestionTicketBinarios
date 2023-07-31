package com.binarios.gestionticket.service;

import com.binarios.gestionticket.dto.request.PersonDTO;
import com.binarios.gestionticket.dto.request.TechDTO;
import com.binarios.gestionticket.dto.response.PersonResponseDTO;
import com.binarios.gestionticket.dto.response.TechResponseDTO;
import com.binarios.gestionticket.entities.Person;
import com.binarios.gestionticket.enums.PersonRole;
import com.binarios.gestionticket.repositories.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    public PersonResponseDTO createUser(PersonDTO personDTO) {
            Person person = new Person();
            person.setFullName(personDTO.getFullName());
            person.setPassword(personDTO.getPassword());
            person.setRole(PersonRole.valueOf(personDTO.getRole()));
            person.setEmail(personDTO.getEmail());
            person.setPhoneNumber(personDTO.getPhoneNumber());
            person.setBirthDate(personDTO.getBirthDate());

            // Generate the username automatically based on fullName and a random 4-digit number
            String generatedUsername = generateUsername(personDTO.getFullName());
            person.setUsername(generatedUsername);

            Person savedPerson = personRepository.save(person);

            // Create a new PersonDTO and set the ID and generated username
            PersonResponseDTO createdPersonDTO = new PersonResponseDTO();
            createdPersonDTO.setId(savedPerson.getId());
            createdPersonDTO.setUsername(savedPerson.getUsername());
            createdPersonDTO.setRole(savedPerson.getRole().name());
            createdPersonDTO.setEmail(savedPerson.getEmail());
            createdPersonDTO.setPhoneNumber(savedPerson.getPhoneNumber());
            createdPersonDTO.setBirthDate(savedPerson.getBirthDate());
            createdPersonDTO.setFullName(savedPerson.getFullName());

            return createdPersonDTO;
        }

        //Username Creation Logic (fullName + 4-digits (Randomly generated))

    private String generateUsername(String fullName) {
        // Remove any spaces from the full name and convert to lowercase
        String fullNameWithoutSpace = fullName.replaceAll("\\s", "").toLowerCase();

        // Generate a random 4-digit number
        String randomNumber = String.format("%04d", new Random().nextInt(10000));

        // Combine the full name (without spaces) and random number to form the username
        return fullNameWithoutSpace + randomNumber;
    }

    public Collection<PersonResponseDTO> allUsers() {
        Collection<Person> people = personRepository.findAll();
        Collection<PersonResponseDTO> personResponseDTOS = new ArrayList<>();

        for (Person person : people){
            PersonResponseDTO createdPersonDTO = new PersonResponseDTO();
            createdPersonDTO.setId(person.getId());
            createdPersonDTO.setUsername(person.getUsername());
            createdPersonDTO.setRole(person.getRole().name());
            createdPersonDTO.setEmail(person.getEmail());
            createdPersonDTO.setPhoneNumber(person.getPhoneNumber());
            createdPersonDTO.setBirthDate(person.getBirthDate());
            createdPersonDTO.setFullName(person.getFullName());
            createdPersonDTO.setSpecialite(person.getSpecialite());
            personResponseDTOS.add(createdPersonDTO);
        }
        return personResponseDTOS;
    }

    public PersonResponseDTO editPerson(Long id, PersonDTO personDTO) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        //If the personDTO is empty we will throw an exception
        if (optionalPerson.isEmpty()) {
            throw new EntityNotFoundException("Person with ID " + id + " not found.");
        }

        Person existingPerson = optionalPerson.get();
        // Update the person with the new data
        existingPerson.setFullName(personDTO.getFullName());
        existingPerson.setRole(PersonRole.valueOf(personDTO.getRole()));
        existingPerson.setPassword(personDTO.getPassword());
        existingPerson.setEmail(personDTO.getEmail());
        existingPerson.setPhoneNumber(personDTO.getPhoneNumber());
        existingPerson.setBirthDate(personDTO.getBirthDate());


        Person updatedPerson = personRepository.save(existingPerson);

        // Create a new PersonResponseDTO to give it back as a response
        PersonResponseDTO updatedPersonResponseDTO = new PersonResponseDTO();
        updatedPersonResponseDTO.setId(updatedPerson.getId());
        updatedPersonResponseDTO.setUsername(updatedPerson.getUsername());
        updatedPersonResponseDTO.setRole(updatedPerson.getRole().name());
        updatedPersonResponseDTO.setEmail(updatedPerson.getEmail());
        updatedPersonResponseDTO.setPhoneNumber(updatedPerson.getPhoneNumber());
        updatedPersonResponseDTO.setBirthDate(updatedPerson.getBirthDate());
        updatedPersonResponseDTO.setFullName(updatedPerson.getFullName());

        return updatedPersonResponseDTO;
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public PersonResponseDTO getUserById(Long id) {
        Person person = personRepository.findById(id).get();
        PersonResponseDTO updatedPersonResponseDTO = new PersonResponseDTO();
        updatedPersonResponseDTO.setId(person.getId());
        updatedPersonResponseDTO.setUsername(person.getUsername());
        updatedPersonResponseDTO.setRole(person.getRole().name());
        updatedPersonResponseDTO.setEmail(person.getEmail());
        updatedPersonResponseDTO.setPhoneNumber(person.getPhoneNumber());
        updatedPersonResponseDTO.setBirthDate(person.getBirthDate());
        updatedPersonResponseDTO.setFullName(person.getFullName());
        updatedPersonResponseDTO.setSpecialite(person.getSpecialite());

        return updatedPersonResponseDTO;
    }

    public TechResponseDTO createTech(TechDTO techDTO) {
        Person person = new Person();
        person.setFullName(techDTO.getFullName());
        person.setPassword(techDTO.getPassword());
        person.setRole(PersonRole.valueOf(techDTO.getRole()));
        person.setEmail(techDTO.getEmail());
        person.setPhoneNumber(techDTO.getPhoneNumber());
        person.setBirthDate(techDTO.getBirthDate());
        person.setSpecialite(techDTO.getSpecialite());

        // Generate the username automatically based on fullName and a random 4-digit number
        String generatedUsername = generateUsername(techDTO.getFullName());
        person.setUsername(generatedUsername);

        Person savedPerson = personRepository.save(person);

        // Create a new PersonDTO and set the ID and generated username
        TechResponseDTO createdTechDTO = new TechResponseDTO();
        createdTechDTO.setId(savedPerson.getId());
        createdTechDTO.setUsername(savedPerson.getUsername());
        createdTechDTO.setRole(savedPerson.getRole().name());
        createdTechDTO.setEmail(savedPerson.getEmail());
        createdTechDTO.setPhoneNumber(savedPerson.getPhoneNumber());
        createdTechDTO.setBirthDate(savedPerson.getBirthDate());
        createdTechDTO.setFullName(savedPerson.getFullName());
        createdTechDTO.setSpecialite(savedPerson.getSpecialite().name());

        return createdTechDTO;
    }

    public TechResponseDTO editTech(Long id, TechDTO techDTO) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isEmpty()) {
            // Handle the case when the person with the given ID is not found
            // You can throw an exception or return an appropriate response
            throw new EntityNotFoundException("Person with ID " + id + " not found.");
        }

        Person existingPerson = optionalPerson.get();
        // Update the person with the new data
        existingPerson.setFullName(techDTO.getFullName());
        existingPerson.setRole(PersonRole.valueOf(techDTO.getRole()));
        existingPerson.setEmail(techDTO.getEmail());
        existingPerson.setPhoneNumber(techDTO.getPhoneNumber());
        existingPerson.setBirthDate(techDTO.getBirthDate());
        existingPerson.setSpecialite(techDTO.getSpecialite());


        Person updatedTech = personRepository.save(existingPerson);

        // Create a new PersonResponseDTO to give it back as a response
        TechResponseDTO updatedTechResponseDTO = new TechResponseDTO();
        updatedTechResponseDTO.setId(updatedTech.getId());
        updatedTechResponseDTO.setUsername(updatedTech.getUsername());
        updatedTechResponseDTO.setRole(updatedTech.getRole().name());
        updatedTechResponseDTO.setEmail(updatedTech.getEmail());
        updatedTechResponseDTO.setPhoneNumber(updatedTech.getPhoneNumber());
        updatedTechResponseDTO.setBirthDate(updatedTech.getBirthDate());
        updatedTechResponseDTO.setFullName(updatedTech.getFullName());
        updatedTechResponseDTO.setSpecialite(updatedTech.getSpecialite().name());

        return updatedTechResponseDTO;
    }

}
