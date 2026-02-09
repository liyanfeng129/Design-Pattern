# 📘 Guide: Mock Object Pattern with EasyMock (W08E01)

## 🏗️ Architecture Overview

### UML — Source Code (unchanged)

```
┌─────────────────────────────────┐
│       «interface»               │
│   RealTimePositionService       │
├─────────────────────────────────┤
│ + getX(pev: PEV): int          │
│ + getY(pev: PEV): int          │
│ + getDirection(pev: PEV): Dir  │
└──────────────┬──────────────────┘
               │ uses
               ▼
┌─────────────────────────────────┐       ┌───────────────┐
│      NavigationService          │       │  Destination   │
├─────────────────────────────────┤       ├───────────────┤
│ - realTimePositionService       │       │ - x: int      │
├─────────────────────────────────┤       │ - y: int      │
│ + getInstructions(pev, dest)    │──────▶│ - name: String │
│ + getDirectionDistance(pev,dest)│       └───────────────┘
│ + isCorrectlyConnected(pev)    │
└─────────────────────────────────┘       ┌──────────────┐
               │ uses                     │  «enum»      │
               ▼                          │  Direction    │
┌─────────────────────────────────┐       ├──────────────┤
│        «abstract» PEV          │       │ NORTH, EAST  │
├─────────────────────────────────┤       │ SOUTH, WEST  │
│ - chargeLevel, licensePlate    │       └──────────────┘
│ - available, pricePerMinute    │
└───────┬─────────┬──────────────┘
        │         │          │
   ┌────┴──┐ ┌───┴───┐ ┌────┴────────┐
   │ EBike │ │EMoped │ │EKickscooter │
   └───────┘ └───────┘ └─────────────┘
```

### UML — Test Code (what you create)

```
┌──────────────────────────────────────────┐
│  NavigationServiceTest                   │
│  @ExtendWith(EasyMockExtension.class)    │
├──────────────────────────────────────────┤
│ @Mock(STRICT) realTimePositionService    │──── Mock of RealTimePositionService
│ @Mock pev                                │──── Mock of PEV
│ @TestSubject navigationService           │──── Auto-injects mock
├──────────────────────────────────────────┤
│ + testDestinationReached()               │
│ + testDirectionDistance()                │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│  AdvancedNavigationServiceTest           │
│  @ExtendWith(EasyMockExtension.class)    │
├──────────────────────────────────────────┤
│ @Mock(NICE) realTimePositionService      │──── Mock of RealTimePositionService
│ @Mock pev                                │──── Mock of PEV
│ @TestSubject navigationService           │──── Auto-injects mock
├──────────────────────────────────────────┤
│ + testConnectionLoss()                   │
│ + testCorrectConnection()   ⚠️ verify    │
└──────────────────────────────────────────┘
```

---

## 🔑 Key Concepts

### 1. Study `NavigationService` method call order

Before writing any test, trace each method to understand **what is called and in what order**:

| Method                        | Call Order                                                 |
| ----------------------------- | ---------------------------------------------------------- |
| `getInstructions(pev, dest)`  | ① `getX(pev)` → ② `getY(pev)` → ③ `getDirection(pev)`    |
| `getDirectionDistance(pev, d)` | ① `getDirection(pev)` → ② `getX(pev)` → ③ `getY(pev)` |
| `isCorrectlyConnected(pev)`  | ① `getX(pev)` → ② `getY(pev)` **(no getDirection!)**      |

### 2. MockType.STRICT vs MockType.NICE

| Feature           | STRICT                          | NICE                                     |
| ----------------- | ------------------------------- | ---------------------------------------- |
| Unexpected calls  | Throws error                    | Returns defaults (`0`, `null`, `false`)  |
| Call order         | Must match exactly              | Order doesn't matter                     |
| Default values     | None — must define all          | `0` for int, `null` for objects          |

### 3. EasyMock lifecycle

```
expect(...).andReturn(...)   →   replay(mock)   →   call method under test   →   verify(mock)
   (record phase)              (switch to replay)      (execute)                (check expectations)
```

---

## Part 1: `NavigationServiceTest` (STRICT mock)

### Diff (git-style)

