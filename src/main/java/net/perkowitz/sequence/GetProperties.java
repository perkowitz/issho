package net.perkowitz.sequence;

/**
 * Created by optic on 7/8/16.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetProperties {

    String result = "";
    InputStream inputStream;

    public Properties getProperties() throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "sequence.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            return prop;

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return null;
    }

}
