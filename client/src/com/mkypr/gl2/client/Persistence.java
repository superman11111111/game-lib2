package com.mkypr.gl2.client;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Persistence {
    public final static String DIRNAME = "GameLib2";

    private static final String OS = (System.getProperty("os.name")).toUpperCase();

    static class SerializationHelper {
        public static String serializeDim(Dimension dim) {
            return String.format("%s=%d,%d", dim.getClass().getName(), dim.width, dim.height);
        }
        public static String serializeColor(Color c) {
            return String.format("%s=%d,%d,%d", c.getClass().getName(), c.getRed(), c.getGreen(), c.getBlue());
        }
        public static String serializeKeybind(Settings.Keybind k) {
            return String.format("%s=%s,%d", k.getClass().getName(), k.action, k.key);
        }
        public static Dimension deserializeDim(String s) {
            String[] sp = s.split(",");
            return new Dimension(Integer.parseInt(sp[0]), Integer.parseInt(sp[1]));
        }
        public static Color deserializeColor(String s) {
            String[] sp = s.split(",");
            return new Color(Integer.parseInt(sp[0]), Integer.parseInt(sp[1]), Integer.parseInt(sp[2]));
        }
        public static Settings.Keybind deserializeKeybind(String s) {
            String[] sp = s.split(",");
            return new Settings.Keybind(sp[0], Integer.parseInt(sp[1]));
        }
    }

    public static Path getWorkingDirectory() {
        String workingDirectory;
        if (OS.contains("WIN")) {
            workingDirectory = System.getenv("AppData");
        } else {
            workingDirectory = System.getProperty("user.home");
            workingDirectory += "/Library/Application Support";
        }
        return Paths.get(workingDirectory, DIRNAME);
    }

    public static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static FileInputStream open(String fileName) {
        Path p = Persistence.getWorkingDirectory();
        System.out.println(p.toUri());
        if (new File(p.toUri()).mkdirs()) {
            System.out.printf("created %s%n", p.toUri());
        }
        Path ph = Paths.get(p.toString(), fileName);
        File f = new File(ph.toUri());
        if (!f.exists() && !f.isDirectory()) {
            try {
                if (f.createNewFile()) {
                    System.out.printf("created %s%n", ph.toUri());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return new FileInputStream(ph.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean write(String fileName, String buffer) {
        Path p = Persistence.getWorkingDirectory();
        try {
            FileWriter fw = new FileWriter(Paths.get(p.toString(), fileName).toString());
            fw.write(buffer);
            fw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static FileInputStream[] open(String... files) {
        Path p = Persistence.getWorkingDirectory();
        if (new File(p.toUri()).mkdirs()) {
            System.out.printf("created %s%n", p.toUri());
        }
        FileInputStream[] fis = new FileInputStream[files.length];
        for (int i = 0; i < files.length; i++) {
            Path ph = Paths.get(p.toString(), files[i]);
            File f = new File(ph.toUri());
            if (!f.exists() && !f.isDirectory()) {
                try {
                    if (f.createNewFile()) {
                        System.out.printf("created %s%n", ph.toUri());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                fis[i] = new FileInputStream(ph.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fis;
    }

    public static HashMap<String, FileInputStream> open2(String... files) {
        Path p = Persistence.getWorkingDirectory();
        if (new File(p.toUri()).mkdirs()) {
            System.out.printf("created %s%n", p.toUri());
        }
        HashMap<String, FileInputStream> m = new HashMap<>();
        for (String fn : files) {
            Path ph = Paths.get(p.toString(), fn);
            File f = new File(ph.toUri());
            if (!f.exists() && !f.isDirectory()) {
                try {
                    if (f.createNewFile()) {
                        System.out.printf("created %s%n", ph.toUri());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                m.put(fn, new FileInputStream(ph.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return m;
    }

}
