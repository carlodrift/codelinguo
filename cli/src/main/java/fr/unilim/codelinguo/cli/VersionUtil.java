package fr.unilim.codelinguo.cli;

import java.io.IOException;
import java.util.Properties;

public class VersionUtil {

    public static String getVersion() {
        Properties properties = new Properties();
        try {
            properties.load(Main.class.getClassLoader().getResourceAsStream("version.properties"));
            return properties.getProperty("version");
        } catch (IOException e) {
            return "unknown version";
        }
    }
}
