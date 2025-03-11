import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class TagInfoTest {

    private final int FIX_MSG_FIRST_TAG = 8;

    @Test
    void getNumber() {
        TagInfo tagInfo = new TagInfo(FIX_MSG_FIRST_TAG, "BeginString", "STRING");
        assertEquals(FIX_MSG_FIRST_TAG, tagInfo.getNumber());
    }

    @Test
    void getName() {
        TagInfo tagInfo = new TagInfo(FIX_MSG_FIRST_TAG, "BeginString", "STRING");
        assertEquals("BeginString", tagInfo.getName());
    }

    @Test
    void getType() {
        TagInfo tagInfo = new TagInfo(FIX_MSG_FIRST_TAG, "BeginString", "STRING");
        assertEquals("STRING", tagInfo.getType());
    }

    @Test
    void addValue() {
        TagInfo tagInfo = new TagInfo(13, "CommType", "CHAR");
        assertFalse(tagInfo.hasValues());
        tagInfo.addValue(new TagInfo.Value("1", "PER_UNIT"));
        assertTrue(tagInfo.hasValues());
    }

    @Test
    void hasValues() {
        TagInfo tagInfo = new TagInfo(13, "CommType", "CHAR");
        assertFalse(tagInfo.hasValues());
        tagInfo.addValue(new TagInfo.Value("1", "PER_UNIT"));
        assertTrue(tagInfo.hasValues());
    }

    @Test
    void hasValue() {
        TagInfo tagInfo = new TagInfo(13, "CommType", "CHAR");
        assertFalse(tagInfo.hasValues());
        tagInfo.addValue(new TagInfo.Value("1", "PER_UNIT"));
        assertTrue(tagInfo.hasValues());
    }

    @Test
    void getValue() {
        TagInfo tagInfo = new TagInfo(13, "CommType", "CHAR");
        assertFalse(tagInfo.hasValues());
        tagInfo.addValue(new TagInfo.Value("1", "PER_UNIT"));
        assertTrue(tagInfo.hasValues());
        TagInfo.Value value = tagInfo.getValue("2");
        assertNull(value);
        TagInfo.Value value2 = tagInfo.getValue("1");
        assertEquals("1", value2.value());
        assertEquals("PER_UNIT", value2.description());
    }
}