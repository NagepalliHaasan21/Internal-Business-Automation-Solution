package com.ironlady.ops.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participants participant;

    public Attendance() {}

    public Attendance(LocalDate date, Participants participant) {
        this.date = date;
        this.participant = participant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Participants getParticipant() {
        return participant;
    }

    public void setParticipant(Participants participant) {
        this.participant = participant;
    }
}

