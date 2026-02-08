# Bridge Pattern Implementation Guide

## Overview

The **Bridge Pattern** is a structural design pattern that decouples an abstraction from its implementation so that the two can vary independently. This guide walks through refactoring an exam hashing system to use the Bridge Pattern.

---

## Problem Statement

The existing system has `ExamSystem` directly using `HashFunction` implementations (`SimpleHashAlgorithm` and `CryptoSecureHashAlgorithm`). The goal is to introduce a **bridge** (`Hashing` abstraction) that allows:
- **PreviewHashing**: Free version with limitations (uses `SimpleHashAlgorithm`)
- **EnterpriseHashing**: Full version (uses `CryptoSecureHashAlgorithm`)

---

## UML Diagrams

### Before: Original Design

```mermaid
classDiagram
    class ExamSystem {
        +hashFile(String, HashFunction) String
    }
    
    class HashFunction {
        <<interface>>
        +calculateHashCode(String) String
    }
    
    class SimpleHashAlgorithm {
        +calculateHashCode(String) String
    }
    
    class CryptoSecureHashAlgorithm {
        +calculateHashCode(String) String
    }
    
    ExamSystem ..> HashFunction : uses
    SimpleHashAlgorithm ..|> HashFunction : implements
    CryptoSecureHashAlgorithm ..|> HashFunction : implements
```

**Text representation:**
```
+-------------------------------------------+
|              ExamSystem                   |
+-------------------------------------------+
| +hashFile(String, HashFunction): String   |
+-------------------------------------------+
                    |
                    | uses directly
                    v
+-------------------------------------------+
|         <<interface>> HashFunction        |
+-------------------------------------------+
| +calculateHashCode(String): String        |
+-------------------------------------------+
                    ^
                    | implements
        +-----------+-----------+
        |                       |
+---------------------+  +---------------------------+
| SimpleHashAlgorithm |  | CryptoSecureHashAlgorithm |
+---------------------+  +---------------------------+
| +calculateHashCode  |  | +calculateHashCode        |
|   (String): String  |  |   (String): String        |
+---------------------+  +---------------------------+
```

### After: Bridge Pattern Design

```mermaid
classDiagram
    class ExamSystem {
        +readFile(String) String
        +hashFile(String, Hashing) String
    }
    
    class Hashing {
        <<abstract>>
        -implementation: HashFunction
        +hashDocument(String) String
        +getImplementation() HashFunction
    }
    
    class PreviewHashing {
        +hashDocument(String) String
    }
    
    class EnterpriseHashing {
        +hashDocument(String) String
    }
    
    class HashFunction {
        <<interface>>
        +calculateHashCode(String) String
    }
    
    class SimpleHashAlgorithm {
        +calculateHashCode(String) String
    }
    
    class CryptoSecureHashAlgorithm {
        +calculateHashCode(String) String
    }
    
    ExamSystem ..> Hashing : uses
    Hashing *-- HashFunction : has-a (bridge)
    PreviewHashing --|> Hashing : extends
    EnterpriseHashing --|> Hashing : extends
    SimpleHashAlgorithm ..|> HashFunction : implements
    CryptoSecureHashAlgorithm ..|> HashFunction : implements
```

**Text representation:**
```
+-------------------------------------------+
|              ExamSystem                   |
+-------------------------------------------+
| +readFile(String): String                 |
| +hashFile(String, Hashing): String        |
+-------------------------------------------+
                    |
                    | uses
                    v
+-------------------------------------------+
|        <<abstract>> Hashing               |
+-------------------------------------------+
| -implementation: HashFunction             |
+-------------------------------------------+
| +hashDocument(String): String             |
| +getImplementation(): HashFunction        |
+-------------------------------------------+
        ^                           |
        | extends                   | has-a (bridge)
        |                           v
+-------+-------+           +---------------------------+
|               |           | <<interface>> HashFunction|
|               |           +---------------------------+
v               v           | +calculateHashCode        |
+-----------------+  +-------------------+  |   (String): String        |
| PreviewHashing  |  | EnterpriseHashing |  +---------------------------+
+-----------------+  +-------------------+              ^
| +hashDocument   |  | +hashDocument     |              | implements
|  (String):String|  |  (String):String  |    +---------+---------+
+-----------------+  +-------------------+    |                   |
        |                    |     +---------------------+  +---------------------------+
        |                    |     | SimpleHashAlgorithm |  | CryptoSecureHashAlgorithm |
        | uses               | uses+---------------------+  +---------------------------+
        v                    v
 SimpleHashAlgorithm   CryptoSecureHashAlgorithm
```

