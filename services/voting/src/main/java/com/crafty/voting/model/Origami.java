package com.crafty.voting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Origami {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String origamiId;
    private String name;
    private int votes;

    public String getOrigamiId() { return origamiId; }
    public void setOrigamiId(String origamiId) { this.origamiId = origamiId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }
}
