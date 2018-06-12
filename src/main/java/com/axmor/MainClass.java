package com.axmor;

import com.axmor.errorhelper.ErrorHelper;
import com.axmor.models.ISettings;
import com.axmor.models.Settings;
import com.axmor.server.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainClass {
    private static final Logger logger = LoggerFactory.getLogger("MainClass");

    public static void main(String[] args) throws IOException {
        ISettings settings = loadSettings();
        Controller controller = new Controller(settings);
        controller.register();
    }

    private static ISettings loadSettings() throws IOException {
        Properties property = new Properties();
        try (InputStream fis = MainClass.class.getResourceAsStream("/config.properties")) {
            property.load(fis);
            Settings settings = new Settings();
            String host = property.getProperty("db.host");
            String login = property.getProperty("db.login");
            String password = property.getProperty("db.password");

            settings.setDbHost(host);
            settings.setDbLogin(login);
            settings.setDbPassword(password);

            return settings;
        } catch (IOException e) {
            logger.error("Properties file can't be found");
            ErrorHelper.throwPropertiesFileNotFound(e);

            return null;
        }
    }
}
