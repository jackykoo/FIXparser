import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageDisplayer {

    private static final Map<ProtocolVersion, MessageDisplayer> instances = new ConcurrentHashMap<>();

    private final Dictionary dictionary;

    private MessageDisplayer(ProtocolVersion version) {
        this.dictionary = Dictionary.getInstance(version);
    }

    public static MessageDisplayer getInstance(ProtocolVersion version) {
        return instances.computeIfAbsent(version, MessageDisplayer::new);
    }

    public void displayMessage(Message message) {
        List<Record> results = new ArrayList<>();

        processSection(message.getHeader(), results);
        processSection(message.getBody(), results);
        processSection(message.getTrailer(), results);

        results.forEach(System.out::println);
    }

    private void processSection(Map<Integer, String> section, List<Record> results) {
        section.forEach((number, value) -> {
            TagInfo tagInfo = dictionary.getTagInfo(number);
            if (tagInfo.hasValues()) {
                String description = tagInfo.getValue(value).description();
                results.add(new Record(number, tagInfo.getName(), value, description));
            } else {
                results.add(new Record(number, tagInfo.getName(), value));
            }
        });
    }

    public static class Record {
        private final int number;
        private final String name;
        private final String value;
        private final String valueDescription;

        public Record(int number, String name, String value, String valueDescription) {
            this.number = number;
            this.name = name;
            this.value = value;
            this.valueDescription = valueDescription;
        }

        public Record(int number, String name, String value) {
            this(number, name, value, "");
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(number).append(":").append(name).append(" = ").append(value);
            if (!valueDescription.isEmpty()) {
                sb.append(" (").append(valueDescription).append(")");
            }
            return sb.toString();
        }
    }
}
