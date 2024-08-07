package com.goldberg.equalexperts.service;

import com.goldberg.equalexperts.dto.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class Price {

    private static final Logger log = LoggerFactory.getLogger(Price.class);

    @Autowired
    private WebClient webClient;

    /**
     * The price of the product will be returned from the price API.
     */
    public BigDecimal retrieveProductPrice(String productName) {
        try {
            Mono<BigDecimal> monoPrice = webClient.get()
                    .uri("/backend-take-home-test-data/{product}.json", productName)
                    .retrieve() // Initiate the request
                    .bodyToMono(ProductDTO.class) // Deserialize the body to ProductDetails class
                    .map(ProductDTO::getPrice); //

            return monoPrice.block(Duration.ofSeconds(1));
        } catch (Exception e) {
            log.warn("Price for " + productName + " not be retrieved from the API.");
            log.error(e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
