package net.perkowitz.issho.util;

import net.perkowitz.issho.hachi.modules.mono.MonoMemory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by optic on 11/13/16.
 */
public class SettingsUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();


    public static Map<Object, Object> getSettings(String filename) throws IOException {

        InputStream inputStream = null;
        try {

            HashMap<Object, Object> settings = null;

            File file = new File(filename);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = SettingsUtil.class.getClassLoader().getResourceAsStream(filename);
            }

            if (inputStream != null) {
                settings = objectMapper.readValue(inputStream, HashMap.class);
            } else {
                throw new FileNotFoundException("Settings file '" + filename + "' not found in the classpath or path");
            }

            return settings;

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
