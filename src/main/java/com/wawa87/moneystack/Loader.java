package com.wawa87.moneystack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Loader {
    private static final Logger LOGGER = LoggerFactory.getLogger(Loader.class);
    private List<String> propertyDirectories;

    public Loader() {
        try {
            String userDir = System.getProperty("user.dir") + "/";
            String jarDir = Paths.get(Class.forName(Thread.currentThread().getStackTrace()[1].getClassName()).getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString() + "/";
            String resDir = System.getProperty("user.dir") + "/src/main/resources/";

            propertyDirectories = new ArrayList<>();
            propertyDirectories.add(userDir);
            propertyDirectories.add(jarDir);
            propertyDirectories.add(resDir);
        } catch (URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties loadPropertiesFile(String fileName) {
        for (String propDir: this.propertyDirectories) {
            try {
                FileInputStream fileInputStream = new FileInputStream(propDir + fileName);
                Properties properties = new Properties();
                properties.load(fileInputStream);
                return properties;
            } catch (IOException e) {
                LOGGER.error(e.toString());
            }
        }

        try {
            InputStream resInput = Class.forName(Thread.currentThread().getStackTrace()[1].getClassName()).getResourceAsStream("/" + fileName);
            Properties properties = new Properties();
            properties.load(resInput);
            return properties;
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.error(e.toString());
        }
        return null;
    }
}