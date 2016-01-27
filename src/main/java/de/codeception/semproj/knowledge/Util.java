package de.codeception.semproj.knowledge;

import java.util.Scanner;
import java.util.regex.MatchResult;

public class Util {

    public static String rand(String... strings) {
        int random = (int) (Math.random() * strings.length);
        return strings[random];
    }

    public static boolean has(String target, String... oneOfThese) {
        for (String s : oneOfThese) {
            if (target.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static String match(String s, String regex) {

        String m;
        try (Scanner scan = new Scanner(s)) {
            m = scan.findInLine(regex);
        }
        return m;
    }

    public static String matchNot(String s, String regex) {

        try (Scanner scan = new Scanner(s)) {
            while (scan.hasNext()) {
                s = s.replaceAll(scan.findInLine(regex), "");
            }
            return s.trim();
        }
    }
}
