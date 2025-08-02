package com.shoppingbasket

import com.shoppingbasket.model._
import com.shoppingbasket.pricing._
import com.shoppingbasket.service._

/**
 * Main application entry point for the Shopping Basket pricing system.
 *
 * Usage: PriceBasket item1 item2 item3 ...
 * Example: PriceBasket Apples Milk Bread
 */
object PriceBasket extends App {

  private val catalog = new ProductCatalog()
  private val offerEngine = new OfferEngine()
  private val basketService = new BasketService(catalog, offerEngine)

  if (args.isEmpty) {
    println("Usage: PriceBasket item1 item2 item3 ...")
    println("Available items: Soup, Bread, Milk, Apples")
    sys.exit(1)
  }

  try {
    val items = args.toList
    val result = basketService.priceBasket(items)

    // Format and display results
    println(formatOutput(result))

  } catch {
    case e: IllegalArgumentException =>
      println(s"Error: ${e.getMessage}")
      sys.exit(1)
    case e: Exception =>
      println(s"Unexpected error: ${e.getMessage}")
      sys.exit(1)
  }

  /**
   * Formats the pricing result for console output.
   */
  private def formatOutput(result: PricingResult): String = {
    val subtotalLine = f"Subtotal: £${result.subtotal}%.2f"

    val discountLines = if (result.discounts.nonEmpty) {
      result.discounts.map(discount =>
        f"${discount.description}: ${discount.amount}%.0fp"
      ).mkString("\n")
    } else {
      "(No offers available)"
    }

    val totalLine = f"Total price: £${result.total}%.2f"

    s"$subtotalLine\n$discountLines\n$totalLine"
  }
}