package com.ironlady.ops.service;

import com.ironlady.ops.entity.Attendance;
import com.ironlady.ops.entity.Participants;
import com.ironlady.ops.repository.AttendanceRepository;
import com.ironlady.ops.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ParticipantRepository participantRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             ParticipantRepository participantRepository) {
        this.attendanceRepository = attendanceRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional(readOnly = true)
    public List<Long> getPresentParticipantIds(LocalDate date) {
        try {
            return attendanceRepository.findByDate(date).stream()
                    .map(a -> {
                        Participants p = a.getParticipant();
                        return p != null ? p.getId() : null;
                    })
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching attendance for date " + date + ": " + e.getMessage(), e);
        }
    }

    @Transactional
    public void saveAttendance(LocalDate date, List<Long> participantIds) {
        try {
            System.out.println("AttendanceService.saveAttendance called with date: " + date + ", participantIds: " + participantIds);
            
            if (date == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }
            
            if (participantIds == null) {
                participantIds = List.of(); // Handle null as empty list
            }

            // Clear existing records for that date
            System.out.println("Fetching existing attendance records for date: " + date);
            List<Attendance> existing = attendanceRepository.findByDate(date);
            System.out.println("Found " + existing.size() + " existing records");
            
            if (!existing.isEmpty()) {
                System.out.println("Deleting existing records...");
                attendanceRepository.deleteAll(existing);
                attendanceRepository.flush(); // Ensure delete is committed before insert
                System.out.println("Existing records deleted");
            }

            // Insert new ones
            if (!participantIds.isEmpty()) {
                System.out.println("Creating " + participantIds.size() + " new attendance records");
                List<Attendance> records = new java.util.ArrayList<>();
                for (Object idObj : participantIds) {
                    Long id;
                    // Handle different number types (Integer, Long, etc.)
                    if (idObj instanceof Number) {
                        id = ((Number) idObj).longValue();
                    } else if (idObj instanceof String) {
                        try {
                            id = Long.parseLong((String) idObj);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid participant ID format: " + idObj);
                            continue;
                        }
                    } else {
                        System.err.println("Unexpected participant ID type: " + idObj.getClass().getName() + ", value: " + idObj);
                        continue;
                    }
                    
                    if (id == null || id <= 0) {
                        System.out.println("Skipping invalid participant ID: " + id);
                        continue;
                    }
                    
                    System.out.println("Looking up participant with ID: " + id + " (type: " + id.getClass().getName() + ")");
                    Participants p = participantRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Participant not found for id " + id));
                    
                    System.out.println("Found participant: " + p.getName() + " (ID: " + p.getId() + ")");
                    
                    Attendance attendance = new Attendance(date, p);
                    records.add(attendance);
                }

                if (!records.isEmpty()) {
                    System.out.println("Saving " + records.size() + " attendance records...");
                    attendanceRepository.saveAll(records);
                    attendanceRepository.flush(); // Force immediate database write
                    System.out.println("Attendance records saved successfully");
                } else {
                    System.out.println("No valid records to save after filtering");
                }
            } else {
                System.out.println("No participant IDs provided, only clearing existing records");
            }
        } catch (Exception e) {
            System.err.println("Exception in saveAttendance: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving attendance for date " + date + ": " + e.getMessage(), e);
        }
    }
}

