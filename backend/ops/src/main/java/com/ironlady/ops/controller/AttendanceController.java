package com.ironlady.ops.controller;

import com.ironlady.ops.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/{date}")
    public ResponseEntity<List<Long>> getAttendanceForDate(@PathVariable String date) {
        try {
            System.out.println("Fetching attendance for date: " + date);
            LocalDate parsed = LocalDate.parse(date);
            System.out.println("Parsed date: " + parsed);
            List<Long> ids = attendanceService.getPresentParticipantIds(parsed);
            System.out.println("Found " + ids.size() + " present participants");
            return ResponseEntity.ok(ids);
        } catch (Exception e) {
            System.err.println("Error fetching attendance for date " + date + ": " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error instead of crashing
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/{date}")
    public ResponseEntity<String> saveAttendanceForDate(@PathVariable String date,
                                                         @RequestBody List<Long> participantIds) {
        try {
            System.out.println("=== Attendance Save Request ===");
            System.out.println("Date string received: " + date);
            System.out.println("Participant IDs received: " + participantIds);
            System.out.println("Participant IDs type: " + (participantIds != null ? participantIds.getClass().getName() : "null"));
            
            if (participantIds == null) {
                System.out.println("Warning: participantIds is null, treating as empty list");
                participantIds = List.of();
            }
            
            LocalDate parsed;
            try {
                parsed = LocalDate.parse(date);
                System.out.println("Parsed date successfully: " + parsed);
            } catch (Exception e) {
                System.err.println("Date parsing error: " + e.getMessage());
                return ResponseEntity.status(400)
                        .body("Invalid date format: " + date + ". Expected format: yyyy-MM-dd");
            }
            
            attendanceService.saveAttendance(parsed, participantIds);
            
            System.out.println("Attendance saved successfully for " + date);
            return ResponseEntity.ok("Attendance saved successfully for " + date);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error saving attendance: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            e.printStackTrace();
            String errorMsg = "Error saving attendance: " + e.getMessage();
            if (e.getCause() != null) {
                errorMsg += " - Cause: " + e.getCause().getMessage();
            }
            return ResponseEntity.status(500).body(errorMsg);
        }
    }
}

