# Shopping Basket Pricing System

A command-line Scala application that calculates the total price of a shopping basket, applying special offers where applicable.

## Features

- **Product Catalog**: Manages available products and their prices
- **Special Offers**: Supports percentage discounts and "Buy X, Get Y" offers
- **Extensible Design**: Uses design patterns for easy maintenance and extension
- **Comprehensive Testing**: Full unit test coverage with ScalaTest
- **Input Validation**: Handles invalid products and edge cases gracefully

## Available Products

| Product | Price |
|---------|--------|
| Soup    | 65p per tin |
| Bread   | 80p per loaf |
| Milk    | £1.30 per bottle |
| Apples  | £1.00 per bag |

## Current Special Offers

1. **Apples**: 10% discount off their normal price
2. **Soup + Bread**: Buy 2 tins of soup and get a loaf of bread for half price

## Usage

### Prerequisites

- Scala 2.13.x
- SBT (Simple Build Tool)

### Building the Application

```bash
# Compile the application
sbt compile

# Run tests
sbt test

# Create executable JAR
sbt assembly
```

### Installation

```shell
sudo sh ./installer.sh
```

### Running the Application

#### After installation

```bash
PriceBasket Apples Milk Bread
```

#### Using SBT

```bash
sbt "run Apples Milk Bread"
```

#### Using JAR file

```bash
java -jar target/scala-2.13/price-basket.jar Apples Milk Bread
```

### Examples

#### Example 1: Apples discount
```bash
PriceBasket Apples Milk Bread
```
    
    Subtotal: £3.10
    Apples 10% off: 10p
    Total price: £3.00

#### Example 2: No offers
```bash
PriceBasket Milk
```

    Subtotal: £1.30
    (No offers available)
    Total price: £1.30

#### Example 3: Soup and bread offer
```bash
PriceBasket Soup Soup Bread
```
    Subtotal: £2.10
    Buy 2 Soup get Bread 50% off: 40p
    Total price: £1.70

#### Example 4: Multiple offers
```bash
PriceBasket Soup Soup Bread Apples
```
    Subtotal: £3.10
    Buy 2 Soup get Bread 50% off: 40p
    Apples 10% off: 10p
    Total price: £2.60


## Architecture

### Design Patterns Used

1. **Strategy Pattern**: `SpecialOffer` trait with concrete implementations for different offer types
2. **Repository Pattern**: `ProductCatalog` abstracts product data access
3. **Facade Pattern**: `BasketService` provides a simplified interface to the pricing system
4. **Template Method Pattern**: Common offer application logic with specific implementations

### Project Structure

```
src/
├── main/scala/com/shoppingbasket/
│   ├── PriceBasket.scala           # Main application entry point
│   ├── model/                      # Domain models
│   │   └── Models.scala            # Product, BasketItem, Discount, PricingResult
│   ├── service/                    # Business services
│   │   ├── ProductCatalog.scala    # Product repository
│   │   └── BasketService.scala     # Main business logic
│   └── pricing/                    # Pricing and offers
│       └── OfferEngine.scala       # Special offers implementation
└── test/scala/com/shoppingbasket/
    └── ShoppingBasketSpec.scala    # Unit tests
```

### Key Components

#### Domain Models
- **Product**: Represents a product with name and price
- **BasketItem**: A product with quantity in the basket
- **Discount**: Applied discount with description and amount
- **PricingResult**: Final result with subtotal, discounts, and total

#### Services
- **ProductCatalog**: Manages the product inventory and pricing
- **BasketService**: Core business logic for basket pricing
- **OfferEngine**: Manages and applies special offers

#### Special Offers
- **PercentageDiscountOffer**: Applies percentage discount to specific products
- **BuyXGetYDiscountOffer**: "Buy X, Get Y at discount" offers

## Testing

The application includes unit tests covering:

- Product catalog operations
- Special offer calculations
- Basket pricing logic
- Input