**Key Relationships:**
- `ExamSystem` uses `Hashing` (abstraction)
- `Hashing` has-a `HashFunction` (the bridge to implementation)
- `PreviewHashing` extends `Hashing` and uses `SimpleHashAlgorithm`
- `EnterpriseHashing` extends `Hashing` and uses `CryptoSecureHashAlgorithm`

---

## Task Breakdown

---

### Task 1: Implement `Hashing` Abstract Class (3 of 3 tests passing)

**Objective**: Create a new abstract class `Hashing` that serves as the "bridge" abstraction, holding a reference to a `HashFunction` implementation.

**Requirements:**
- Private field `implementation` of type `HashFunction`
- Constructor accepting `HashFunction` parameter
- Getter method `getImplementation()`
- Abstract method `hashDocument(String)`

#### Step 1.1: Create the file

Create new file: `src/de/tum/cit/aet/pse/Hashing.java`

#### Step 1.2: Add the class implementation

**Before:** *File did not exist*

**After:** `Hashing.java`
```java
package de.tum.cit.aet.pse;

public abstract class Hashing 
{
    private HashFunction implementation;

    public Hashing(HashFunction hashFunction)
    {
        this.implementation = hashFunction;
    }

    public HashFunction getImplementation() {
        return implementation;
    }

    public abstract String hashDocument(String document);
}
```

#### Git Diff (from `git diff cd04a0b..HEAD -- src/de/tum/cit/aet/pse/Hashing.java`)
```diff
diff --git a/src/de/tum/cit/aet/pse/Hashing.java b/src/de/tum/cit/aet/pse/Hashing.java
new file mode 100644
--- /dev/null
+++ b/src/de/tum/cit/aet/pse/Hashing.java
@@ -0,0 +1,17 @@
+package de.tum.cit.aet.pse;
+
+public abstract class Hashing 
+{
+    private HashFunction implementation;
+
+    public Hashing(HashFunction hashFunction)
+    {
+        this.implementation = hashFunction;
+    }
+
+    public HashFunction getImplementation() {
+        return implementation;
+    }
+
+    public abstract String hashDocument(String document);
+}
```

#### Changes Summary
| Line | Change Type | Content |
|------|-------------|---------|
| 1 | + Added | `package de.tum.cit.aet.pse;` |
| 3-4 | + Added | `public abstract class Hashing {` |
| 5 | + Added | `private HashFunction implementation;` |
| 7-10 | + Added | Constructor `public Hashing(HashFunction hashFunction)` |
| 12-14 | + Added | Getter `public HashFunction getImplementation()` |
| 16 | + Added | Abstract method `public abstract String hashDocument(String document);` |

---

### Task 2: Implement `PreviewHashing` (4 of 4 tests passing)

**Objective**: Create a subclass of `Hashing` that:
- Uses `SimpleHashAlgorithm` as its implementation
- Throws `IllegalArgumentException` if document exceeds 1000 characters

**Requirements:**
- Extend `Hashing`
- Constant for maximum length: 1000
- Call `super(new SimpleHashAlgorithm())` in constructor
- Override `hashDocument()` with length validation

#### Step 2.1: Create the file

Create new file: `src/de/tum/cit/aet/pse/PreviewHashing.java`

#### Step 2.2: Add the class implementation

**Before:** *File did not exist*

**After:** `PreviewHashing.java`
```java
package de.tum.cit.aet.pse;

public class PreviewHashing extends Hashing {
    private static final int PREVIEW_HASH_MAXIMUM_LENGTH = 1000;
    public PreviewHashing() {
        super(new SimpleHashAlgorithm());
    }

    @Override
    public String hashDocument(String document) {
        if (document.length() <= PREVIEW_HASH_MAXIMUM_LENGTH)
            return getImplementation().calculateHashCode(document);
        else
            throw new IllegalArgumentException("Document too long for Preview Hashing");
    }
}
```

