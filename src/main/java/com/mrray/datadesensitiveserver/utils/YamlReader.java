package com.mrray.datadesensitiveserver.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public final class YamlReader {
    private YamlReader() {
    }

    public static String getKey(String type) {
        File dumpFile = new File(System.getProperty("user.dir") + "/src/main/resources/application.yaml");
        Yaml yaml = new Yaml();
        Map<String, Object> result;
        try {
            result = (Map<String, Object>) yaml.load(new FileInputStream(dumpFile));
            Map<String, Object> keys = (Map<String, Object>) result.get("keys");
            return keys.get(type).toString();
        } catch (Exception e) {
            return "";
        }
    }
}
