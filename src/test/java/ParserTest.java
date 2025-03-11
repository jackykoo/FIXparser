import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    private static Parser parser;

    @BeforeAll
    public static void setUp() {
        parser = new Parser(ProtocolVersion.FIX44);
    }

    @Test
    public void testParseValidMessage() {
        // Arrange
        byte[] msg = "8=FIX.4.4\u00019=65\u000135=A\u000134=5\u000149=BANZAI\u000152=20231123-17:20:39.148\u000156=EXEC\u000198=0\u0001108=30\u000110=224\u0001"
                .getBytes(StandardCharsets.US_ASCII);

        // Act
        Message result = parser.parse(msg);

        // Assert
        assertNotNull(result);
        assertEquals("FIX.4.4", result.getHeader().get(8));
        assertEquals("30", result.getBody().get(108));
        assertEquals("224", result.getTrailer().get(10));
    }

    @Test
    public void testParseInvalidChecksum_ShouldThrowParserException() {
        // Arrange
        byte[] invalidMessage = "8=FIX.4.4\u00019=65\u000135=A\u000134=5\u000149=BANZAI\u000152=20231123-17:20:39.148\u000156=EXEC\u000198=0\u0001108=30\u000110=999\u0001"
                .getBytes(StandardCharsets.US_ASCII);

        // Act & Assert
        ParserException exception = assertThrows(ParserException.class, () -> parser.parse(invalidMessage),
                "Parsing a message with an invalid checksum should throw ParserException");

        assertNotNull(exception.getMessage());
    }

    @Test
    public void testParseInvalidMessageUnknownField() {
        // Arrange
        byte[] invalidMessage = "8=FIX.4.4\u00019=65\u000135=A\u000134=5\u000149=BANZAI\u000152=20231123-17:20:39.148\u000156=EXEC\u000198=0\u0001108=30\u0001999=UnknownField\u000110=224\u0001"
                .getBytes(StandardCharsets.US_ASCII);

        // Act & Assert
        ParserException exception = assertThrows(ParserException.class, () -> parser.parse(invalidMessage),
                "Parsing a message with an unknown field should throw ParserException");

        assertNotNull(exception.getMessage());
    }


    @Test
    public void testParseInvalidMessageInvalidFieldValue() {
        // Arrange
        byte[] invalidMessage = "8=FIX.4.4\u00019=65\u000135=A\u000134=5\u000149=BANZAI\u000152=20231123-17:20:39.148\u000156=EXEC\u000198=0\u0001108=InvalidValue\u000110=224\u0001"
                .getBytes(StandardCharsets.US_ASCII);

        // Act & Assert
        ParserException exception = assertThrows(ParserException.class, () -> parser.parse(invalidMessage),
                "Parsing a message with an invalid field value should throw ParserException");

        assertNotNull(exception.getMessage());
    }
}
