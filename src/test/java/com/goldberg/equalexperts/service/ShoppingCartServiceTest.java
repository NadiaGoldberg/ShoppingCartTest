package com.goldberg.equalexperts.service;

import com.goldberg.equalexperts.model.Cart;
import com.goldberg.equalexperts.model.CartItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = "shopping.cart.taxRate=0.125")

public class ShoppingCartServiceTest {

    private static final String WRONG_PRODUCT = "Wrong Product";
    private static final String CORNFLAKES = "cornflakes";
    private static final String WEETABIX = "weetabix";
    private static final String CHEERIOS = "cheerios";
    private static final String SHREDDIES = "shreddies";
    private static final BigDecimal CORNFLAKES_PRICE = BigDecimal.valueOf(2.52);
    private static final BigDecimal WEETABIX_PRICE = BigDecimal.valueOf(9.98);
    private static final BigDecimal CHEERIOS_PRICE = BigDecimal.valueOf(11);
    private static final BigDecimal SHREDDIES_PRICE = BigDecimal.valueOf(15);
    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.125);

    @Mock
    private Price priceService;

    @InjectMocks
    private ShoppingCart shoppingCart;

    private AutoCloseable mocked;
    private Cart cart = new Cart();


    @BeforeEach
    void setUp() {
        cart.setProductList(new ArrayList<>());
        shoppingCart = new ShoppingCart(TAX_RATE);
        mocked = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMocks() throws Throwable {
        mocked.close();
    }


    @Test
    void addTaxExemptedProductToCartTest() {
        String product1  = SHREDDIES;
        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(SHREDDIES_PRICE);

        shoppingCart.addProductToCart(cart, product1, 1);

        assertEquals(roundValue(BigDecimal.ZERO, 2, RoundingMode.UP), cart.getTax());
        assertEquals(roundValue(BigDecimal.valueOf(15),2, RoundingMode.UP), cart.getSubTotal());
        assertEquals(roundValue( BigDecimal.valueOf(15),2, RoundingMode.UP), cart.getTotal());
    }

    @Test
    void addTaxExcemptedProductToCartPlusNotExemptedProductTest() {

        String product1  = SHREDDIES;
        String product2  = CHEERIOS;
        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(SHREDDIES_PRICE);
        shoppingCart.addProductToCart(cart, product1, 1);

        Mockito.when(priceService.retrieveProductPrice(product2)).thenReturn(CHEERIOS_PRICE);
        shoppingCart.addProductToCart(cart, product2, 1);

        assertEquals(cart.getTax(), roundValue(BigDecimal.valueOf(1.38), 2, RoundingMode.UP));
        assertEquals(roundValue(BigDecimal.valueOf(26),2, RoundingMode.UP), cart.getSubTotal());
        assertEquals(roundValue( BigDecimal.valueOf(27.38),2, RoundingMode.UP), cart.getTotal());

    }

    @Test
    void addSingleProductTest() {
        String product1 = CORNFLAKES;
        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(CORNFLAKES_PRICE);

        shoppingCart.addProductToCart(cart, product1, 1);

        assertEquals(cart.getTax(), roundValue(BigDecimal.valueOf(0.32), 2, RoundingMode.UP));
        assertEquals(cart.getSubTotal(), roundValue(BigDecimal.valueOf(2.52), 2, RoundingMode.UP));
        assertEquals(cart.getTotal(), roundValue(BigDecimal.valueOf(2.84), 2, RoundingMode.UP));

    }

    @Test
    void addSingleProductQuantityTest() {
        String product1 = CORNFLAKES;
        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(CORNFLAKES_PRICE);

        shoppingCart.addProductToCart(cart, product1, 2);

        assertEquals(cart.getTax(), roundValue(BigDecimal.valueOf(0.63), 2, RoundingMode.UP));
        assertEquals(cart.getSubTotal(), roundValue(BigDecimal.valueOf(5.04), 2, RoundingMode.UP));
        assertEquals(cart.getTotal(), roundValue(BigDecimal.valueOf(5.67), 2, RoundingMode.UP));
    }

    @Test
    void addMultipleDifferentProductsTest() {
        String product1 = CORNFLAKES;
        String product2 = WEETABIX;

        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(CORNFLAKES_PRICE);

        shoppingCart.addProductToCart(cart, product1, 1);

        Mockito.when(priceService.retrieveProductPrice(product2)).thenReturn(WEETABIX_PRICE);
        shoppingCart.addProductToCart(cart, product2, 1);

        assertEquals(cart.getTax(), roundValue(BigDecimal.valueOf(1.57), 2, RoundingMode.UP));
        assertEquals(cart.getSubTotal(), roundValue(BigDecimal.valueOf(12.5), 2, RoundingMode.UP));
        assertEquals(cart.getTotal(), roundValue(BigDecimal.valueOf(14.07), 2, RoundingMode.UP));

    }

    @Test
    void addMultipleSameProductsTest() {

        String product1 = CORNFLAKES;
        String product2 = CORNFLAKES;
        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(CORNFLAKES_PRICE);
        shoppingCart.addProductToCart(cart, product1, 1);

        Mockito.when(priceService.retrieveProductPrice(product2)).thenReturn(CORNFLAKES_PRICE);
        shoppingCart.addProductToCart(cart, product2, 1);

        assertEquals(cart.getProductList().size(), 1);

        assertEquals(cart.getTax(), roundValue(BigDecimal.valueOf(0.63), 2, RoundingMode.UP));
        assertEquals(cart.getSubTotal(), roundValue(BigDecimal.valueOf(5.04), 2, RoundingMode.UP));
        assertEquals(cart.getTotal(), roundValue(BigDecimal.valueOf(5.67), 2, RoundingMode.UP));

    }

    @Test
    void addEmptyProductTest() {

        String product1 = "";

        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(BigDecimal.ZERO);
        shoppingCart.addProductToCart(cart, product1, 1);

        assertTrue(cart.getProductList().isEmpty());
        assertEquals(cart.getCartErrors(), "Due to an error  was removed from the cart.");
        assertEquals(cart.getTax(), roundValue(BigDecimal.valueOf(0), 2, RoundingMode.UP));
        assertEquals(cart.getSubTotal(), roundValue(BigDecimal.valueOf(0), 2, RoundingMode.UP));
        assertEquals(cart.getTotal(), roundValue(BigDecimal.valueOf(0), 2, RoundingMode.UP));
    }

    @Test
    void testAddProductWithZeroQuanity() {

        shoppingCart.addProductToCart(cart, CORNFLAKES, 0);

        assertTrue(cart.getProductList().isEmpty());
    }

    @Test
    void addProductWithNoPriceTest() {

        Mockito.when(priceService.retrieveProductPrice(WRONG_PRODUCT)).thenReturn(BigDecimal.ZERO);

        shoppingCart.addProductToCart(cart, WRONG_PRODUCT, 1);

        assertEquals(cart.getCartErrors(), "Due to an error " + WRONG_PRODUCT + " was removed from the cart.");

        assertEquals(cart.getTax(), roundValue(BigDecimal.valueOf(0), 2, RoundingMode.UP));
        assertEquals(cart.getSubTotal(), roundValue(BigDecimal.valueOf(0), 2, RoundingMode.UP));
        assertEquals(cart.getTotal(), roundValue(BigDecimal.valueOf(0), 2, RoundingMode.UP));

    }

    @Test
    void TestCase1() {

        String product1 = CORNFLAKES;
        String product2 = CORNFLAKES;
        String product3 = WEETABIX;

        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(CORNFLAKES_PRICE);
        shoppingCart.addProductToCart(cart, product1, 1);

        Mockito.when(priceService.retrieveProductPrice(product2)).thenReturn(CORNFLAKES_PRICE);
        shoppingCart.addProductToCart(cart, product2, 1);

        Mockito.when(priceService.retrieveProductPrice(product3)).thenReturn(WEETABIX_PRICE);
        shoppingCart.addProductToCart(cart, product3, 1);

        assertEquals(cart.getProductList().size(), 2);

        assertEquals(cart.getTax(), roundValue(BigDecimal.valueOf(1.88), 2, RoundingMode.UP));
        assertEquals(cart.getSubTotal(), roundValue(BigDecimal.valueOf(15.02), 2, RoundingMode.UP));
        assertEquals(cart.getTotal(), roundValue(BigDecimal.valueOf(16.90), 2, RoundingMode.UP));

    }

    @Test
    //Add different 3 products with 1 quantity each and will remove one of the products.
    void removeProductFromCart() {

        String product1 = WEETABIX;
        String product2 = CORNFLAKES;
        String product3 = CHEERIOS;

        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(CORNFLAKES_PRICE);
        shoppingCart.addProductToCart(cart, product1, 1);

        Mockito.when(priceService.retrieveProductPrice(product2)).thenReturn(CORNFLAKES_PRICE);
        shoppingCart.addProductToCart(cart, product2, 1);

        Mockito.when(priceService.retrieveProductPrice(product3)).thenReturn(CHEERIOS_PRICE);
        shoppingCart.addProductToCart(cart, product3, 1);

        assertEquals(cart.getProductList().size(), 3);

        shoppingCart.removeItem(cart, product3);

        assertEquals(cart.getProductList().size(),2);

        List<CartItem> productList = cart.getProductList();
        Iterator<CartItem> iterator = productList.iterator();

        boolean found = false;
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProductName().equals(product3)) {
                found = true;
                break;
            }
        }
        assertEquals(found, false);

    }

    @Test
    // Adding 3 of the same products and removing one item.
    void removeProductsFromCart() {

        String product1 = CHEERIOS;

        Mockito.when(priceService.retrieveProductPrice(product1)).thenReturn(CORNFLAKES_PRICE);
        shoppingCart.addProductToCart(cart, product1, 3);

        shoppingCart.removeItem(cart, product1);

        List<CartItem> productList = cart.getProductList();
        Iterator<CartItem> iterator = productList.iterator();

        int qty = 0;
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProductName().equals(product1)) {
                qty  = item.getQuantity();
                break;
            }
        }
        assertEquals(2, qty);

    }

    private BigDecimal roundValue(BigDecimal value, int scale, RoundingMode roundingMode) {
        BigDecimal defaultValue = BigDecimal.ZERO;
        if (value == null) {
            value = defaultValue;
        }
        return value.setScale(scale, roundingMode);
    }
}


