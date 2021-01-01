package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;

public class Strings {

    private static HashMap<String, String> stringHashMap = new HashMap<>();

    public static String getString(String stringName) {
        return stringHashMap.get(stringName);
    }

    public static String getString(String stringName, String... args) {
        String s = stringHashMap.get(stringName);
        String characterToReplace;
        for (int i = 0; i < args.length; i++) {
            characterToReplace = "%" + (i + 1) + "$";
            if (s.contains(characterToReplace)) {
                s = s.replace(characterToReplace, args[i]);
            }
        }
        return s;
    }

    /**
     * This Method is called when the language has been changed.
     **/
    public static void updateStrings() {
        try {
            Log.l("Updating Strings");
            String pathName = "";
            switch (Parameters.getLanguage()) {
                case ENGLISH:
                default:
                    pathName = "res/strings/strings-en.xml";
                    break;
                case SPANISH:
                    pathName = "res/strings/strings-es.xml";
                    break;
                case CATALAN:
                    pathName = "res/strings/strings-ca.xml";
                    break;
            }

            File xmlFile = new File(pathName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            stringHashMap.clear();

            NodeList nodeList = doc.getElementsByTagName("string");
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) != null) {
                    Element element = (Element) nodeList.item(i);
                    stringHashMap.put(element.getAttribute("name"), element.getTextContent());
                }
            }
        } catch (Exception e) {
            Log.e("Error loading strings file");
        }
    }
}