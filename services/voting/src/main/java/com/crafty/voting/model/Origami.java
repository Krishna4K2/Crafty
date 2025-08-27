package com.crafty.voting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Origami {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("origamiId")
    private Long origamiId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("votes")
    private int votes;

    public Long getOrigamiId() { return origamiId; }
    public void setOrigamiId(Long origamiId) { this.origamiId = origamiId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }
}
