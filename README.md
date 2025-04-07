# Deep Copy Utility

This is my custom implementation of a deep copy utility.

It performs a full deep copy of:

- Primitive types and their wrappers
- Arrays
- Common collections (`List`, `Set`, etc.)
- Maps (`HashMap`, `TreeMap`, etc.)
- Regular objects, including those with inheritance
- Cyclic references (e.g., self-linked structures)

### How to use

Simply call:

```java
YourType copy = CopyUtility.deepCopy(originalObject);
```

### ⚠️ Notes

This implementation uses some risky mechanisms under the hood:
- Accessing private fields via reflection
- Skipping constructors via `sun.reflect.ReflectionFactory`

I **strongly recommend not to use this in production** without reviewing your security and performance requirements.

### ✅ Test coverage

Included test cases cover:

- Primitive and wrapper types
- Arrays
- Collections and maps
- POJOs and inheritance
- Cyclical object graphs


### 🚫 Limitations

- Does not support deep copying of:
    - `Optional`, `EnumSet`, `WeakHashMap`, `ConcurrentSkipListMap`
    - Streams, lambdas, or custom serialization logic
    - Final fields with no setters
- Will not copy native resources (e.g. `FileInputStream`, `Socket`)
- May break under Java module restrictions without `--add-opens` flags
- Uses internal APIs (`ReflectionFactory`) which may not work in all environments (e.g. GraalVM, Android)


Run `main()` in `CopyUtilityTest.java` to verify functionality.
