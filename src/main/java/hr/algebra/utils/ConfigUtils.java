package hr.algebra.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class ConfigUtils {

    private static final String CONFIG_FILE = "config.xml";

    private static String dbUrl;
    private static int windowWidth;
    private static int windowHeight;
    private static String windowTitle;
    private static String adminUsername;
    private static String adminPassword;

    private ConfigUtils() {}

    public static void load() {
        try {
            InputStream is = ConfigUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (is == null) {
                throw new RuntimeException("Config file not found: " + CONFIG_FILE);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            // Database
            dbUrl = getElementValue(doc, "url");

            // Window
            windowWidth = Integer.parseInt(getElementValue(doc, "width"));
            windowHeight = Integer.parseInt(getElementValue(doc, "height"));
            windowTitle = getElementValue(doc, "title");

            // Admin
            adminUsername = getElementValue(doc, "username");
            adminPassword = getElementValue(doc, "password");

            System.out.println("Configuration loaded successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    private static String getElementValue(Document doc, String tagName) {
        return doc.getElementsByTagName(tagName).item(0).getTextContent();
    }

    public static String getDbUrl() { return dbUrl; }
    public static int getWindowWidth() { return windowWidth; }
    public static int getWindowHeight() { return windowHeight; }
    public static String getWindowTitle() { return windowTitle; }
    public static String getAdminUsername() { return adminUsername; }
    public static String getAdminPassword() { return adminPassword; }
}