```diff
 @ExtendWith(EasyMockExtension.class)
 class NavigationServiceTest {
 
     // TODO make sure to specify the necessary elements for the mock object pattern
     // and to use the required mock type (STRICT)
+    @Mock(MockType.STRICT)
+    private RealTimePositionService realTimePositionService;
+
+    @Mock
+    private PEV pev;
 
     @TestSubject
     private NavigationService navigationService = new NavigationService();
 
+    @Test
+    void testDestinationReached() {
+        Destination destination = new Destination(5, 10, "Home");
+
+        expect(realTimePositionService.getX(pev)).andReturn(5);
+        expect(realTimePositionService.getY(pev)).andReturn(10);
+        expect(realTimePositionService.getDirection(pev)).andReturn(Direction.NORTH);
+
+        replay(realTimePositionService);
+
+        String instructions = navigationService.getInstructions(pev, destination);
+        assertEquals("destination reached", instructions);
+    }
+
+    @Test
+    void testDirectionDistance() {
+        Destination destination = new Destination(5, 10, "Office");
+        expect(realTimePositionService.getDirection(pev)).andReturn(Direction.SOUTH);
+        expect(realTimePositionService.getX(pev)).andReturn(5);
+        expect(realTimePositionService.getY(pev)).andReturn(15);
+        replay(realTimePositionService);
+        String distance = navigationService.getDirectionDistance(pev, destination);
+        assertEquals("drive south for 5 more kilometers", distance);
+    }
     // TODO make sure to initialize the attributes required for the tests
 
     // TODO implement testDestinationReached()
 
     // TODO implement testDirectionDistance()
 }
```

### Task 1A: `testDestinationReached()` — Step-by-step

**Goal:** Test that `getInstructions()` returns `"destination reached"` when PEV position == destination.

1. **Create a destination** with coordinates `(5, 10)`.
2. **Record expectations in the exact order** that `getInstructions()` calls them (STRICT!):
   - Line 47 of `NavigationService`: `getX(pev)` → return `5`
   - Line 48: `getY(pev)` → return `10`
   - Line 49: `getDirection(pev)` → return any direction (e.g., `NORTH`)
3. **Call `replay()`** to switch from record → replay mode.
4. **Execute** `getInstructions(pev, destination)`.
5. **Assert** result equals `"destination reached"`.

**Why does it return "destination reached"?**
Because `pevX == destination.getX()` (5==5) AND `pevY == destination.getY()` (10==10), so `sameX` and `sameY` are both `true`, hitting the `else` branch:

```java
} else {
    return "destination reached";
}
```

**⚠️ Why must we also mock `getDirection()`?**
Even though the code reaches `"destination reached"` before *using* `direction`, `getDirection()` is called on line 49 **before** the condition checks. With STRICT mocks, **all recorded calls must actually happen and in order**.

### Task 1B: `testDirectionDistance()` — Step-by-step

**Goal:** Test that `getDirectionDistance()` returns `"drive south for 5 more kilometers"`.

1. **Create a destination** at `(5, 10)`.
2. **Record expectations in the exact order** that `getDirectionDistance()` calls them:
   - Line 74: `getDirection(pev)` → `Direction.SOUTH` ← **called FIRST!**
   - Line 75: `getX(pev)` → `5`
   - Line 76: `getY(pev)` → `15` ← greater than dest Y (10)
3. **Call `replay()`**.
4. **Execute** `getDirectionDistance(pev, destination)`.
5. **Assert** result equals `"drive south for 5 more kilometers"` (15 − 10 = 5).

**⚠️ Critical order difference:** `getDirectionDistance()` calls `getDirection()` **first**, then `getX()`, then `getY()`. This is the **opposite** order from `getInstructions()` which calls `getX()` first. With STRICT mocks, getting the order wrong causes failure!

---

## Part 2: `AdvancedNavigationServiceTest` (NICE mock)

### Diff (git-style)

