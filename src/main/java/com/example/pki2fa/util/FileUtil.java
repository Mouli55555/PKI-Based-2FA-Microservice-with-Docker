package com.example.pki2fa.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static String readSeed() throws Exception {
        Path seedPath = Path.of("data/seed.txt");
        return Files.readString(seedPath).trim();
    }
}
