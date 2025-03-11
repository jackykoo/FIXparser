import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Dictionary {

    private final Map<Integer, TagInfo> tagInfoMap;

    private final Set<String> headerFields;
    private final Set<String> trailerFields;

    private static final Map<ProtocolVersion, Dictionary> instances = new ConcurrentHashMap<>();

    public static Dictionary getInstance(ProtocolVersion version) {
        return instances.computeIfAbsent(version, Dictionary::new);
    }

    private Dictionary(ProtocolVersion version) {
        String fileName = version.getResourceName();
        try (InputStream inputStream = Dictionary.class.getClassLoader().getResourceAsStream(fileName)) {
            Document document = parseXml(inputStream);
            this.tagInfoMap = readFields(document);
            this.headerFields = readHeaderFields(document);
            this.trailerFields = readTrailerFields(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize FIXDictionary", e);
        }
    }

    public TagInfo getTagInfo(int key) {
        return tagInfoMap.get(key);
    }

    public boolean isHeaderTag(String tagName) {
        return headerFields.contains(tagName);
    }

    public boolean isTrailerTag(String tagName) {
        return trailerFields.contains(tagName);
    }

    private Document parseXml(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();
        return document;
    }

    private Map<Integer, TagInfo> readFields(Document document) {
        Map<Integer, TagInfo> fieldsTable = new HashMap<>();

        Element fieldsElement = (Element) document.getElementsByTagName("fields").item(0);
        NodeList fieldsList = fieldsElement.getElementsByTagName("field");
        for (int i = 0; i < fieldsList.getLength(); i++) {
            Element fieldElement = (Element) fieldsList.item(i);
            int number = Integer.parseInt(fieldElement.getAttribute("number"));
            String name = fieldElement.getAttribute("name");
            String type = fieldElement.getAttribute("type");
            TagInfo tagInfo = new TagInfo(number, name, type);

            NodeList valueList = fieldElement.getElementsByTagName("value");
            for (int j = 0; j < valueList.getLength(); j++) {
                Element valueElement = (Element) valueList.item(j);
                String enumValue = valueElement.getAttribute("enum");
                String description = valueElement.getAttribute("description");
                TagInfo.Value value = new TagInfo.Value(enumValue, description);
                tagInfo.addValue(value);
            }
            fieldsTable.put(tagInfo.getNumber(), tagInfo);
        }

        return fieldsTable;
    }

    private Set<String> readHeaderFields(Document document) {
        Set<String> headerFields = new HashSet<>();
        Element headerElement = (Element) document.getElementsByTagName("header").item(0);

        NodeList headerFieldList = headerElement.getElementsByTagName("field");
        for (int i = 0; i < headerFieldList.getLength(); i++) {
            Element fieldElement = (Element) headerFieldList.item(i);
            String fieldName = fieldElement.getAttribute("name");
            headerFields.add(fieldName);
        }

        NodeList groupList = headerElement.getElementsByTagName("group");
        for (int i = 0; i < groupList.getLength(); i++) {
            Element groupElement = (Element) groupList.item(i);
            String groupName = groupElement.getAttribute("name");
            headerFields.add(groupName);

            NodeList groupFieldList = groupElement.getElementsByTagName("field");
            for (int j = 0; j < groupFieldList.getLength(); j++) {
                Element groupFieldElement = (Element) groupFieldList.item(j);
                String groupFieldName = groupFieldElement.getAttribute("name");
                headerFields.add(groupFieldName);
            }
        }

        return headerFields;
    }

    private Set<String> readTrailerFields(Document document) {
        Set<String> trailerFields = new HashSet<>();
        Element trailerElement = (Element) document.getElementsByTagName("trailer").item(0);

        NodeList trailerFieldList = trailerElement.getElementsByTagName("field");
        for (int i = 0; i < trailerFieldList.getLength(); i++) {
            Element fieldElement = (Element) trailerFieldList.item(i);
            String fieldName = fieldElement.getAttribute("name");
            trailerFields.add(fieldName);
        }


        return trailerFields;
    }
}
