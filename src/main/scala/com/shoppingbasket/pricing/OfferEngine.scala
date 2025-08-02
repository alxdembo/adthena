package com.shoppingbasket.pricing

import com.shoppingbasket.model._

/**
 * Strategy pattern interface for special offers.
 */
trait SpecialOffer {
  /**
   * Applies the offer to a list of basket items.
   *
   * @param items The basket items
   * @return List of discounts applied, empty if offer doesn't apply
   */
  def apply(items: List[BasketItem]): List[Discount]

  /**
   * Checks if this offer is applicable to the given basket items.
   *
   * @param items The basket items
   * @return true if the offer can be applied
   */
  def isApplicable(items: List[BasketItem]): Boolean
}

/**
 * Percentage discount offer for a specific product.
 *
 * @param productName The name of the product to discount
 * @param discountPercent The discount percentage (0-100)
 */
class PercentageDiscountOffer(productName: String, discountPercent: Int) extends SpecialOffer {

  override def apply(items: List[BasketItem]): List[Discount] = {
    items
      .filter(_.product.name.equalsIgnoreCase(productName))
      .flatMap { item =>
        val discountAmount = (item.totalPrice * discountPercent / 100).setScale(2, BigDecimal.RoundingMode.HALF_UP)
        if (discountAmount > 0) {
          Some(Discount(s"$productName $discountPercent% off", discountAmount * 100)) // Convert to pence
        } else {
          None
        }
      }
  }

  override def isApplicable(items: List[BasketItem]): Boolean = {
    items.exists(_.product.name.equalsIgnoreCase(productName))
  }
}

/**
 * Buy X get Y at discount offer.
 *
 * @param triggerProduct The product that triggers the offer
 * @param triggerQuantity The minimum quantity needed to trigger the offer
 * @param discountProduct The product that gets discounted
 * @param discountPercent The discount percentage for the discounted product
 */
class BuyXGetYDiscountOffer(
                             triggerProduct: String,
                             triggerQuantity: Int,
                             discountProduct: String,
                             discountPercent: Int
                           ) extends SpecialOffer {

  override def apply(items: List[BasketItem]): List[Discount] = {
    val triggerItem = items.find(_.product.name.equalsIgnoreCase(triggerProduct))
    val discountItem = items.find(_.product.name.equalsIgnoreCase(discountProduct))

    (triggerItem, discountItem) match {
      case (Some(trigger), Some(discount)) if trigger.quantity >= triggerQuantity =>
        val eligibleDiscountQuantity = Math.min(
          trigger.quantity / triggerQuantity,
          discount.quantity
        )

        if (eligibleDiscountQuantity > 0) {
          val discountAmount = (discount.product.price * discountPercent / 100 * eligibleDiscountQuantity)
            .setScale(2, BigDecimal.RoundingMode.HALF_UP)
          List(Discount(
            s"Buy $triggerQuantity $triggerProduct get $discountProduct $discountPercent% off",
            discountAmount * 100 // Convert to pence
          ))
        } else {
          List.empty
        }
      case _ => List.empty
    }
  }

  override def isApplicable(items: List[BasketItem]): Boolean = {
    val hasTrigger = items.exists(item =>
      item.product.name.equalsIgnoreCase(triggerProduct) && item.quantity >= triggerQuantity
    )
    val hasDiscount = items.exists(_.product.name.equalsIgnoreCase(discountProduct))

    hasTrigger && hasDiscount
  }
}

/**
 * Offer engine that manages and applies special offers to baskets.
 * Implements the Strategy pattern with a collection of offer strategies.
 */
class OfferEngine {

  // Current active offers
  private val offers: List[SpecialOffer] = List(
    new PercentageDiscountOffer("Apples", 10),
//    new PercentageDiscountOffer("Soup", 10),
    new BuyXGetYDiscountOffer("Soup", 2, "Bread", 50)
  )

  /**
   * Applies all applicable offers to the given basket items.
   *
   * @param items The basket items
   * @return List of all discounts applied
   */
  def applyOffers(items: List[BasketItem]): List[Discount] = {
    offers.flatMap(_.apply(items))
  }

  /**
   * Gets all currently active offers.
   *
   * @return List of special offers
   */
  def getActiveOffers: List[SpecialOffer] = offers
}