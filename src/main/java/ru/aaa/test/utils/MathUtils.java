package ru.aaa.test.utils;

import java.util.Random;

/**
 * @author adrian_român
 */
public class MathUtils {

    //[min, max)
    public static int getRandomNumber(final int min, final int max) {
        return new  Random().nextInt(max - min) + min;
    }
}
