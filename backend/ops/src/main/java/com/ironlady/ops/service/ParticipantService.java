package com.ironlady.ops.service;

import com.ironlady.ops.entity.Participants;
import com.ironlady.ops.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository repo;

    public ParticipantService(ParticipantRepository repo) {
        this.repo = repo;
    }

    public Participants add(Participants p) {
        return repo.save(p);
    }

    public List<Participants> getAll() {
        return repo.findAll();
    }

    public Participants getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant not found"));
    }       

    public Participants update(Long id, Participants p) {
        Participants existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        existing.setName(p.getName());
        existing.setEmail(p.getEmail());
        existing.setPhone(p.getPhone());
        existing.setProgramName(p.getProgramName());
        existing.setCohortNumber(p.getCohortNumber());
        existing.setPaymentStatus(p.getPaymentStatus());
        existing.setStatus(p.getStatus());
        existing.setNotes(p.getNotes());

        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
