package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.FarmerFeedback;
import com.agrosmart.backend.repositories.FarmerFeedbackRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FarmerFeedbackRepository feedbackRepository;

    public FeedbackController(FarmerFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping
    public List<FarmerFeedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderBySubmittedAtDesc();
    }

    @PostMapping
    public ResponseEntity<FarmerFeedback> submitFeedback(@RequestBody FarmerFeedback feedback) {
        if (feedback.getId() == null || feedback.getId().trim().isEmpty()) {
            feedback.setId("FB-" + System.currentTimeMillis());
        }
        if (feedback.getSubmittedAt() == null || feedback.getSubmittedAt().trim().isEmpty()) {
            feedback.setSubmittedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        }
        if (feedback.getRating() == null) {
            feedback.setRating(5);
        }

        FarmerFeedback saved = feedbackRepository.save(feedback);
        return ResponseEntity.ok(saved);
    }
}