#### Git Diff (from `git diff cd04a0b..HEAD -- src/de/tum/cit/aet/pse/PreviewHashing.java`)
```diff
diff --git a/src/de/tum/cit/aet/pse/PreviewHashing.java b/src/de/tum/cit/aet/pse/PreviewHashing.java
new file mode 100644
--- /dev/null
+++ b/src/de/tum/cit/aet/pse/PreviewHashing.java
@@ -0,0 +1,16 @@
+package de.tum.cit.aet.pse;
+
+public class PreviewHashing extends Hashing {
+    private static final int PREVIEW_HASH_MAXIMUM_LENGTH = 1000;
+    public PreviewHashing() {
+        super(new SimpleHashAlgorithm());
+    }
+
+    @Override
+    public String hashDocument(String document) {
+        if (document.length() <= PREVIEW_HASH_MAXIMUM_LENGTH)
+            return getImplementation().calculateHashCode(document);
+        else
+            throw new IllegalArgumentException("Document too long for Preview Hashing");
+    }
+}
```

#### Changes Summary
| Line | Change Type | Content |
|------|-------------|---------|
| 1 | + Added | `package de.tum.cit.aet.pse;` |
| 3 | + Added | `public class PreviewHashing extends Hashing {` |
| 4 | + Added | `private static final int PREVIEW_HASH_MAXIMUM_LENGTH = 1000;` |
| 5-7 | + Added | Constructor calling `super(new SimpleHashAlgorithm())` |
| 9-15 | + Added | Override `hashDocument()` with length check |

---

### Task 3: Implement `EnterpriseHashing` (3 of 3 tests passing)

**Objective**: Create a subclass of `Hashing` that:
- Uses `CryptoSecureHashAlgorithm` as its implementation
- Has no document length restrictions

**Requirements:**
- Extend `Hashing`
- Call `super(new CryptoSecureHashAlgorithm())` in constructor
- Override `hashDocument()` without restrictions

#### Step 3.1: Create the file

Create new file: `src/de/tum/cit/aet/pse/EnterpriseHashing.java`

#### Step 3.2: Add the class implementation

**Before:** *File did not exist*

**After:** `EnterpriseHashing.java`
```java
package de.tum.cit.aet.pse;

public class EnterpriseHashing extends Hashing {
    public EnterpriseHashing() {
        super(new CryptoSecureHashAlgorithm());
    }

    @Override
    public String hashDocument(String document) {
        return getImplementation().calculateHashCode(document);
    }
}
```

#### Git Diff (from `git diff cd04a0b..HEAD -- src/de/tum/cit/aet/pse/EnterpriseHashing.java`)
```diff
diff --git a/src/de/tum/cit/aet/pse/EnterpriseHashing.java b/src/de/tum/cit/aet/pse/EnterpriseHashing.java
new file mode 100644
--- /dev/null
+++ b/src/de/tum/cit/aet/pse/EnterpriseHashing.java
@@ -0,0 +1,12 @@
+package de.tum.cit.aet.pse;
+
+public class EnterpriseHashing extends Hashing {
+    public EnterpriseHashing() {
+        super(new CryptoSecureHashAlgorithm());
+    }
+
+    @Override
+    public String hashDocument(String document) {
+        return getImplementation().calculateHashCode(document);
+    }
+}
```

#### Changes Summary
| Line | Change Type | Content |
|------|-------------|---------|
| 1 | + Added | `package de.tum.cit.aet.pse;` |
| 3 | + Added | `public class EnterpriseHashing extends Hashing {` |
| 4-6 | + Added | Constructor calling `super(new CryptoSecureHashAlgorithm())` |
| 8-11 | + Added | Override `hashDocument()` delegating to implementation |

---

### Task 4: Implement `readFile` in `ExamSystem` (1 of 1 tests passing)

**Objective**: Implement file reading functionality that:
- Reads file content and returns it as a String
- Catches exceptions and throws `RuntimeException` with descriptive message

**Requirements:**
- Use `java.nio.file.Files.readString()` 
- Wrap in try-catch for `IOException`
- Throw `RuntimeException` with descriptive error message

#### Step 4.1: Add required imports

**Before (line 1-4):**
```java
package de.tum.cit.aet.pse;

import java.nio.file.Path;
```

**After (line 1-6):**
```java
package de.tum.cit.aet.pse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
```

#### Step 4.2: Implement the `readFile` method

**Before:**
```java
public static String readFile(String filepath) {
    Path path = Path.of(filepath);
    // TODO 4: Return the content of the passed file as a String.
    return null;
}
```

**After:**
```java
public static String readFile(String filepath) {
    Path path = Path.of(filepath);
    // TODO 4: Return the content of the passed file as a String.
    try {
        String content = Files.readString(path);
        return content;
    } catch (IOException e) {
        throw new RuntimeException("Error reading file: " + filepath, e);
    }
}
```

