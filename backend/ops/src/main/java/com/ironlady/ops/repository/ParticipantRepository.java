package com.ironlady.ops.repository;

import com.ironlady.ops.entity.Participants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participants, Long> {
}
