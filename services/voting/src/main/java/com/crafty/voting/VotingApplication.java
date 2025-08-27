package com.crafty.voting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableScheduling
public class VotingApplication {
    public static void main(String[] args) {
        SpringApplication.run(VotingApplication.class, args);
    }
}

@Configuration
@Profile({"h2", "default"})
@EnableJpaRepositories(basePackages = "com.crafty.voting.repository.jpa")
class JpaRepositoriesConfig {}

@Configuration
@Profile("mongo")
@EnableMongoRepositories(basePackages = "com.crafty.voting.repository.mongo")
class MongoRepositoriesConfig {}