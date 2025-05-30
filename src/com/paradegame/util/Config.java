package com.paradegame.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The Config class loads configuration settings from a properties file and provides utility methods
 * to retrieve values for different types (String, int, boolean).
 */
public class Config {
    // Stores the configuration properties loaded from the file
    private static final Properties props = new Properties();

    static {
        try {
            // Load configuration settings from properties file
            props.load(new FileInputStream("resources/config.properties"));
        } catch (IOException e) {
            System.out.println("⚠️ Warning: Could not load config.properties, using defaults.");
        }
    }

    /**
     * Gets the property value associated with the specified key as a String.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if the key is not found
     * @return the value of the property, or the default value if not found
     */
    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * Gets the property value associated with the specified key as an integer.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if the key is not found or if the value is not a valid integer
     * @return the value of the property as an integer, or the default value if not found or invalid
     */
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets the property value associated with the specified key as a boolean.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if the key is not found or if the value is not a valid boolean
     * @return the value of the property as a boolean, or the default value if not found or invalid
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(props.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
