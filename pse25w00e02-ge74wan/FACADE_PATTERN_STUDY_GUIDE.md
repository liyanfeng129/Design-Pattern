# Facade Pattern - Step-by-Step Solution Guide

## Overview
This guide explains how to implement the **Facade Pattern** to reduce coupling between subsystems.

---

## Problem: Before the Facade

### Initial Architecture (Commit: `ac6ed5d`)
```
┌─────────────┐     ┌────────────────────┐
│  BookStore  │────▶│  OrderController   │
│             │────▶│  ShippingController│
└─────────────┘     └────────────────────┘

┌─────────────┐     ┌────────────────────────┐
│   Cinema    │────▶│  OrderController       │
│             │────▶│  ShippingController    │
│             │────▶│  AdvertisementController│
└─────────────┘     └────────────────────────┘
```

**Problems:**
1. **High coupling** - Clients depend on multiple controllers
2. **Exposed internal methods** - Clients can access methods they shouldn't (e.g., `commissionExternalParcelService()`)
3. **Complex client code** - Clients must coordinate multiple controller calls

---

## Solution: After the Facade

### Final Architecture (Commit: `dffc152`)
```
┌─────────────┐     ┌──────────────────┐     ┌────────────────────────┐
│  BookStore  │────▶│                  │────▶│  OrderController       │
└─────────────┘     │  ECommerceFacade │────▶│  ShippingController    │
                    │                  │────▶│  AdvertisementController│
┌─────────────┐     │                  │     └────────────────────────┘
│   Cinema    │────▶│                  │
└─────────────┘     └──────────────────┘
```

---

## Step-by-Step Implementation

### STEP 1: Create the Facade Class (Task 1)
**File:** `ECommerceFacade.java` (NEW FILE)

```java
package de.tum.cit.aet.pse.ecommerce;

public class ECommerceFacade {
    // Private references to subsystem controllers
    private OrderController orderController;
    private AdvertisementController advertisementController;
    private ShippingController shippingController;
    
    // Constructor initializes all controllers
    public ECommerceFacade() {
        this.orderController = new OrderController();
        this.advertisementController = new AdvertisementController();
        this.shippingController = new ShippingController();
    }
}
```

**WHY:** The facade holds private references to controllers. Clients cannot access these directly.

---

### STEP 2: Delegate Public Methods (Task 2)

Add methods that delegate to the respective controllers:

```java
// From OrderController
public void processOrder(Order order) {
    orderController.processOrder(order);
}

public void processOrder(Order order, String phoneNumber) {
    orderController.processOrder(order, phoneNumber);
}

public Order retrieveLatestOrder(int id) {
    return orderController.retrieveLatestOrder(id);
}

// From AdvertisementController
public void playAdvertisement(int ageRestriction) {
    advertisementController.playAdvertisement(ageRestriction);
}
```

**WHY:** We only expose methods that clients actually need.

---

### STEP 3: Implement shipOrder Method (Task 2 - Special)

```java
public void shipOrder(Order order, String shippingAddress) {
    // Create shipping and set it on the order
    order.setShipping(shippingController.createShipping(shippingAddress));
    // Ship the order
    shippingController.shipOrder(order);
}
```

**WHY:** This method **combines** multiple operations:
- Before: Clients had to call `createShipping()`, `setShipping()`, then `shipOrder()`
- After: Clients just call `shipOrder(order, address)`

**IMPORTANT:** We do NOT expose:
- `createShipping()` - internal implementation detail
- `commissionExternalParcelService()` - clients shouldn't access this

---

### STEP 4: Refactor BookStore (Task 3)

**BEFORE → AFTER (with changes marked):**
```diff
- import de.tum.cit.aet.pse.ecommerce.OrderController;
- import de.tum.cit.aet.pse.ecommerce.ShippingController;
+ import de.tum.cit.aet.pse.ecommerce.ECommerceFacade;

  public class BookStore {
-     private final OrderController orderController;
-     private final ShippingController shippingController;
+     private final ECommerceFacade eCommerceFacade;
    
      public BookStore(String address, String name) {
-         this.orderController = new OrderController();
-         this.shippingController = new ShippingController();
+         this.eCommerceFacade = new ECommerceFacade();
      }
    
      public void acceptOrder(String shippingAddress, String phoneNumber) {
-         Order order = orderController.retrieveLatestOrder(id);
-         orderController.processOrder(order, phoneNumber);
-         order.setShipping(shippingController.createShipping(shippingAddress));
-         shippingController.shipOrder(order);
+         Order order = eCommerceFacade.retrieveLatestOrder(id);
+         eCommerceFacade.processOrder(order, phoneNumber);
+         eCommerceFacade.shipOrder(order, shippingAddress);  // Combines shipping creation + shipOrder!
      }
  }
```

