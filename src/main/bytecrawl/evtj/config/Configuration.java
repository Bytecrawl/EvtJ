package bytecrawl.evtj.config;

/*
    Configuration.java

    Static class holding the server configurable values.

    Authors:
        Alex Vinyals - <alevinval@gmail.com> - 2014

 */

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Configuration {

    private static final Logger logger =
            LoggerFactory.getLogger(Configuration.class);

    // Path to the configuration file
    private static String configPath = "";

    // HashMap for storing configuration key values
    private static HashMap<String, String> configMap = new HashMap<String, String>();

    // Key definition
    final public static String CFG_WORKER_POOL = "worker-pool";
    final public static String CFG_BUFFER_SIZE = "buffer-size";
    final public static String CFG_SPLIT_SEQUENCE = "split-sequence";

    // Allowed keys array
    private static final String[] allowedOptionsArray = {
            CFG_WORKER_POOL,
            CFG_BUFFER_SIZE,
            CFG_SPLIT_SEQUENCE
    };

    // Allowed keys set
    public static final Set<String> allowedOptions = new HashSet<String>(Arrays.asList(allowedOptionsArray));

    /**
     * New configuration with 'by-default' values
     */
    public static void newConfiguration() {
        configMap = new HashMap<String, String>();
        loadDefaultConfiguration();
    }

    /**
     * New configuration 'by-default' values
     * overrided by configuration specified in config file
     */
    public static void newConfiguration(String path) throws ConfigurationException {
        configPath = path;
        configMap = new HashMap<String, String>();
        loadDefaultConfiguration();
        parseConfigFile();
        loadConfiguration();
    }

    private static void parseConfigFile() throws ConfigurationException {
        try {
            File file = new File(configPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document dom = builder.parse(file);

            NodeList nodeList = dom.getElementsByTagName("option");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String paramName = element.getAttribute("name");
                    String value = element.getTextContent();
                    configMap.put(paramName, value);
                }
            }

        } catch (IOException e) {
            logger.error("Configuration file not found");
            throw new ConfigurationException("Configuration file not found");
        } catch (SAXException e) {
            logger.error("Configuration parsing error");
            throw new ConfigurationException("Configuration parsing error: SAX");
        } catch (ParserConfigurationException e) {
            logger.error("Could not create the Document Builder");
            throw new ConfigurationException("Configuration parsing error: Document Builder");
        }

    }

    /**
     * Loads configuration keys into the configMap,
     * Takes care to remove non allowed option keys.
     */
    private static void loadConfiguration() {
        HashMap<String, String> cleanedConfigMap = (HashMap<String, String>) configMap.clone();
        for (String option : configMap.keySet()) {
            if (!allowedOptions.contains(option)) {
                cleanedConfigMap.remove(option);
            }
        }
        configMap = cleanedConfigMap;
    }

    /**
     * Loads default configuration into the configMap
     */
    private static void loadDefaultConfiguration() {
        configMap.put(CFG_WORKER_POOL, "10");
        configMap.put(CFG_BUFFER_SIZE, "1024");
        configMap.put(CFG_SPLIT_SEQUENCE, "\n");
    }

    public static int getInt(String option) {
        return Integer.parseInt(configMap.get(option));
    }

    public static String get(String option) {
        return configMap.get(option);
    }

}
