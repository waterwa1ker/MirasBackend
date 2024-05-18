package com.example.tatar.by.util;

import java.util.Random;

public class RandomCodeGenerator {

    private final static int MIN = 100_000;
    private final static int MAX = 999_999;

    public static String getRandomCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(MAX - MIN + 1) + MIN);
    }

}