#### Git Diff (relevant portion)
```diff
 package de.tum.cit.aet.pse;
 
+import java.nio.file.Files;
 import java.nio.file.Path;
+import java.io.IOException;

 ...

 public static String readFile(String filepath) {
     Path path = Path.of(filepath);
     // TODO 4: Return the content of the passed file as a String.
-    return null;
+    try {
+        String content = Files.readString(path);
+        return content;
+    } catch (IOException e) {
+        throw new RuntimeException("Error reading file: " + filepath, e);
+    }
 }
```

#### Changes Summary
| Location | Change Type | Description |
|----------|-------------|-------------|
| Imports | + Added | `import java.nio.file.Files;` |
| Imports | + Added | `import java.io.IOException;` |
| readFile() | - Deleted | `return null;` |
| readFile() | + Added | Try-catch block with `Files.readString(path)` |
| readFile() | + Added | `RuntimeException` thrown on error with filepath info |

---

### Task 5: Adapt `hashFile` in `ExamSystem` (2 of 2 tests passing)

**Objective**: Change method signature to accept `Hashing` instead of `HashFunction`

**Requirements:**
- Change parameter type from `HashFunction` to `Hashing`
- Call `hashDocument()` instead of `calculateHashCode()`

#### Step 5.1: Modify the method signature and body

**Before:**
```java
// TODO 5: Change signature, make use of the bridge pattern
public static String hashFile(String document, HashFunction hashFunction) {
    return hashFunction.calculateHashCode(document);
}
```

**After:**
```java
// TODO 5: Change signature, make use of the bridge pattern
public static String hashFile(String document, Hashing hashing) {
    return hashing.hashDocument(document);
}
```

#### Git Diff (relevant portion)
```diff
 // TODO 5: Change signature, make use of the bridge pattern
-public static String hashFile(String document, HashFunction hashFunction) {
-    return hashFunction.calculateHashCode(document);
+public static String hashFile(String document, Hashing hashing) {
+    return hashing.hashDocument(document);
 }
```

#### Changes Summary
| Location | Change Type | Before | After |
|----------|-------------|--------|-------|
| Parameter | Modified | `HashFunction hashFunction` | `Hashing hashing` |
| Method call | Modified | `hashFunction.calculateHashCode(document)` | `hashing.hashDocument(document)` |

---

### Task 6: Adapt `main` method in `ExamSystem` (1 of 1 tests passing)

**Objective**: Update main method to use `PreviewHashing` and `EnterpriseHashing` instead of direct `HashFunction` implementations

**Requirements:**
- Replace `HashFunction simpleHash = new SimpleHashAlgorithm()` with `Hashing previewHashing = new PreviewHashing()`
- Replace `HashFunction cryptoSecureHash = new CryptoSecureHashAlgorithm()` with `Hashing enterpriseHashing = new EnterpriseHashing()`
- Update all method calls with new variable names

#### Step 6.1: Replace SimpleHashAlgorithm with PreviewHashing

**Before:**
```java
// TODO 6: Change SimpleHash to PreviewHashing
HashFunction simpleHash = new SimpleHashAlgorithm();

System.out.println(hashFile(file1, simpleHash));
try {
    System.out.println(hashFile(file2, simpleHash));
    throw new IllegalStateException("Hashing this file with preview hashing should not work!");
} catch (IllegalArgumentException e) {
    e.printStackTrace();
}
```

**After:**
```java
// TODO 6: Change SimpleHash to PreviewHashing
Hashing previewHashing = new PreviewHashing();

System.out.println(hashFile(file1, previewHashing));
try {
    System.out.println(hashFile(file2, previewHashing));
    throw new IllegalStateException("Hashing this file with preview hashing should not work!");
} catch (IllegalArgumentException e) {
    e.printStackTrace();
}
```

#### Step 6.2: Replace CryptoSecureHashAlgorithm with EnterpriseHashing

**Before:**
```java
// TODO 6: Change CryptoSecureHashAlgorithm to EnterpriseHashing
HashFunction cryptoSecureHash = new CryptoSecureHashAlgorithm();

System.out.println(hashFile(file1, cryptoSecureHash));
System.out.println(hashFile(file2, cryptoSecureHash));
```

**After:**
```java
// TODO 6: Change CryptoSecureHashAlgorithm to EnterpriseHashing
Hashing enterpriseHashing = new EnterpriseHashing();

System.out.println(hashFile(file1, enterpriseHashing));
System.out.println(hashFile(file2, enterpriseHashing));
```

