package net.perkowitz.issho.controller.apps.hachi;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import java.io.*;

public class Config {

    private static final String MODULES = "modules";

    private static ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode jsonNode;

    public Config(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public ArrayNode getModules() {
        return (ArrayNode)jsonNode.get(MODULES);
    }


    /***** static methods *****/

    public static Config fromJsonFile(String filename) throws IOException {

        InputStream inputStream = null;
        try {

            File file = new File(filename);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = Config.class.getClassLoader().getResourceAsStream(filename);
            }

            if (inputStream != null) {
                return new Config(objectMapper.readTree(inputStream));
            }
            throw new FileNotFoundException("Config file '" + filename + "' not found in the classpath or path");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return null;
    }

}
