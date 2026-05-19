package hr.algebra.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtils {

    private static final String LOG_FILE = "logs/app_log.xml";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LogUtils() {}

    public static void log(String username, String action, String details) {
        try {
            File logFile = new File(LOG_FILE);
            logFile.getParentFile().mkdirs();

            Document doc;
            Element root;

            if (logFile.exists()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                doc = builder.parse(logFile);
                root = doc.getDocumentElement();
            } else {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                doc = builder.newDocument();
                root = doc.createElement("logs");
                doc.appendChild(root);
            }

            Element logEntry = doc.createElement("log");

            Element timeElement = doc.createElement("timestamp");
            timeElement.setTextContent(LocalDateTime.now().format(FORMATTER));
            logEntry.appendChild(timeElement);

            Element userElement = doc.createElement("user");
            userElement.setTextContent(username);
            logEntry.appendChild(userElement);

            Element actionElement = doc.createElement("action");
            actionElement.setTextContent(action);
            logEntry.appendChild(actionElement);

            Element detailsElement = doc.createElement("details");
            detailsElement.setTextContent(details);
            logEntry.appendChild(detailsElement);

            root.appendChild(logEntry);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(logFile));

        } catch (Exception e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}