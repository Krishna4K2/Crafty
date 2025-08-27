package com.crafty.voting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.crafty.voting.model.Origami;
import com.crafty.voting.repository.jpa.OrigamiRepository;
import com.crafty.voting.repository.mongo.OrigamiMongoRepository;

import java.util.Optional;

@Service
public class OrigamiService {

    @Autowired
    private OrigamiRepository origamiRepository;

    @Autowired
    private OrigamiMongoRepository origamiMongoRepository;

    @Autowired
    private Environment env;

    public Optional<Origami> getOrigamiById(String id) {
        if (isMongoProfile()) {
            return origamiMongoRepository.findById(id);
        } else {
            try {
                Long longId = Long.valueOf(id);
                return origamiRepository.findById(longId);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
    }

    public Origami saveOrUpdateOrigami(Origami origami) {
        if (isMongoProfile()) {
            return origamiMongoRepository.save(origami);
        } else {
            return origamiRepository.save(origami);
        }
    }

    public int getVotes(String origamiId) {
        Optional<Origami> origamiOpt = getOrigamiById(origamiId);
        return origamiOpt.map(Origami::getVotes).orElse(0);
    }

    private boolean isMongoProfile() {
        String[] profiles = env.getActiveProfiles();
        for (String profile : profiles) {
            if ("mongo".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
}