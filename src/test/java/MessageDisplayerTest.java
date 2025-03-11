import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MessageDisplayerTest {

    private static ProtocolVersion version;

    @BeforeAll
    static void setUp() {
        version = ProtocolVersion.FIX44;
    }

    @Test
    void testGetInstance() {
        MessageDisplayer instance1 = MessageDisplayer.getInstance(version);
        MessageDisplayer instance2 = MessageDisplayer.getInstance(version);

        assertSame(instance1, instance2, "Instances should be the same");
    }

    @Test
    void testRecordToString() {
        MessageDisplayer.Record record1 = new MessageDisplayer.Record(1, "Field1", "Value1");
        assertEquals("1:Field1 = Value1", record1.toString());

        MessageDisplayer.Record record2 = new MessageDisplayer.Record(2, "Field2", "Value2", "Description2");
        assertEquals("2:Field2 = Value2 (Description2)", record2.toString());
    }
}