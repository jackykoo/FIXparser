import java.util.LinkedHashMap;
import java.util.Map;

public class Message {

    private final Map<Integer, String> header;
    private final Map<Integer, String> body;
    private final Map<Integer, String> trailer;

    public Message() {
        this.header = new LinkedHashMap<>();
        this.body = new LinkedHashMap<>();
        this.trailer = new LinkedHashMap<>();
    }

    public void addHeaderField(int tag, String value) {
        header.put(tag, value);
    }

    public void addBodyField(int tag, String value) {
        body.put(tag, value);
    }

    public void addTrailerField(int tag, String value) {
        trailer.put(tag, value);
    }

    public Map<Integer, String> getHeader() {
        return header;
    }

    public Map<Integer, String> getBody() {
        return body;
    }

    public Map<Integer, String> getTrailer() {
        return trailer;
    }
}
