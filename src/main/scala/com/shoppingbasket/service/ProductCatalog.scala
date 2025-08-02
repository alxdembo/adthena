package com.shoppingbasket.service

import com.shoppingbasket.model.Product

/**
 * Product catalog service that manages available products and their prices.
 * Implements the Repository pattern for product data access.
 */
class ProductCatalog {

  // Product catalog with current prices
  private val products = Map(
    "soup" -> Product("Soup", BigDecimal("0.65")),
    "bread" -> Product("Bread", BigDecimal("0.80")),
    "milk" -> Product("Milk", BigDecimal("1.30")),
    "apples" -> Product("Apples", BigDecimal("1.00"))
  )

  /**
   * Finds a product by name (case-insensitive).
   *
   * @param name The product name to search for
   * @return Some(Product) if found, None otherwise
   */
  def findProduct(name: String): Option[Product] = {
    products.get(name.toLowerCase)
  }

  /**
   * Gets a product by name, throwing an exception if not found.
   *
   * @param name The product name
   * @return The product
   * @throws IllegalArgumentException if product is not found
   */
  def getProduct(name: String): Product = {
    findProduct(name).getOrElse(
      throw new IllegalArgumentException(s"Unknown product: $name")
    )
  }

  /**
   * Returns all available products.
   *
   * @return Collection of all products
   */
  def getAllProducts: Iterable[Product] = products.values

  /**
   * Checks if a product exists in the catalog.
   *
   * @param name The product name
   * @return true if the product exists, false otherwise
   */
  def hasProduct(name: String): Boolean = products.contains(name.toLowerCase)
}