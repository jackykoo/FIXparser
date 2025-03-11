import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class MessageTest {

    private Message message;

    @BeforeEach
    public void setUp() {
        message = new Message();
    }

    @Test
    public void testAddHeaderField() {
        message.addHeaderField(1, "HeaderValue1");
        message.addHeaderField(2, "HeaderValue2");

        Map<Integer, String> header = message.getHeader();
        assertEquals(2, header.size());
        assertEquals("HeaderValue1", header.get(1));
        assertEquals("HeaderValue2", header.get(2));
    }

    @Test
    public void testAddBodyField() {
        message.addBodyField(1, "BodyValue1");
        message.addBodyField(2, "BodyValue2");

        Map<Integer, String> body = message.getBody();
        assertEquals(2, body.size());
        assertEquals("BodyValue1", body.get(1));
        assertEquals("BodyValue2", body.get(2));
    }

    @Test
    public void testAddTrailerField() {
        message.addTrailerField(1, "TrailerValue1");
        message.addTrailerField(2, "TrailerValue2");

        Map<Integer, String> trailer = message.getTrailer();
        assertEquals(2, trailer.size());
        assertEquals("TrailerValue1", trailer.get(1));
        assertEquals("TrailerValue2", trailer.get(2));
    }

    @Test
    public void testHeaderSequence() {
        message.addHeaderField(1, "HeaderValue1");
        message.addHeaderField(2, "HeaderValue2");
        message.addHeaderField(3, "HeaderValue3");

        Map<Integer, String> header = message.getHeader();
        assertInstanceOf(LinkedHashMap.class, header, "Header should be a LinkedHashMap to preserve order");

        // Verify the sequence of entries
        int expectedTag = 1;
        for (Map.Entry<Integer, String> entry : header.entrySet()) {
            assertEquals(expectedTag, entry.getKey(), "Header tag sequence is incorrect");
            assertEquals("HeaderValue" + expectedTag, entry.getValue(), "Header value sequence is incorrect");
            expectedTag++;
        }
    }

    @Test
    public void testBodySequence() {
        message.addBodyField(1, "BodyValue1");
        message.addBodyField(2, "BodyValue2");
        message.addBodyField(3, "BodyValue3");

        Map<Integer, String> body = message.getBody();
        assertInstanceOf(LinkedHashMap.class, body, "Body should be a LinkedHashMap to preserve order");

        // Verify the sequence of entries
        int expectedTag = 1;
        for (Map.Entry<Integer, String> entry : body.entrySet()) {
            assertEquals(expectedTag, entry.getKey(), "Body tag sequence is incorrect");
            assertEquals("BodyValue" + expectedTag, entry.getValue(), "Body value sequence is incorrect");
            expectedTag++;
        }
    }

    @Test
    public void testTrailerSequence() {
        message.addTrailerField(1, "TrailerValue1");
        message.addTrailerField(2, "TrailerValue2");
        message.addTrailerField(3, "TrailerValue3");

        Map<Integer, String> trailer = message.getTrailer();
        assertInstanceOf(LinkedHashMap.class, trailer, "Trailer should be a LinkedHashMap to preserve order");

        // Verify the sequence of entries
        int expectedTag = 1;
        for (Map.Entry<Integer, String> entry : trailer.entrySet()) {
            assertEquals(expectedTag, entry.getKey(), "Trailer tag sequence is incorrect");
            assertEquals("TrailerValue" + expectedTag, entry.getValue(), "Trailer value sequence is incorrect");
            expectedTag++;
        }
    }
}