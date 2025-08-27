package com.crafty.voting.controller;

import com.crafty.voting.service.OrigamiService;
import com.crafty.voting.model.Origami;
import org.springframework.http.ResponseEntity;
import com.crafty.voting.repository.jpa.OrigamiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/origamis")
public class VotingController {

    @Autowired
    OrigamiRepository origamiRepository;

    @Autowired
    private OrigamiService origamiService;

    @GetMapping
    public List<Origami> getAllOrigamis() {
        return origamiRepository.findAll();
    }

    @GetMapping("/{origamiId}")
    public Origami getOrigami(@PathVariable String origamiId) {
        return origamiService.getOrigamiById(origamiId)
            .orElseThrow(() -> new RuntimeException("Origami Not Found"));
    }

    @GetMapping("/{origamiId}/votes")
    public ResponseEntity<Integer> getVotes(@PathVariable String origamiId) {
        try {
            int votes = origamiService.getVotes(origamiId);
            return ResponseEntity.ok(votes);
        } catch (Exception e) {
            // Log error and return a suitable error response if needed.
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public Origami addOrigami(@RequestBody Origami origami) {
        return origamiService.saveOrUpdateOrigami(origami);
    }

    @PostMapping("/{origamiId}/vote")
    public Origami voteForOrigami(@PathVariable String origamiId) {
        Origami origami = origamiService.getOrigamiById(origamiId)
            .orElseThrow(() -> new RuntimeException("Origami Not Found"));
        origami.setVotes(origami.getVotes() + 1);
        return origamiService.saveOrUpdateOrigami(origami);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getVotingServiceStatus() {
        try {
            // Check if we can access the database and synchronization is working
            long origamiCount = origamiRepository.count();
            if (origamiCount >= 0) { // Basic check that database is accessible
                return ResponseEntity.ok("{\"status\":\"up\",\"message\":\"Voting Service is Online\"}");
            } else {
                return ResponseEntity.status(503).body("{\"status\":\"down\",\"message\":\"Database not accessible\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(503).body("{\"status\":\"down\",\"message\":\"Service unavailable: " + e.getMessage() + "\"}");
        }
    }
}