#### Git Diff (relevant portion)
```diff
 // TODO 6: Change SimpleHash to PreviewHashing
-HashFunction simpleHash = new SimpleHashAlgorithm();
+Hashing previewHashing = new PreviewHashing();

-System.out.println(hashFile(file1, simpleHash));
+System.out.println(hashFile(file1, previewHashing));
 try {
-    System.out.println(hashFile(file2, simpleHash));
+    System.out.println(hashFile(file2, previewHashing));
     throw new IllegalStateException("Hashing this file with preview hashing should not work!");
 } catch (IllegalArgumentException e) {
     e.printStackTrace();
 }

 // TODO 6: Change CryptoSecureHashAlgorithm to EnterpriseHashing
-HashFunction cryptoSecureHash = new CryptoSecureHashAlgorithm();
+Hashing enterpriseHashing = new EnterpriseHashing();

-System.out.println(hashFile(file1, cryptoSecureHash));
-System.out.println(hashFile(file2, cryptoSecureHash));
+System.out.println(hashFile(file1, enterpriseHashing));
+System.out.println(hashFile(file2, enterpriseHashing));
```

#### Changes Summary
| Location | Change Type | Before | After |
|----------|-------------|--------|-------|
| Variable declaration | Modified | `HashFunction simpleHash = new SimpleHashAlgorithm()` | `Hashing previewHashing = new PreviewHashing()` |
| Variable declaration | Modified | `HashFunction cryptoSecureHash = new CryptoSecureHashAlgorithm()` | `Hashing enterpriseHashing = new EnterpriseHashing()` |
| Method calls | Modified | `hashFile(file1, simpleHash)` | `hashFile(file1, previewHashing)` |
| Method calls | Modified | `hashFile(file2, simpleHash)` | `hashFile(file2, previewHashing)` |
| Method calls | Modified | `hashFile(file1, cryptoSecureHash)` | `hashFile(file1, enterpriseHashing)` |
| Method calls | Modified | `hashFile(file2, cryptoSecureHash)` | `hashFile(file2, enterpriseHashing)` |

---

## Complete File Changes Summary

### New Files Created

| File | Purpose |
|------|---------|
| `Hashing.java` | Abstract bridge class |
| `PreviewHashing.java` | Limited preview version (SimpleHashAlgorithm) |
| `EnterpriseHashing.java` | Full enterprise version (CryptoSecureHashAlgorithm) |

### Modified Files

| File | Changes |
|------|---------|
| `ExamSystem.java` | Added imports, implemented `readFile()`, modified `hashFile()` signature, updated `main()` |

### Unchanged Files

| File | Status |
|------|--------|
| `HashFunction.java` | No changes required |
| `SimpleHashAlgorithm.java` | No changes required |
| `CryptoSecureHashAlgorithm.java` | No changes required |

---

## Test Data

| File | Characters | Can be hashed by |
|------|------------|------------------|
| `short_exam.txt` | 65 | PreviewHashing ✓, EnterpriseHashing ✓ |
| `long_exam.txt` | 1814 | PreviewHashing ✗ (>1000), EnterpriseHashing ✓ |

---

## Bridge Pattern Benefits in This Implementation

1. **Decoupling**: The `Hashing` abstraction is decoupled from `HashFunction` implementations
2. **Extensibility**: New hashing versions (like `PremiumHashing`) can be added without modifying existing code
3. **Single Responsibility**: Each class has a clear, single purpose
4. **Easy Algorithm Switching**: Algorithms can be swapped at runtime via constructor injection
5. **Testing**: Each component can be tested independently

---

## Git Commit History

```
beb0997 fixed warning
22bbfac try to fix main again
0d32a6e try to fix main
8312e3e try fix again
35bbe0f try to pass test
d2915c1 fixed error throwing
723d664 fixed implementation
c79a845 fixed error
53e64ea done
cd04a0b Set up template for exercise  ← Initial template
```

---

## Quick Reference: Key Pattern Elements

| Bridge Pattern Element | Implementation in This Project |
|------------------------|--------------------------------|
| **Abstraction** | `Hashing` (abstract class) |
| **Refined Abstraction** | `PreviewHashing`, `EnterpriseHashing` |
| **Implementor** | `HashFunction` (interface) |
| **Concrete Implementor** | `SimpleHashAlgorithm`, `CryptoSecureHashAlgorithm` |
| **Client** | `ExamSystem` |
