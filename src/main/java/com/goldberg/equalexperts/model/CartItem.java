package com.goldberg.equalexperts.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItem {
    private String productName;

    private Integer quantity;

    private BigDecimal productPrice;

    public CartItem(String productName, Integer quantity, BigDecimal productPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.productPrice = productPrice;
    }
}
