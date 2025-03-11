import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DictionaryTest {

    private Dictionary dictionary;
    private ProtocolVersion testVersion;

    @BeforeEach
    void setUp() {
        testVersion = ProtocolVersion.FIX44;
        dictionary = Dictionary.getInstance(testVersion);
    }

    @Test
    void testGetInstance() {
        assertNotNull(dictionary);

        Dictionary anotherInstance = Dictionary.getInstance(testVersion);
        assertSame(dictionary, anotherInstance);
    }

    @Test
    void testGetTagInfo() {
        TagInfo tagInfo = dictionary.getTagInfo(1);

        assertNotNull(tagInfo);
        assertEquals("Account", tagInfo.getName());
        assertEquals("STRING", tagInfo.getType());
        assertNull(dictionary.getTagInfo(999));
    }

    @Test
    void testIsHeaderTag() {
        assertTrue(dictionary.isHeaderTag("BeginString"));
        assertTrue(dictionary.isHeaderTag("BodyLength"));
        assertTrue(dictionary.isHeaderTag("HopCompID"));

        assertFalse(dictionary.isHeaderTag("Account"));
    }

    @Test
    void testIsTrailerTag() {
        assertTrue(dictionary.isTrailerTag("Signature"));
        assertTrue(dictionary.isTrailerTag("CheckSum"));

        assertFalse(dictionary.isTrailerTag("Account"));
    }
}
