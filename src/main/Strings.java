package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Strings {
    public static String NONE = "";
    public static String PROJECT_VERSION = "";
    public static String OPENGL_VERSION = "";
    public static String RESUME_GAME = "";
    public static String ENABLE_FULL_SCREEN = "";
    public static String DISABLE_FULL_SCREEN = "";
    public static String ENABLE_CREATIVE_MODE = "";
    public static String DISABLE_CREATIVE_MODE = "";
    public static String ENABLE_ENEMIES_SPAWN = "";
    public static String DISABLE_ENEMIES_SPAWN = "";
    public static String ENABLE_SHADERS = "";
    public static String DISABLE_SHADERS = "";
    public static String EXIT_GAME = "";
    public static String LANGUAGE = "";
    public static String RESOLUTION = "";
    public static String EFFECT_SOUND_LEVEL = "";
    public static String MUSIC_SOUND_LEVEL = "";
    public static String AMBIENCE_SOUND_LEVEL = "";
    public static String UPDATE_DISTANCE = "";
    public static String RENDER_DISTANCE = "";
    public static String FRAMES_PER_SECOND = "";
    public static String SPAWN_RATE = "";
    public static String GAME_TIME_SPEED = "";

    public static String getString(String s, String... args) {
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

            NodeList nodeList = doc.getElementsByTagName("string");
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) != null) {
                    Element element = (Element) nodeList.item(i);
                    if (element.getAttribute("name").equals("project_version")) {
                        PROJECT_VERSION = element.getTextContent();
                    } else if (element.getAttribute("name").equals("opengl_version")) {
                        OPENGL_VERSION = element.getTextContent();
                    } else if (element.getAttribute("name").equals("resume_game")) {
                        RESUME_GAME = element.getTextContent();
                    } else if (element.getAttribute("name").equals("enable_full_screen")) {
                        ENABLE_FULL_SCREEN = element.getTextContent();
                    } else if (element.getAttribute("name").equals("disable_full_screen")) {
                        DISABLE_FULL_SCREEN = element.getTextContent();
                    } else if (element.getAttribute("name").equals("enable_creative_mode")) {
                        ENABLE_CREATIVE_MODE = element.getTextContent();
                    } else if (element.getAttribute("name").equals("disable_creative_mode")) {
                        DISABLE_CREATIVE_MODE = element.getTextContent();
                    } else if (element.getAttribute("name").equals("enable_enemies_spawn")) {
                        ENABLE_ENEMIES_SPAWN = element.getTextContent();
                    } else if (element.getAttribute("name").equals("disable_enemies_spawn")) {
                        DISABLE_ENEMIES_SPAWN = element.getTextContent();
                    } else if (element.getAttribute("name").equals("enable_shaders")) {
                        ENABLE_SHADERS = element.getTextContent();
                    } else if (element.getAttribute("name").equals("disable_shaders")) {
                        DISABLE_SHADERS = element.getTextContent();
                    } else if (element.getAttribute("name").equals("exit_game")) {
                        EXIT_GAME = element.getTextContent();
                    } else if (element.getAttribute("name").equals("language")) {
                        LANGUAGE = element.getTextContent();
                    } else if (element.getAttribute("name").equals("resolution")) {
                        RESOLUTION = element.getTextContent();
                    } else if (element.getAttribute("name").equals("effect_sound_level")) {
                        EFFECT_SOUND_LEVEL = element.getTextContent();
                    } else if (element.getAttribute("name").equals("music_sound_level")) {
                        MUSIC_SOUND_LEVEL = element.getTextContent();
                    } else if (element.getAttribute("name").equals("ambience_sound_level")) {
                        AMBIENCE_SOUND_LEVEL = element.getTextContent();
                    } else if (element.getAttribute("name").equals("update_distance")) {
                        UPDATE_DISTANCE = element.getTextContent();
                    } else if (element.getAttribute("name").equals("render_distance")) {
                        RENDER_DISTANCE = element.getTextContent();
                    } else if (element.getAttribute("name").equals("frames_per_second")) {
                        FRAMES_PER_SECOND = element.getTextContent();
                    } else if (element.getAttribute("name").equals("spawn_rate")) {
                        SPAWN_RATE = element.getTextContent();
                    } else if (element.getAttribute("name").equals("game_time_speed")) {
                        GAME_TIME_SPEED = element.getTextContent();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error loading strings file");
        }
    }
}