package com.mkypr.gl2.client;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class Settings {

    private static final HashMap<String, Setting> SETTINGS = new HashMap<>();

    static class Setting {
        String name;
        Object value;
        JComponent component;
        Dimension dimension;
        Callable<Object> valueGetter;

        protected Setting(String name, Object value, JComponent component, Dimension dimension, Callable<Object> valueGetter) {
            this.name = name;
            this.value = value;
            this.component = component;
            this.dimension = dimension;
            this.valueGetter = valueGetter;
        }

        private static Setting create(String name, Object value, JComponent component, Dimension dimension, Callable<Object> valueGetter) {
            if (dimension == null) dimension = new Dimension(400, 40);
            return new Setting(name, value, component, dimension, valueGetter);
        }
    }

    public static void addSetting(String key, Object value, JComponent component, Dimension dimension, Callable<Object> valueGetter) {
        SETTINGS.put(key, Setting.create(key, value, component, dimension, valueGetter));
    }

    public static void addSetting(String key, Callable<Object> valueGetter, JComponent component) throws Exception {
        SETTINGS.put(key, Setting.create(key, valueGetter.call(), component, null, valueGetter));
    }

    public static void addSetting(String key, Object value) {
        SETTINGS.put(key, Setting.create(key, value, null, null, null));
    }

    public static Object getValue(String key) {
        return SETTINGS.get(key).value;
    }

    public static void setValue(String key, Object value) {
        SETTINGS.get(key).value = value;
    }

    public static void setComponent(String key, JComponent component) {
        SETTINGS.get(key).component = component;
    }

    public static void setDimension(String key, Dimension dimension) {
        SETTINGS.get(key).dimension = dimension;
    }

    public static HashMap<String, Setting> allSettings() {
        return SETTINGS;
    }

    static class Resolution extends Dimension {
        public Resolution(int width, int height) {
            super(width, height);
        }

        @Override
        public String toString() {
            return String.format("%dx%d", width, height);
        }
    }

    static class Keybind {
        final Callable<Boolean> action;
        int key;

        Keybind(Callable<Boolean> action, int key) {
            this.action = action;
            this.key = key;
        }
    }

}
