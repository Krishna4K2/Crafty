package com.crafty.voting.repository.mongo;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.crafty.voting.model.Origami;

@Profile("mongo")
public interface OrigamiMongoRepository extends MongoRepository<Origami, String> {}
