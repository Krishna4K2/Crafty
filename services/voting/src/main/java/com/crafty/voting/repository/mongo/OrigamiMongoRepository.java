package com.crafty.voting.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.crafty.voting.model.Origami;

public interface OrigamiMongoRepository extends MongoRepository<Origami, String> {}
