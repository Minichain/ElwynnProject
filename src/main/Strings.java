package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;

public class Strings {
    public static final String englishStringPath = "res/strings/strings-en.xml";
    public static final String spanishStringPath = "res/strings/strings-es.xml";
    public static final String catalanStringPath = "res/strings/strings-ca.xml";
    private static HashMap<String, String> stringHashMap = new HashMap<>();

    public static String getString(String stringName) {
        return stringHashMap.get(stringName);
    }

    public static String getString(String stringName, String... args) {
        String s = stringHashMap.get(stringName);
        String characterToReplace;
        for (int i = 0; i < args.length; i++) {
            characterToReplace = "%" + (i + 1) + "$";
            if (s.contains(characterToReplace) && args[i] != null) {
                s = s.replace(characterToReplace, args[i]);
            }
        }
        return s;
    }

    /**
     * This Method is called when the language has been changed.
     **/
    public static void updateStrings() {
        String pathName;
        switch (Parameters.getLanguage()) {
            case English:
            default:
                pathName = englishStringPath;
                break;
            case Spanish:
                pathName = spanishStringPath;
                break;
            case Catalan:
                pathName = catalanStringPath;
                break;
        }
        updateStrings(pathName);
    }

    public static void updateStrings(String pathName) {
        try {
            Log.l("Updating Strings");
            File xmlFile = new File(pathName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            NodeList nodeList = doc.getElementsByTagName("string");
            String key, value;
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) != null) {
                    Element element = (Element) nodeList.item(i);
                    key = element.getAttribute("name");
                    value = element.getTextContent();
                    if (stringHashMap.containsKey(key)) {
                        stringHashMap.replace(key, value);
                    } else {
                        stringHashMap.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error loading strings file");
        }
    }
}