# FIX Protocol Parser

A high-performance FIX protocol parser for low-latency environments with minimal memory footprint. Efficiently parses raw FIX messages (`byte[]`) while validating against version-specific dictionaries.

# Example Usage

```java
ProtocolVersion version = ProtocolVersion.FIX44;
Parser parser = new Parser(version);
Message message = parser.parse(rawFixBytes);
```

# Specifications Sources
This library utilizes FIX protocol specifications from the QuickFIX/J project:

- [FIX 4.4 Dictionary](https://github.com/quickfix-j/quickfixj/blob/master/quickfixj-messages/quickfixj-messages-fix44/src/main/resources/FIX44.xml)
- [FIX 4.2 Dictionary](https://github.com/quickfix-j/quickfixj/blob/master/quickfixj-messages/quickfixj-messages-fix42/src/main/resources/FIX42.xml)

## Performance Characteristics

This implementation achieves high performance and small memory footprint through:

1. **Zero-Copy Parsing**  
   Operates directly on the raw byte array without creating intermediate buffers or string copies until absolutely necessary.

2. **Single-Pass Processing**  
   Performs all critical operations (field parsing, checksum calculation, validation) in a single linear pass through the message.

3. **On-the-Fly Checksum Calculation**  
   Computes the checksum incrementally while parsing fields, eliminating the need for a separate validation pass.

4. **Efficient Tag Parsing**  
   Converts tag numbers from ASCII to integer using optimized arithmetic operations rather than string conversion.

5. **Memory Reuse**
    - Dictionary stored as singleton instances per protocol version
    - Field values created only when needed
    - No temporary objects created during parsing

6. **Optimized Delimiter Search**  
   Custom `indexOf` implementation avoids object allocations and complex regex operations.

## Benchmarks

This section outlines the methodology and results of the performance benchmarks for the `Parser` class. The goal is to measure the efficiency of the parser under different scenarios and ensure consistent performance.

### Testing Methodology
1. **Warm-up Phase**:  
   Before measuring performance, the parser executes 1,000 warm-up iterations to allow the JVM's JIT compiler to optimize the code and avoid cold-start inaccuracies.

2. **Benchmark Execution**:  
   Each test case runs the parser 10 times, and individual execution times (in nanoseconds) are recorded. The results are averaged and converted to microseconds for readability.

3. **Assertion Check**:  
   The test asserts that the average time per parse operation remains below 1 millisecond (1,000 microseconds) to catch performance regressions.

### Results
The following table summarizes the benchmark results for three test cases:

| Test File              | Average Parse Time (Microseconds) |
|------------------------|-----------------------------------|
| `examples/example1.bin`| 1.475 µs                          |
| `examples/example2.bin`| 0.9665 µs                         |
| `examples/example3.bin`| 1.4543 µs                         |

- **Total Tests Passed**: 3/3.
- All results are well within the acceptable threshold of `< 1000 µs`.

### Run Locally
```bash
mvn clean test -Dtest=ParserBenchmark