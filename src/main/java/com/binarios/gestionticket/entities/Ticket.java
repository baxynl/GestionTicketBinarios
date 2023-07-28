package com.binarios.gestionticket.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;


    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private TicketStatus status;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Person client; // The User who created the ticket

    @ManyToOne
    @JoinColumn(name = "assigned_tech_id")
    private Person assignedTech; // The User representing the tech assigned to the ticket

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Person admin; // The User representing the admin overseeing the ticket


    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id")
    private List<Attachment> attachments;

}