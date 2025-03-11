import java.util.LinkedHashMap;

public final class TagInfo {

    private final int number;
    private final String name;
    private final String type;
    private final LinkedHashMap<String, Value> values;

    public TagInfo(int number, String name, String type) {
        this.number = number;
        this.name = name;
        this.type = type;
        values = new LinkedHashMap<>();
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void addValue(Value value) {
        values.put(value.value(), value);
    }

    public boolean hasValues() {
        return !values.isEmpty();
    }

    public boolean hasValue(String value) {
        return values.containsKey(value);
    }

    public Value getValue(String value) {
        return values.get(value);
    }

    public record Value(String value, String description) {}
}
