package com.appmeteo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser JSON scritto a mano, senza librerie esterne — solo java.util.regex.
 * Non è un parser generico: sa leggere solo la struttura delle risposte di Open-Meteo.
 */
public class SimpleJsonParser {

    private final String json;

    public SimpleJsonParser(String json) {
        this.json = json;
    }

    public double getDouble(String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+\\.?\\d*)").matcher(json);
        if (m.find()) return Double.parseDouble(m.group(1));
        throw new RuntimeException("Campo JSON non trovato: " + key);
    }

    public int getInt(String key) {
        return (int) getDouble(key);
    }

    public String getString(String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        if (m.find()) return m.group(1);
        throw new RuntimeException("Campo JSON non trovato: " + key);
    }

    public SimpleJsonParser getObject(String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\\{").matcher(json);
        if (!m.find()) throw new RuntimeException("Oggetto JSON non trovato: " + key);
        return new SimpleJsonParser(extractBraceBlock(m.end() - 1));
    }

    public SimpleJsonParser getFirstObjectInArray(String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[").matcher(json);
        if (!m.find()) throw new RuntimeException("Array JSON non trovato: " + key);
        int objStart = json.indexOf('{', m.end());
        if (objStart == -1) throw new RuntimeException("Array JSON vuoto: " + key);
        return new SimpleJsonParser(extractBraceBlock(objStart));
    }

    public List<String> getStringArray(String key) {
        List<String> result = new ArrayList<>();
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]").matcher(json);
        if (m.find()) {
            Matcher items = Pattern.compile("\"([^\"]*)\"").matcher(m.group(1));
            while (items.find()) result.add(items.group(1));
        }
        return result;
    }

    public List<Double> getDoubleArray(String key) {
        List<Double> result = new ArrayList<>();
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]").matcher(json);
        if (m.find()) {
            for (String part : m.group(1).split(",")) {
                part = part.trim();
                if (part.isEmpty()) continue;
                result.add(part.equals("null") ? 0.0 : Double.parseDouble(part));
            }
        }
        return result;
    }

    public List<Integer> getIntArray(String key) {
        List<Integer> result = new ArrayList<>();
        for (Double d : getDoubleArray(key)) {
            result.add(d.intValue());
        }
        return result;
    }

    private String extractBraceBlock(int openBraceIndex) {
        int depth = 0;
        for (int i = openBraceIndex; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) return json.substring(openBraceIndex, i + 1);
            }
        }
        throw new RuntimeException("JSON malformato: parentesi graffa non chiusa");
    }
}