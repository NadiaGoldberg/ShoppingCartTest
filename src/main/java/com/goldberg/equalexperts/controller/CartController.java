package com.goldberg.equalexperts.controller;

import com.goldberg.equalexperts.dto.ProductRequest;
import com.goldberg.equalexperts.error.EqualExpertsError;
import com.goldberg.equalexperts.model.Cart;
import com.goldberg.equalexperts.service.ShoppingCart;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * The cart controller is responsible for managing the cart.
 * This will include updating the cart and calculating the totals.
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    @Autowired
    private ShoppingCart shoppingCart;

    /**
     * This method will be responsible for adding selected products to a cart and calculating
     * the totals of the cart.
     *
     * @param session        the current session of the cart
     * @param productRequest the product details to be added to the cart
     * @param forceEmptyCart the indicator will forcefully clear the cart even if it is still in the same state
     * @return Returns the shopping cart or the relevant error
     */
    @GetMapping("/addProduct")

    public ResponseEntity<?> addProduct(HttpSession session, @RequestBody ProductRequest productRequest,
                                        @RequestHeader(value = "ForceEmptyCart", defaultValue = "false") boolean forceEmptyCart) {

        try {
            //Retrieves the cart from the current session or force clear the cart.
            //This could be better implemented with a JWT token.
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null || forceEmptyCart) {
                cart = new Cart();
                cart.setProductList(new ArrayList<>());
                session.setAttribute("cart", cart);
            }

            shoppingCart.addProductToCart(cart, productRequest.getProductName(), productRequest.getQuantity());

            log.info("addProductToCart: Successful");
            return ResponseEntity.ok(cart);

        } catch (IllegalArgumentException iae) {
            EqualExpertsError error = new EqualExpertsError(HttpStatus.BAD_REQUEST, iae.getMessage(), "Check the product details or quantity");
            log.info("addProductToCart: Error {}", iae.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            EqualExpertsError error = new EqualExpertsError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
            log.info("addProductToCart: Error {}", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
