package com.shoppingbasket.service

import com.shoppingbasket.model._
import com.shoppingbasket.pricing.OfferEngine

/**
 * Service class that handles basket pricing logic.
 * Implements the Facade pattern, providing a simplified interface
 * to the complex subsystem of product catalog and offer engine.
 *
 * @param catalog     The product catalog service
 * @param offerEngine The offer engine for applying discounts
 */
class BasketService(catalog: ProductCatalog, offerEngine: OfferEngine) {

  /**
   * Prices a basket of items given as a list of product names.
   *
   * @param itemNames List of product names in the basket
   * @return PricingResult with subtotal, discounts, and total
   * @throws IllegalArgumentException if any product is not found
   */
  def priceBasket(itemNames: List[String]): PricingResult = {
    val basketItems = groupItemsByProduct(itemNames)

    val subtotal = basketItems.map(_.totalPrice).sum
    val discounts = offerEngine.applyOffers(basketItems)
    val totalDiscountAmount = discounts.map(_.amount).sum
    val total = subtotal - totalDiscountAmount

    PricingResult(subtotal, discounts, total)
  }

  /**
   * Groups individual item names into BasketItem objects with quantities.
   *
   * @param itemNames List of product names (may contain duplicates)
   * @return List of BasketItem objects with quantities
   */
  private def groupItemsByProduct(itemNames: List[String]): List[BasketItem] = {
    itemNames
      .groupBy(_.toLowerCase)
      .map { case (_, names) =>
        val product = catalog.getProduct(names.head)
        BasketItem(product, names.length)
      }
      .toList
  }

  /**
   * Validates that all item names exist in the catalog.
   *
   * @param itemNames List of product names to validate
   * @throws IllegalArgumentException if any product is not found
   */
  def validateItems(itemNames: List[String]): Unit = {
    val invalidItems = itemNames.filterNot(catalog.hasProduct)
    if (invalidItems.nonEmpty) {
      throw new IllegalArgumentException(s"Unknown products: ${invalidItems.mkString(", ")}")
    }
  }
}