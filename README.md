# Curl

curl --location --request GET 'http://localhost:9000/cart/addProduct' \
--header 'ForceEmptyCart: false' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=11DCA8EA496933D0FC092511649EED17' \
--data '{    
"productName": "cornflakes",
"quantity": 1
}'

# Code notes

* I saved the vat value in the properties file.
  Ideally the VAT should be retrieved based on the location of the client.
  The VAT values could also be cached.

* The error handling is very basic. This should be enhanced for a better client experience.

* System logging should be added as per IT specifications.

* For this solution I will just pass the Product name to be added to the cart.
  Each product should contain details about itself including price. When this product is added to the cart the price
  must be included.  
  This will keep you from calling to the price API with every product in the cart if you could already have the data
  available from a previous call to the backend
  If there is any issue with the price retrieval process, the product should then not be allowed to be added to the
  cart, instead of a exception on.

* I used a simple HTTP session keep state of the cart this could be done with a JWT token.

* Price API call can be made more configurable with properties as well.

## Warning: Please read these instructions carefully and entirely first

* Clone this repository to your local machine.
* Use your IDE of choice to complete the assignment.
* When you have completed the assignment, you need to push your code to this repository
  and [mark the assignment as completed by clicking here](https://app.snapcode.review/submission_links/fb8a2892-39df-43e6-8c14-b3b5b73699d2).
* Once you mark it as completed, your access to this repository will be revoked. Please make sure that you have
  completed the assignment and pushed all code from your local machine to this repository before you click the link.
* There is no time limit for this task - however, for guidance, it is expected to typically take around 1-2 hours.

# Begin the task

Write some code that provides the following basic shopping cart capabilities:

1. Add a product to the cart
    1. Specifying the product name and quantity
    2. Retrieve the product price by issuing a request to the [Price API](#price-api) specified below
    3. Cart state (totals, etc.) must be available

2. Calculate the state:
    1. Cart subtotal (sum of price for all items)
    2. Tax payable (charged at 12.5% on the subtotal)
    3. Total payable (subtotal + tax)
    4. Totals should be rounded up where required (to two decimal places)

## Price API

The price API is an existing API that returns the price details for a product, identified by it's name. The shopping
cart should integrate with the price API to retrieve product prices.

### Price API Service Details

Base URL: `https://equalexperts.github.io/`

View Product: `GET /backend-take-home-test-data/{product}.json`

List of available products

* `cheerios`
* `cornflakes`
* `frosties`
* `shreddies`
* `weetabix`

## Example

The below is a sample with the correct values you can use to confirm your calculations

### Inputs

* Add 1 × cornflakes @ 2.52 each
* Add another 1 x cornflakes @2.52 each
* Add 1 × weetabix @ 9.98 each

### Results

* Cart contains 2 x cornflakes
* Cart contains 1 x weetabix
* Subtotal = 15.02
* Tax = 1.88
* Total = 16.90

## Tips on what we’re looking for

We value simplicity as an architectural virtue and as a development practice. Solutions should reflect the difficulty of
the assigned task, and shouldn’t be overly complex. We prefer simple, well tested solutions over clever solutions.

### DO

* ✅ Include unit tests.
* ✅ Test both any client and logic.
* ✅ Update the README.md with any relevant information, assumptions, and/or tradeoffs you would like to highlight.

### DO NOT

* ❌ Submit any form of app, such as web APIs, browser, desktop, or command-line applications.
* ❌ Add unnecessary layers of abstraction.
* ❌ Add unnecessary patterns/ architectural features that aren’t called for e.g. persistent storage.
