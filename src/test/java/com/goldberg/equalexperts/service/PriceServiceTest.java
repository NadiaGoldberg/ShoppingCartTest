package com.goldberg.equalexperts.service;

import com.goldberg.equalexperts.dto.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private Price priceService;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void retrieveProductPrice() {
        ProductDTO product = new ProductDTO();
        product.setPrice(BigDecimal.TEN);
        when(responseSpec.bodyToMono(ProductDTO.class)).thenReturn(Mono.just(product));

        BigDecimal result = priceService.retrieveProductPrice("cornflakes");
        assertEquals(BigDecimal.TEN, result);
    }

    @Test
    void retrieveProductPriceZero() {
        when(responseSpec.bodyToMono(ProductDTO.class)).thenThrow(new RuntimeException("API failure"));

        BigDecimal result = priceService.retrieveProductPrice("wrong");
        assertEquals(BigDecimal.ZERO, result);
    }
}