```diff
 @ExtendWith(EasyMockExtension.class)
 class AdvancedNavigationServiceTest {
 
     // TODO make sure to specify the necessary elements for the mock object pattern
     // and to use the required mock type (NICE)
+    @Mock(MockType.NICE)
+    private RealTimePositionService realTimePositionService;
+
+    @Mock
+    private PEV pev;
 
     @TestSubject
     private NavigationService navigationService = new NavigationService();
 
+    @Test
+    void testConnectionLoss() {
+        Destination destination = new Destination(5, 10, "Home");
+
+        expect(realTimePositionService.getX(pev)).andReturn(0);
+        expect(realTimePositionService.getY(pev)).andReturn(0);
+        expect(realTimePositionService.getDirection(pev)).andReturn(null);
+
+        replay(realTimePositionService);
+
+        String instructions = navigationService.getInstructions(pev, destination);
+        assertEquals("connection lost", instructions);
+    }
+
+    @Test
+    void testCorrectConnection() {
+        expect(realTimePositionService.getX(pev)).andReturn(5);
+        expect(realTimePositionService.getY(pev)).andReturn(10);
+        // Intentionally adding an extra expected call that won't be made
+        expect(realTimePositionService.getDirection(pev)).andReturn(Direction.NORTH);
+
+        replay(realTimePositionService);
+
+        String isConnected = navigationService.isCorrectlyConnected(pev);
+        assertEquals("correctly connected", isConnected);
+
+        // This verify should fail due to the extra expected call
+        verify(realTimePositionService);
+    }
     // TODO make sure to initialize the attributes required for the tests
 
     // TODO implement testConnectionLoss()
 
     // TODO implement testCorrectConnection()
     // Remark: make sure to use the verify() functionality
     // & that the test fails due to a verify error
 }
```

### Task 2A: `testConnectionLoss()` — Step-by-step

**Goal:** Test that `getInstructions()` returns `"connection lost"` when position service returns invalid/default values.

1. **Create a destination** (any values, e.g., `(5, 10)`).
2. **Record expectations** returning "connection lost" values:
   - `getX(pev)` → `0`
   - `getY(pev)` → `0`
   - `getDirection(pev)` → `null`
3. **Call `replay()`**.
4. **Execute** and **assert** `"connection lost"`.

**Why does it work?** In `getInstructions()` line 56:

```java
if (direction == null && pevX == 0 && pevY == 0) {
    return "connection lost";
}
```

**💡 NICE mock insight:** With a NICE mock, you could actually just call `replay(realTimePositionService)` without any `expect()` calls — the mock would automatically return `0` for `getX()`/`getY()` and `null` for `getDirection()`. Both approaches are valid. The solution explicitly sets the expectations for clarity.

### Task 2B: `testCorrectConnection()` — Step-by-step

**Goal:** Demonstrate `verify()` by intentionally causing a verification failure.

1. **Study `isCorrectlyConnected()`** — it only calls `getX(pev)` and `getY(pev)` (**NOT** `getDirection()`).
2. **Record three expectations:**
   - `getX(pev)` → `5` ✅ (will be called)
   - `getY(pev)` → `10` ✅ (will be called)
   - `getDirection(pev)` → `Direction.NORTH` ❌ (will **NOT** be called — this is the trick!)
3. **Call `replay()`**.
4. **Execute** `isCorrectlyConnected(pev)` — only `getX` and `getY` are called.
5. **Assert** result equals `"correctly connected"` (5 ≥ 0 && 10 ≥ 0).
6. **Call `verify(realTimePositionService)`** — this **FAILS** because `getDirection()` was expected but never called.

**⚠️ Why it doesn't crash during replay:** NICE mocks don't complain about order or unexpected calls. The assertion itself passes, but `verify()` detects the unmatched expectation and throws an `AssertionError`.

**⚠️ This test is expected to fail** — that's the entire point! It demonstrates that `verify()` catches when expected methods are never called.

---

## Summary of All Changes

| File                             | What was added                            | Lines |
| -------------------------------- | ----------------------------------------- | ----- |
| `NavigationServiceTest.java`     | `@Mock(STRICT) realTimePositionService`   | +2    |
|                                  | `@Mock PEV pev`                           | +2    |
|                                  | `testDestinationReached()` method         | +12   |
|                                  | `testDirectionDistance()` method           | +10   |
| `AdvancedNavigationServiceTest.java` | `@Mock(NICE) realTimePositionService` | +2    |
|                                  | `@Mock PEV pev`                           | +2    |
|                                  | `testConnectionLoss()` method             | +12   |
|                                  | `testCorrectConnection()` method          | +14   |
| **Source files (`src/`)**        | **Nothing changed**                       | **0** |