**CHANGES SUMMARY:**
| What | Before | After |
|------|--------|-------|
| Imports | 2 controllers | 1 facade |
| Fields | 2 controller references | 1 facade reference |
| acceptOrder() | 4 lines, manual shipping setup | 3 lines, simplified |

---

### STEP 5: Refactor Cinema (Task 3)

**BEFORE → AFTER (with changes marked):**
```diff
- import de.tum.cit.aet.pse.ecommerce.AdvertisementController;
- import de.tum.cit.aet.pse.ecommerce.OrderController;
- import de.tum.cit.aet.pse.ecommerce.ShippingController;
+ import de.tum.cit.aet.pse.ecommerce.ECommerceFacade;

  public class Cinema {
-     private final OrderController orderController;
-     private final ShippingController shippingController;
-     private final AdvertisementController advertisementController;
+     private final ECommerceFacade eCommerceFacade;
    
      public Cinema(String address, String name) {
-         this.orderController = new OrderController();
-         this.shippingController = new ShippingController();
-         this.advertisementController = new AdvertisementController();
+         this.eCommerceFacade = new ECommerceFacade();
      }
    
      public void advertise(int ageRestriction) {
-         advertisementController.playAdvertisement(ageRestriction);
+         eCommerceFacade.playAdvertisement(ageRestriction);
      }
    
      public void deliverPopcorn(String shippingAddress) {
-         Order order = orderController.retrieveLatestOrder(id);
-         orderController.processOrder(order);
-         order.setShipping(shippingController.createShipping(shippingAddress));
-         shippingController.shipOrder(order);
+         Order order = eCommerceFacade.retrieveLatestOrder(id);
+         eCommerceFacade.processOrder(order);
+         eCommerceFacade.shipOrder(order, shippingAddress);  // Combines shipping creation + shipOrder!
      }
  }
```

**CHANGES SUMMARY:**
| What | Before | After |
|------|--------|-------|
| Imports | 3 controllers | 1 facade |
| Fields | 3 controller references | 1 facade reference |
| Constructor | 3 instantiations | 1 instantiation |

---

## Key Takeaways for Exam

### 1. What is the Facade Pattern?
> A structural pattern that provides a **simplified interface** to a complex subsystem.

### 2. When to use it?
- When clients need to interact with multiple classes
- When you want to hide internal complexity
- When you want to reduce coupling between packages

### 3. Implementation Checklist
- [ ] Create a new Facade class
- [ ] Add **private** references to all subsystem classes
- [ ] Initialize subsystem objects in the constructor
- [ ] Create **public** methods that delegate to subsystem methods
- [ ] Only expose methods that clients actually need
- [ ] Refactor clients to use the facade instead of direct controller access

### 4. Benefits
| Benefit | Explanation |
|---------|-------------|
| **Reduced coupling** | Clients depend on 1 class instead of many |
| **Information hiding** | Internal methods not exposed |
| **Simpler client code** | Complex operations simplified |
| **Easier maintenance** | Changes to subsystem don't affect clients |

### 5. Common Mistakes to Avoid
- ❌ Making controller fields public
- ❌ Exposing all methods from controllers (only expose what's needed)
- ❌ Forgetting to remove old controller references from clients
- ❌ Not combining related operations (like the shipOrder method)

---

## Git Commit History Reference

| Commit | Message | What was done |
|--------|---------|---------------|
| `ac6ed5d` | Set up template for exercise | Initial code with direct controller usage |
| `fb0aa65` | ECommerceFacede implemented with all functions | Created facade, added all methods |
| `1948b91` | decoupling done | Refactored BookStore and Cinema to use facade |
| `dffc152` | corrected the name eCommerceFacade | Fixed typo in naming |

---

## Quick Reference: Code Transformations

### Import Statement Pattern
```diff
- import de.tum.cit.aet.pse.ecommerce.OrderController;
- import de.tum.cit.aet.pse.ecommerce.ShippingController;
- import de.tum.cit.aet.pse.ecommerce.AdvertisementController;
+ import de.tum.cit.aet.pse.ecommerce.ECommerceFacade;
```

### Field Declaration Pattern
```diff
- private final OrderController orderController;
- private final ShippingController shippingController;
+ private final ECommerceFacade eCommerceFacade;
```

### Shipping Operation Pattern
```diff
- Order order = orderController.retrieveLatestOrder(id);
- orderController.processOrder(order);
- order.setShipping(shippingController.createShipping(address));
- shippingController.shipOrder(order);
+ Order order = eCommerceFacade.retrieveLatestOrder(id);
+ eCommerceFacade.processOrder(order);
+ eCommerceFacade.shipOrder(order, address);  // 2 lines → 1 line!
```

Good luck on your exam! 🎓
