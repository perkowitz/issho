package net.perkowitz.issho.util;

/**
 * Created by optic on 7/8/16.
 */

import net.perkowitz.sequence.RunSequencer;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties getProperties(String filename) throws IOException {

        InputStream inputStream = null;
        try {
            Properties properties = new Properties();

            File file = new File(filename);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = RunSequencer.class.getClassLoader().getResourceAsStream(filename);
            }

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + filename + "' not found in the classpath or path");
            }

            return properties;

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
