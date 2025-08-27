package com.crafty.voting.service;

import com.crafty.voting.config.AppProperties;
import com.crafty.voting.model.Origami;
import com.crafty.voting.model.CatalogueProductDTO;
import com.crafty.voting.repository.jpa.OrigamiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrigamiSynchronizationService {

    private static final Logger log = LoggerFactory.getLogger(OrigamiSynchronizationService.class);

    @Autowired
    private OrigamiRepository origamiRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String catalogueServiceUrl;

    public OrigamiSynchronizationService(OrigamiRepository origamiRepository, RestTemplate restTemplate, AppProperties appProperties) {
        this.origamiRepository = origamiRepository;
        this.restTemplate = restTemplate;
        this.catalogueServiceUrl = appProperties.getServiceUrl();
    }

    @Scheduled(fixedRate = 60000) // 1 minute = 60000 ms
    public void synchronizeOrigamis() {
    try {
        List<CatalogueProductDTO> catalogueProducts = fetchOrigamisFromCatalogueService();
        for (CatalogueProductDTO product : catalogueProducts) {
            if (product.getId() != null) { // Check if ID is not null
                Optional<Origami> existingOrigami = origamiRepository.findById(product.getId());
                if (!existingOrigami.isPresent()) {
                    Origami newOrigami = new Origami();
                    newOrigami.setOrigamiId(product.getId());
                    newOrigami.setName(product.getName());
                    newOrigami.setVotes(0); // Initialize with 0 votes
                    origamiRepository.save(newOrigami);
                    log.info("Added new origami: {} with ID: {}", product.getName(), product.getId());
                } else {
                    Origami updatedOrigami = existingOrigami.get();
                    updatedOrigami.setName(product.getName());
                    // Preserve existing votes
                    origamiRepository.save(updatedOrigami);
                    log.info("Updated existing origami: {} with ID: {}", product.getName(), product.getId());
                }
            } else {
                log.warn("Skipped product with null ID");
            }
        }
    } catch (Exception e) {
        log.error("Error during synchronization of origamis: " + e.getMessage(), e);
    }
    }




    private List<CatalogueProductDTO> fetchOrigamisFromCatalogueService() {
    try {
        CatalogueProductDTO[] productsArray = restTemplate.getForObject(catalogueServiceUrl, CatalogueProductDTO[].class);
        List<CatalogueProductDTO> products = Arrays.asList(productsArray);
        products.forEach(product -> log.info("Fetched product with ID: {} and name: {}", product.getId(), product.getName()));
        return products;
    } catch (RestClientException e) {
        log.error("Failed to fetch origamis from catalogue service: " + e.getMessage(), e);
        return Collections.emptyList();
    }
    }

}
