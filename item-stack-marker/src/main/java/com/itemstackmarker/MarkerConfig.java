package com.itemstackmarker;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Simple config, saved to config/itemstackmarker.properties */
public final class MarkerConfig {
    private static MarkerConfig instance = new MarkerConfig();

    public boolean enabled = true;
    /** Items farther than this (in blocks) get no marker. */
    public double maxDistance = 64.0;
    /** Height of the white line in blocks. */
    public float lineHeight = 1.2f;
    /** Thickness of the white line in blocks. */
    public float lineWidth = 0.04f;
    /** Where the line starts relative to the item's feet (negative = below). */
    public float lineStart = -0.1f;
    /** Size of the count text. 1.0 is already big; raise it for even bigger. */
    public float textScale = 1.0f;
    /** Text grows with distance so it stays readable far away. 0 turns it off. */
    public float distanceGrowth = 0.04f;
    /** Draw through walls. */
    public boolean throughWalls = true;
    /** Show "1" for single items too. */
    public boolean showSingles = true;
    /** ARGB hex colors. */
    public int lineColor = 0xFFFFFFFF;
    public int textColor = 0xFFFFFFFF;
    public int textBackground = 0x99000000;
    public int textOutline = 0xFF000000;

    public static MarkerConfig get() {
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("itemstackmarker.properties");
    }

    public static void load() {
        MarkerConfig cfg = new MarkerConfig();
        Path p = path();
        if (Files.exists(p)) {
            Properties props = new Properties();
            try (Reader r = Files.newBufferedReader(p)) {
                props.load(r);
                cfg.enabled = bool(props, "enabled", cfg.enabled);
                cfg.maxDistance = dbl(props, "maxDistance", cfg.maxDistance);
                cfg.lineHeight = (float) dbl(props, "lineHeight", cfg.lineHeight);
                cfg.lineWidth = (float) dbl(props, "lineWidth", cfg.lineWidth);
                cfg.lineStart = (float) dbl(props, "lineStart", cfg.lineStart);
                cfg.textScale = (float) dbl(props, "textScale", cfg.textScale);
                cfg.distanceGrowth = (float) dbl(props, "distanceGrowth", cfg.distanceGrowth);
                cfg.throughWalls = bool(props, "throughWalls", cfg.throughWalls);
                cfg.showSingles = bool(props, "showSingles", cfg.showSingles);
                cfg.lineColor = argb(props, "lineColor", cfg.lineColor);
                cfg.textColor = argb(props, "textColor", cfg.textColor);
                cfg.textBackground = argb(props, "textBackground", cfg.textBackground);
                cfg.textOutline = argb(props, "textOutline", cfg.textOutline);
            } catch (IOException | RuntimeException e) {
                System.err.println("[ItemStackMarker] Could not read config, using defaults: " + e);
            }
        }
        instance = cfg;
        cfg.save();
    }

    private void save() {
        Properties props = new Properties();
        props.setProperty("enabled", Boolean.toString(enabled));
        props.setProperty("maxDistance", Double.toString(maxDistance));
        props.setProperty("lineHeight", Float.toString(lineHeight));
        props.setProperty("lineWidth", Float.toString(lineWidth));
        props.setProperty("lineStart", Float.toString(lineStart));
        props.setProperty("textScale", Float.toString(textScale));
        props.setProperty("distanceGrowth", Float.toString(distanceGrowth));
        props.setProperty("throughWalls", Boolean.toString(throughWalls));
        props.setProperty("showSingles", Boolean.toString(showSingles));
        props.setProperty("lineColor", hex(lineColor));
        props.setProperty("textColor", hex(textColor));
        props.setProperty("textBackground", hex(textBackground));
        props.setProperty("textOutline", hex(textOutline));
        try (Writer w = Files.newBufferedWriter(path())) {
            props.store(w, "Item Stack Marker - colors are AARRGGBB hex");
        } catch (IOException e) {
            System.err.println("[ItemStackMarker] Could not save config: " + e);
        }
    }

    private static boolean bool(Properties p, String k, boolean d) {
        String v = p.getProperty(k);
        return v == null ? d : Boolean.parseBoolean(v.trim());
    }

    private static double dbl(Properties p, String k, double d) {
        String v = p.getProperty(k);
        return v == null ? d : Double.parseDouble(v.trim());
    }

    private static int argb(Properties p, String k, int d) {
        String v = p.getProperty(k);
        return v == null ? d : (int) Long.parseLong(v.trim().replace("#", ""), 16);
    }

    private static String hex(int c) {
        return String.format("%08X", c);
    }
}
