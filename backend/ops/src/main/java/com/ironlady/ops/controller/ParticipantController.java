package com.ironlady.ops.controller;

import com.ironlady.ops.entity.Participants;
import com.ironlady.ops.service.ParticipantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    private final ParticipantService service;

    public ParticipantController(ParticipantService service) {
        this.service = service;
    }

    @PostMapping
    public Participants create(@RequestBody Participants p) {
        return service.add(p);
    }

    @GetMapping("/{id}")
    public Participants getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<Participants> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public Participants update(@PathVariable Long id, @RequestBody Participants p) {
        return service.update(id, p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
