package com.goldberg.equalexperts.service;

import com.goldberg.equalexperts.model.Cart;
import com.goldberg.equalexperts.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This class is responsible for managing the shopping cart.
 */
@Service
public class ShoppingCart {

    @Autowired
    Price priceService;

    private static final String TAX_EXEMPTED_PRODUCT = "shreddies";

    private BigDecimal taxRate;

    public ShoppingCart(@Value("${shopping.cart.taxRate}") BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /**
     * Products will be added or the quantity updated.
     * The sub-total price, tax and total price will also be calculated.
     */
    public void addProductToCart(Cart cart, String productName, Integer quantity) {
        if (validateInput(cart, productName, quantity)) {
            BigDecimal productPrice = retrieveProductPrice(productName);
            addProductToCart(cart, productName, quantity, productPrice);
            retrieveCartState(cart);
        }
    }

    private boolean validateInput(Cart cart, String productName, Integer quantity) {
        cart.setCartErrors("");
        if (taxRate == null) {
            cart.setCartErrors("Tax rate could not be retrieved. Cart could not be updated.");
            return false;

        } else if (quantity == 0) {
            cart.setCartErrors("Quantity of product can not be zero. Cart could not be updated.");
            return false;
        }
        return true;
    }

    //This could be updated to use a map instead of a ArrayList
    private void addProductToCart(Cart cart, String productName, Integer quantity, BigDecimal price) {
        boolean found = false;

        for (CartItem item : cart.getProductList()) {
            if (item.getProductName().equals(productName) &&
                    item.getProductPrice().compareTo(price) == 0) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            cart.getProductList().add(new CartItem(productName, quantity, price));
        }
    }

    /**
     * Retrieves the product price from the PriceService api
     */
    private BigDecimal retrieveProductPrice(String productName) {
        return priceService.retrieveProductPrice(productName);
    }

    /**
     * This method will calculate the subtotal, tax and total values of the cart.
     * It will also round the values.
     */
    private void retrieveCartState(Cart cart) {
        BigDecimal subTotal = calculateCartSubTotal(cart);
        //BigDecimal tax = calculateTaxPayable(subTotal);
        BigDecimal tax = calculateTaxPayable(cart.getProductList());
        BigDecimal total = calculateTotalPayable(subTotal, tax);
        cart.setSubTotal(roundValue(subTotal, 2, RoundingMode.UP));
        cart.setTax(roundValue(tax, 2, RoundingMode.UP));
        cart.setTotal(roundValue(total, 2, RoundingMode.UP));
    }

    private BigDecimal calculateTaxPayable(List<CartItem> productList) {
        BigDecimal taxableValue = BigDecimal.ZERO;

        ListIterator<CartItem> cartItemListIterator = productList.listIterator();
        while (cartItemListIterator.hasNext()) {
            CartItem cartItem = cartItemListIterator.next();
            if (!cartItem.getProductName().equals(TAX_EXEMPTED_PRODUCT)) {
                taxableValue = taxableValue.add((cartItem.getProductPrice().
                        multiply(BigDecimal.valueOf(cartItem.getQuantity()))));
            }
        }
        return taxableValue.multiply(taxRate);
    }

    /**
     * The subtotal of the cart will be calculated.  If the price of an item is 0 it will be removed from the cart.
     */
    private BigDecimal calculateCartSubTotal(Cart cart) {
        BigDecimal subTotal = BigDecimal.ZERO;
        Iterator<CartItem> iterator = cart.getProductList().iterator();

        while (iterator.hasNext()) {
            CartItem cartItem = iterator.next();
            if (cartItem.getProductPrice().equals(BigDecimal.ZERO)) {
                cart.setCartErrors("Due to an error " + cartItem.getProductName() + " was removed from the cart.");
                iterator.remove();
            } else {
                subTotal = subTotal.add(
                        BigDecimal.valueOf(cartItem.getQuantity()).multiply(cartItem.getProductPrice())
                );
            }
        }

        return subTotal;
    }

    private BigDecimal calculateTaxPayable(BigDecimal subTotal) {


        return subTotal.multiply(taxRate);
    }

    private BigDecimal calculateTotalPayable(BigDecimal subTotal, BigDecimal tax) {
        return subTotal.add(tax);
    }

    /**
     * Safe rounding
     */
    private BigDecimal roundValue(BigDecimal value, int scale, RoundingMode roundingMode) {
        BigDecimal defaultValue = BigDecimal.ZERO;
        if (value == null) {
            value = defaultValue;
        }
        return value.setScale(scale, roundingMode);
    }

    public void removeItem(Cart cart, String productName) {

        List<CartItem> productList = cart.getProductList();
        Iterator<CartItem> iterator = productList.iterator();

        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProductName().equals(productName)) {
                if (item.getQuantity() == 1) {
                    iterator.remove();
                } else {
                    item.setQuantity(item.getQuantity() - 1);
                }
                break;
            }
        }
    }
}
