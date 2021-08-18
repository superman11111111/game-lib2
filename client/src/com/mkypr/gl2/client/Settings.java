package com.mkypr.gl2.client;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class Settings {

    public static Dimension defaultDim = new Dimension(400, 40);

    private static final HashMap<String, Setting> SETTINGS = new HashMap<>();

    static class Setting {
        String name;
        Object value;
        JComponent component;
        Dimension dimension;
        Callable<Object> valueGetter;
        boolean persistent;

        protected Setting(String name, Object value, JComponent component, Dimension dimension, Callable<Object> valueGetter, boolean persistent) {
            this.name = name;
            this.value = value;
            this.component = component;
            this.dimension = dimension;
            this.valueGetter = valueGetter;
            this.persistent = persistent;
        }

        private static Setting create(String name, Object value, JComponent component, Dimension dimension, Callable<Object> valueGetter, boolean persistent) {
            if (dimension == null) dimension = Settings.defaultDim;
            return new Setting(name, value, component, dimension, valueGetter, persistent);
        }
    }

    public static String serialize() {
        StringBuilder sb = new StringBuilder();
        for (String k : SETTINGS.keySet()) {
            Setting s = SETTINGS.get(k);
            if (!s.persistent) continue;
            StringBuilder sb2 = new StringBuilder();
            if (s.value instanceof Dimension) {
                sb2.append(Persistence.SerializationHelper.serializeDim((Dimension) s.value));
            } else if (s.value instanceof Color) {
                sb2.append(Persistence.SerializationHelper.serializeColor((Color) s.value));
            } else if (s.value instanceof Settings.Keybind[]) {
                for (Keybind kk : (Keybind[]) s.value) {
                    sb2.append(Persistence.SerializationHelper.serializeKeybind(kk));
                    sb2.append("/");
                }
            } else {
                sb2.append(s.value);
            }
            sb.append(s.name);
            sb.append(":");
            sb.append(sb2);
            sb.append(";\n");
        }
        return sb.toString();
    }

    public static void deserialize(String s) {
        String[] sp = s.split(";\n");
        if (sp.length == 0) return;
        for (String r : sp) {
            if (r.isBlank()) continue;
            String[] spp = r.split(":");
            Setting setting = SETTINGS.get(spp[0]);
            String v = spp[1];
            String[] arraySplit = v.split("/");
            String[] sp2 = v.split("=");
            if (sp2.length < 2) {
                setting.value = Integer.parseInt(sp2[0]);
                continue;
            }
            String className = sp2[0];
            String value = sp2[1];
            if (className.equalsIgnoreCase(Dimension.class.getName())) {
                setting.value = Persistence.SerializationHelper.deserializeDim(value);
            } else if (className.equalsIgnoreCase(Color.class.getName())) {
                setting.value = Persistence.SerializationHelper.deserializeColor(value);
            } else if (className.equalsIgnoreCase(Keybind.class.getName())) {
                if (arraySplit.length > 1) {
                    Keybind[] keybinds = new Keybind[arraySplit.length];
                    for (int i = 0; i < arraySplit.length; i++) {
                        keybinds[i] = Persistence.SerializationHelper.deserializeKeybind(arraySplit[i].split("=")[1]);
                    }
                    setting.value = keybinds;
                }
            }
        }
    }


    public static void addSetting(String key, Callable<Object> valueGetter, JComponent component, Dimension dim, boolean persistent) {
        try {
            SETTINGS.put(key, Setting.create(key, valueGetter.call(), component, dim, valueGetter, persistent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addSetting(String key, Object value, JComponent component, boolean persistent) {
        SETTINGS.put(key, Setting.create(key, value, component, null, null, persistent));
    }

    public static void addSetting(String key, Object value, boolean persistent) {
        SETTINGS.put(key, Setting.create(key, value, null, null, null, persistent));
    }

    public static Object getValue(String key) {
        return SETTINGS.get(key).value;
    }

    public static JComponent getComponent(String key) {
        return SETTINGS.get(key).component;
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

    public static void setValueGetter(String key, Callable<Object> valueGetter) { SETTINGS.get(key).valueGetter = valueGetter; }

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
        final String action;
        int key;

        public Keybind(String action, int key) {
            this.action = action;
            this.key = key;
        }

        public Callable<Boolean> getCallable() {
            return new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return (Boolean) Client.class.getDeclaredMethod(action).invoke(null);
                }
            };
        }
    }

}
