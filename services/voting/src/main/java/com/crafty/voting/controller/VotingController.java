package com.crafty.voting.controller;

import com.crafty.voting.service.OrigamiService;
import com.crafty.voting.model.Origami;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.crafty.voting.repository.jpa.OrigamiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Origami> getOrigami(@PathVariable String origamiId) {
        try {
            return origamiService.getOrigamiById(origamiId)
                .map(origami -> ResponseEntity.ok(origami))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    public ResponseEntity<Origami> addOrigami(@RequestBody Origami origami) {
        try {
            Origami savedOrigami = origamiService.saveOrUpdateOrigami(origami);
            return ResponseEntity.ok(savedOrigami);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{origamiId}/vote")
    public ResponseEntity<Origami> voteForOrigami(@PathVariable String origamiId) {
        try {
            Origami origami = origamiService.getOrigamiById(origamiId)
                .orElseThrow(() -> new RuntimeException("Origami Not Found"));
            origami.setVotes(origami.getVotes() + 1);
            Origami updatedOrigami = origamiService.saveOrUpdateOrigami(origami);
            return ResponseEntity.ok(updatedOrigami);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Origami Not Found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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