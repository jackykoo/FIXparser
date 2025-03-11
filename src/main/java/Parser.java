import java.nio.charset.StandardCharsets;

/**
 * Parses FIX protocol messages into a structured {@link Message} object using a version-specific dictionary.
 * <p>
 * This class handles tag/value validation, checksum verification, and categorizes fields into header, body,
 * and trailer sections based on the FIX specification. Example usage:
 * <pre>
 * {@code
 * ProtocolVersion version = ProtocolVersion.FIX44;
 * Parser parser = new Parser(version);
 * Message message = parser.parse(rawFixBytes);
 * }
 * </pre>
 * </p>
 */
public class Parser {
    private final Dictionary dictionary;

    private static final byte SOH = 1;
    private static final byte EQUALS = '=';
    private static final byte CHECKSUM_FIELD = 10;

    public Parser(ProtocolVersion version) {
        this.dictionary = Dictionary.getInstance(version);
    }

    public Message parse(byte[] msg) {
        Message fixMessage = new Message();
        int start = 0;
        int end;
        int calculatedChecksum = 0;
        int providedChecksum = -1;

        while (start < msg.length) {
            end = indexOf(msg, SOH, start);
            if (end == -1) {
                end = msg.length;
            }

            int eqPos = indexOf(msg, EQUALS, start, end);
            if (eqPos == -1) {
                throw new ParserException("Invalid data: No '=' found in field");
            }

            int tag = parseTag(msg, start, eqPos);

            String value = new String(msg, eqPos + 1, end - (eqPos + 1), StandardCharsets.US_ASCII);

            TagInfo tagInfo = dictionary.getTagInfo(tag);
            if (tagInfo == null) {
                throw new ParserException("Invalid data: Unknown field number " + tag);
            }
            if (tagInfo.hasValues() && !tagInfo.hasValue(value)) {
                throw new ParserException("Invalid data: Invalid value for field " + tag);
            }

            if (tag != CHECKSUM_FIELD) {
                calculatedChecksum = updateChecksum(calculatedChecksum, msg, start, end);
            } else {
                providedChecksum = Integer.parseInt(value);
            }

            addFieldToMessage(fixMessage, tagInfo, tag, value);

            start = end + 1;
        }

        calculatedChecksum %= 256;

        if (providedChecksum != -1 && calculatedChecksum != providedChecksum) {
            throw new ParserException("Invalid data: Checksum mismatch. Calculated: " + calculatedChecksum + ", Provided: " + providedChecksum);
        }

        return fixMessage;
    }

    /**
     * Parses a tag number from a byte array.
     * <p>
     * This method extracts the tag number from the byte array by converting the ASCII digits
     * between the start index and the position of the '=' character into an integer.
     * </p>
     *
     * @param msg The byte array containing the FIX message.
     * @param start The starting index (inclusive) of the tag in the byte array.
     * @param eqPos The index of the '=' character in the byte array.
     * @return The parsed tag number as an integer.
     */
    private int parseTag(byte[] msg, int start, int eqPos) {
        int tag = 0;
        for (int i = start; i < eqPos; i++) {
            tag = (tag * 10 + (msg[i] - '0'));
        }
        return tag;
    }

    /**
     * Updates the checksum for the FIX message.
     * <p>
     * This method calculates the checksum by summing the byte values of the message between the start
     * and end indices, including the SOH (Start of Header) delimiter. The checksum is used to verify
     * the integrity of the FIX message.
     * </p>
     *
     * @param checksum The current checksum value.
     * @param msg The byte array containing the FIX message.
     * @param start The starting index (inclusive) of the field in the byte array.
     * @param end The ending index (exclusive) of the field in the byte array.
     * @return The updated checksum value.
     */
    private int updateChecksum(int checksum, byte[] msg, int start, int end) {
        for (int i = start; i < end; i++) {
            checksum += msg[i] & 0xFF;
        }
        checksum += SOH;
        return checksum;
    }

    /**
     * Adds a parsed field to the appropriate section (header, body, or trailer) of the FIX message.
     * <p>
     * This method categorizes the field based on the tag's metadata from the FIX dictionary and adds it
     * to the corresponding section of the {@link Message} object.
     * </p>
     *
     * @param fixMessage The {@link Message} object to which the field will be added.
     * @param tagInfo The {@link TagInfo} object containing metadata about the tag.
     * @param tag The tag number of the field.
     * @param value The value associated with the tag.
     */
    private void addFieldToMessage(Message fixMessage, TagInfo tagInfo, int tag, String value) {
        if (dictionary.isHeaderTag(tagInfo.getName())) {
            fixMessage.addHeaderField(tag, value);
        } else if (dictionary.isTrailerTag(tagInfo.getName())) {
            fixMessage.addTrailerField(tag, value);
        } else {
            fixMessage.addBodyField(tag, value);
        }
    }

    /**
     * Finds the index of a specific byte in the byte array within a given range.
     *
     * @param array The byte array to search.
     * @param target The byte to find.
     * @param start The starting index (inclusive).
     * @param end The ending index (exclusive).
     * @return The index of the target byte, or -1 if not found.
     */
    private int indexOf(byte[] array, byte target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the index of a specific byte in the byte array.
     *
     * @param array The byte array to search.
     * @param target The byte to find.
     * @param start The starting index (inclusive).
     * @return The index of the target byte, or -1 if not found.
     */
    private int indexOf(byte[] array, byte target, int start) {
        return indexOf(array, target, start, array.length);
    }
}
