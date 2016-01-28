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

//    public static void main(String[] args) {
//        
//    }
}
