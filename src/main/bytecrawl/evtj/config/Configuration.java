package bytecrawl.evtj.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Configuration {

    final public static String CONFIG_WORKER_POOL = "worker-pool";
    final public static String CONFIG_BUFFER_SIZE = "buffer-size";
    final public static String CONFIG_SPLIT_SEQUENCE = "split-sequence";
    private static final Logger logger =
            LoggerFactory.getLogger(Configuration.class);
    final private static String defaultFileName = "evtj.xml";
    private static final String[] recognizedOptions = {CONFIG_WORKER_POOL, CONFIG_BUFFER_SIZE,
            CONFIG_SPLIT_SEQUENCE};
    private static File file;
    private static String fileName;
    private static HashMap<String, String> optionsMap;

    private static void standardConfiguration() {
        fileName = defaultFileName;
        optionsMap = new HashMap<String, String>();
        loadDefaultConfiguration();
        parseFile();
        loadConfiguration();
    }

    private static void customConfiguration(String fName) {
        fileName = fName;
        optionsMap = new HashMap<String, String>();
        loadDefaultConfiguration();
        parseFile();
        loadConfiguration();
    }

    public static void newConfiguration() {
        standardConfiguration();
    }

    public static void newConfiguration(String configPath) {
        customConfiguration(configPath);
    }

    private static void parseFile() {
        file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document dom = builder.parse(file);

            NodeList nodes = dom.getElementsByTagName("option");
            Node node;
            Element element;
            String paramName, value;

            for (int i = 0; i < nodes.getLength(); i++) {
                node = nodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element) node;
                    paramName = getValue("param", element);
                    value = getValue("value", element);
                    optionsMap.put(paramName, value);
                }
            }

        } catch (SAXException e) {
            logger.error("Error parsing configuration file.");
        } catch (IOException e) {
            logger.error("Configuration file not found.");
        } catch (ParserConfigurationException e) {
            logger.error("Could not create the Document Builder");
        }

    }

    private static void loadConfiguration() {
        for (String option : recognizedOptions) {
            if (!optionsMap.containsKey(option)) {
                optionsMap.remove(option);
            }
        }
    }

    private static void loadDefaultConfiguration() {
        optionsMap.put(CONFIG_WORKER_POOL, "10");
        optionsMap.put(CONFIG_BUFFER_SIZE, "1024");
        optionsMap.put(CONFIG_SPLIT_SEQUENCE, "\n");
    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodes.item(0);
        return node.getNodeValue();
    }

    public static int getInt(String option) {
        return Integer.parseInt(optionsMap.get(option));
    }

    public static String get(String option) {
        return optionsMap.get(option);
    }
}
