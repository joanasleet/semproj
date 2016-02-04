/*
 * Copyright 2015 Julia <julia@julia-laptop>, <alex@codeception.de>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package de.codeception.semproj.knowledge;

import java.util.Scanner;

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

    public static String capitalize(String s) {

        StringBuilder capit = new StringBuilder(s.toLowerCase());
        capit.setCharAt(0, s.toUpperCase().charAt(0));
        return capit.toString();
    }

    public static String sanitize(String s) {

        String[] sarr = s.trim().split(" ");
        String san = capitalize(sarr[0]);
        for (int i = 1; i < sarr.length; i++) {
            san += "_" + capitalize(sarr[i]);
        }
        return san;
    }

    public static String cleanWikitext(String wiki) {

        return null;
    }
}
