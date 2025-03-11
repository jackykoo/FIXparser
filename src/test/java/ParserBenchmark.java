import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserBenchmark {

    private static Parser parser;

    @BeforeAll
    public static void setup() {
        parser = new Parser(ProtocolVersion.FIX44);
    }

    @ParameterizedTest
    @ValueSource(strings = {"examples/example1.bin", "examples/example2.bin", "examples/example3.bin"})
    public void benchmarkParser(String testFile) throws IOException {
        byte[] msg = loadFile(testFile);

        // Warm-up phase
        for (int i = 0; i < 1000; i++) {
            parser.parse(msg);
        }

        // Benchmark phase
        int numRuns = 10;
        long[] runningTimes = new long[numRuns];
        for (int i = 0; i < numRuns; i++) {
            long startTime = System.nanoTime();
            parser.parse(msg);
            long endTime = System.nanoTime();
            runningTimes[i] = endTime - startTime;
        }

        double averageTime = Arrays.stream(runningTimes).average().orElse(0) / 1000.0;
        System.out.println("Average running time: " + averageTime + " microseconds\n");

        // Assert that the average time is within an acceptable range
        assertTrue(averageTime < 1000, "Performance degraded: Average time exceeded 1 millisecond");
    }

    private byte[] loadFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] msg = new byte[(int) file.length()];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            inputStream.read(msg);
        }
        return msg;
    }
}