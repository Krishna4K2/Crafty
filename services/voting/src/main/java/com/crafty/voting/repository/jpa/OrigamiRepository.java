package com.crafty.voting.repository.jpa;

import com.crafty.voting.model.Origami;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrigamiRepository extends JpaRepository<Origami, Long> {
}
