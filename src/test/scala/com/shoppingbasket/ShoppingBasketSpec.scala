package com.shoppingbasket

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.shoppingbasket.model._
import com.shoppingbasket.service._
import com.shoppingbasket.pricing._

class ProductCatalogSpec extends AnyFlatSpec with Matchers {

  val catalog = new ProductCatalog()

  "ProductCatalog" should "find existing products case-insensitively" in {
    catalog.findProduct("soup") shouldBe Some(Product("Soup", BigDecimal("0.65")))
    catalog.findProduct("BREAD") shouldBe Some(Product("Bread", BigDecimal("0.80")))
    catalog.findProduct("Milk") shouldBe Some(Product("Milk", BigDecimal("1.30")))
  }

  it should "return None for non-existing products" in {
    catalog.findProduct("pizza") shouldBe None
    catalog.findProduct("") shouldBe None
  }

  it should "throw exception when getting non-existing product" in {
    an[IllegalArgumentException] should be thrownBy catalog.getProduct("pizza")
  }

  it should "check product existence correctly" in {
    catalog.hasProduct("soup") shouldBe true
    catalog.hasProduct("APPLES") shouldBe true
    catalog.hasProduct("pizza") shouldBe false
  }
}

class PercentageDiscountOfferSpec extends AnyFlatSpec with Matchers {

  val applesOffer = new PercentageDiscountOffer("Apples", 10)
  val applesProduct: Product = Product("Apples", BigDecimal("1.00"))
  val breadProduct: Product = Product("Bread", BigDecimal("0.80"))

  "PercentageDiscountOffer" should "apply discount to matching product" in {
    val items = List(BasketItem(applesProduct, 2))
    val discounts = applesOffer.apply(items)

    discounts should have size 1
    discounts.head.description shouldBe "Apples 10% off"
    discounts.head.amount shouldBe BigDecimal("20") // 20 pence
  }

  it should "not apply discount to non-matching products" in {
    val items = List(BasketItem(breadProduct, 1))
    val discounts = applesOffer.apply(items)

    discounts shouldBe empty
  }

  it should "be applicable when matching product exists" in {
    val items = List(BasketItem(applesProduct, 1))
    applesOffer.isApplicable(items) shouldBe true
  }

  it should "not be applicable when matching product doesn't exist" in {
    val items = List(BasketItem(breadProduct, 1))
    applesOffer.isApplicable(items) shouldBe false
  }
}

class BuyXGetYDiscountOfferSpec extends AnyFlatSpec with Matchers {

  val soupBreadOffer = new BuyXGetYDiscountOffer("Soup", 2, "Bread", 50)
  val soupProduct: Product = Product("Soup", BigDecimal("0.65"))
  val breadProduct: Product = Product("Bread", BigDecimal("0.80"))

  "BuyXGetYDiscountOffer" should "apply discount when conditions are met" in {
    val items = List(
      BasketItem(soupProduct, 2),
      BasketItem(breadProduct, 1)
    )
    val discounts = soupBreadOffer.apply(items)

    discounts should have size 1
    discounts.head.description shouldBe "Buy 2 Soup get Bread 50% off"
    discounts.head.amount shouldBe BigDecimal("40") // 40 pence
  }

  it should "not apply discount when trigger quantity not met" in {
    val items = List(
      BasketItem(soupProduct, 1),
      BasketItem(breadProduct, 1)
    )
    val discounts = soupBreadOffer.apply(items)

    discounts shouldBe empty
  }

  it should "not apply discount when discount product missing" in {
    val items = List(BasketItem(soupProduct, 2))
    val discounts = soupBreadOffer.apply(items)

    discounts shouldBe empty
  }

  it should "handle multiple trigger quantities correctly" in {
    val items = List(
      BasketItem(soupProduct, 4),
      BasketItem(breadProduct, 2)
    )
    val discounts = soupBreadOffer.apply(items)

    discounts should have size 1
    discounts.head.amount shouldBe BigDecimal("80") // 80 pence (2 breads discounted)
  }
}

class BasketServiceSpec extends AnyFlatSpec with Matchers {

  val catalog = new ProductCatalog()
  val offerEngine = new OfferEngine()
  val basketService = new BasketService(catalog, offerEngine)

  "BasketService" should "price basket with no offers correctly" in {
    val result = basketService.priceBasket(List("Milk"))

    result.subtotal shouldBe BigDecimal("1.30")
    result.discounts shouldBe empty
    result.total shouldBe BigDecimal("1.30")
  }

  it should "price basket with apple discount correctly" in {
    val result = basketService.priceBasket(List("Apples", "Milk", "Bread"))

    result.subtotal shouldBe BigDecimal("3.10")
    result.discounts should have size 1
    result.discounts.head.description shouldBe "Apples 10% off"
    result.discounts.head.amount shouldBe BigDecimal("10") // 10 pence
    result.total shouldBe BigDecimal("3.00")
  }

  it should "price basket with soup-bread offer correctly" in {
    val result = basketService.priceBasket(List("Soup", "Soup", "Bread"))

    result.subtotal shouldBe BigDecimal("2.10")
    result.discounts should have size 1
    result.discounts.head.description shouldBe "Buy 2 Soup get Bread 50% off"
    result.discounts.head.amount shouldBe BigDecimal("40") // 40 pence
    result.total shouldBe BigDecimal("1.70")
  }

  it should "handle multiple of same item correctly" in {
    val result = basketService.priceBasket(List("Apples", "Apples"))

    result.subtotal shouldBe BigDecimal("2.00")
    result.discounts should have size 1
    result.discounts.head.amount shouldBe BigDecimal("20") // 20 pence (10% of £2.00)
    result.total shouldBe BigDecimal("1.80")
  }

  it should "apply multiple offers when applicable" in {
    val result = basketService.priceBasket(List("Soup", "Soup", "Bread", "Apples"))

    result.subtotal shouldBe BigDecimal("3.10")
    result.discounts should have size 2
    result.total shouldBe BigDecimal("2.60") // £3.10 - £0.40 (bread) - £0.10 (apples)
  }

  it should "throw exception for unknown products" in {
    an[IllegalArgumentException] should be thrownBy {
      basketService.priceBasket(List("Pizza"))
    }
  }
}

class ModelSpec extends AnyFlatSpec with Matchers {

  "Product" should "require non-negative price" in {
    an[IllegalArgumentException] should be thrownBy Product("Test", BigDecimal("-1"))
  }

  it should "require non-empty name" in {
    an[IllegalArgumentException] should be thrownBy Product("", BigDecimal("1"))
  }

  "BasketItem" should "require positive quantity" in {
    val product = Product("Test", BigDecimal("1"))
    an[IllegalArgumentException] should be thrownBy BasketItem(product, 0)
    an[IllegalArgumentException] should be thrownBy BasketItem(product, -1)
  }

  it should "calculate total price correctly" in {
    val product = Product("Test", BigDecimal("1.50"))
    val item = BasketItem(product, 3)
    item.totalPrice shouldBe BigDecimal("4.50")
  }

  "Discount" should "require non-negative amount" in {
    an[IllegalArgumentException] should be thrownBy Discount("Test", BigDecimal("-1"))
  }

  it should "require non-empty description" in {
    an[IllegalArgumentException] should be thrownBy Discount("", BigDecimal("1"))
  }

  "PricingResult" should "validate that total doesn't exceed subtotal" in {
    an[IllegalArgumentException] should be thrownBy {
      PricingResult(BigDecimal("1.00"), List.empty, BigDecimal("2.00"))
    }
  }
}