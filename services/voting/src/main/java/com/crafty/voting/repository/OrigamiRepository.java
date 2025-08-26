package com.crafty.voting.repository;

import com.crafty.voting.model.Origami;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrigamiRepository extends JpaRepository<Origami, Long> {
  int countByOrigamiId(Long origamiId);
}
