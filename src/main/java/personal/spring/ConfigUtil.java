package personal.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.LoggerFactory;

/**
 * Created by zura on 5/23/14.
 */
public class ConfigUtil {

    private static org.slf4j.Logger LOGGER = LoggerFactory
            .getLogger(ConfigUtil.class);

    public static final Map<?, ?> CONFIG;

    private static final String[] SYSTEM_PROPERTY_NAMES = { "user.dir",
            "catalina.home", "user.home" };

    private static final String CONF_FILE = "personal-finances.properties";

    static {

        String filePath = findConfigFile();

        Properties props = new Properties();

        try {
            props.load(new FileInputStream(new File(filePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CONFIG = props;
    }

    private static String findConfigFile() {

        String filePath = null;
        File f;

        for (String systemProperty : SYSTEM_PROPERTY_NAMES) {

            String systemPropertyPath = System.getProperty(systemProperty);
            String path = systemPropertyPath + "/" + CONF_FILE;
            f = new File(path);

            if (f.exists()) {
                filePath = path;
                break;
            }
        }

        if (filePath == null) {
            filePath = getDefaultConfigFile();
        }

        LOGGER.info("Config file: " + filePath);

        return filePath;
    }

    private static String getDefaultConfigFile() {
        return ConfigUtil.class.getResource("../../" + CONF_FILE).getFile();
    }

    public static String getConfig(String key) {
        return (CONFIG != null && CONFIG.get(key) != null)
                ? CONFIG.get(key).toString() : null;
    }
}
