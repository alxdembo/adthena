package com.shoppingbasket.model

/**
 * Represents a product in the catalog with its name and price.
 *
 * @param name  The product name (case-insensitive)
 * @param price The price in pounds
 */
case class Product(name: String, price: BigDecimal) {
  require(price >= 0, "Price must be non-negative")
  require(name.nonEmpty, "Product name cannot be empty")
}

/**
 * Represents an item in the shopping basket.
 *
 * @param product  The product being purchased
 * @param quantity The number of items
 */
case class BasketItem(product: Product, quantity: Int) {
  require(quantity > 0, "Quantity must be positive")

  /** Calculate the total price for this basket item */
  def totalPrice: BigDecimal = product.price * quantity
}

/**
 * Represents a discount applied to the basket.
 *
 * @param description Human-readable description of the discount
 * @param amount      The discount amount in pence
 */
case class Discount(description: String, amount: BigDecimal) {
  require(amount >= 0, "Discount amount must be non-negative")
  require(description.nonEmpty, "Discount description cannot be empty")
}

/**
 * Represents the final pricing result for a basket.
 *
 * @param subtotal  The subtotal before discounts
 * @param discounts List of applied discounts
 * @param total     The final total after discounts
 */
case class PricingResult(
                          subtotal: BigDecimal,
                          discounts: List[Discount],
                          total: BigDecimal
                        ) {
  require(subtotal >= 0, "Subtotal must be non-negative")
  require(total >= 0, "Total must be non-negative")
  require(total <= subtotal, "Total cannot be greater than subtotal")
}