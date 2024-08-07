package com.goldberg.equalexperts.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Cart {

    private List<CartItem> productList;

    private BigDecimal subTotal;

    private BigDecimal tax;

    private BigDecimal total;

    private String cartErrors = "";
}
