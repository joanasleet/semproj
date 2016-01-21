/*
 * CBrainWahl.java
 * 
 * Copyright 2016 Julia <julia@julia-laptop>
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
 * 
 * 
 */
package de.codeception.semproj.emma;

import java.util.Scanner;

public class CBrainWahl {

    private static String continent;
    private static String season;
    private static String popsize;
    private static String temp;
    private static String beach;
    

    public static boolean spell(String wort, String check) {
        int counter = 0;

        if (check.length() >= wort.length()) {
            for (int i = 0; i < wort.length(); i++) {
                if (wort.charAt(i) != check.charAt(i)) {
                    counter++;
                }
                if (counter > (wort.length() / 3)) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < check.length(); i++) {
                if (wort.charAt(i) != check.charAt(i)) {
                    counter++;
                }
                if (counter > (wort.length() / 3)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean checkContinent(String s, String[] array) {
        String[] probe = s.split("\\s");

        for (int i = 0; i < array.length; i++) {
            String wort = array[i];
            for (int j = 0; j < probe.length; j++) {
                String check = probe[j];
                if (spell(wort, check)) {
                    continent = wort;
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean checkSeason(String s, String[] array) {
        String[] probe = s.split("\\s");

        for (int i = 0; i < array.length; i++) {
            String wort = array[i];
            for (int j = 0; j < probe.length; j++) {
                String check = probe[j];
                if (spell(wort, check)) {
                    season = wort;
                    return true;
                }

            }
        }

        return false;
    }

    public static void choose(String s) {
        Scanner scan = new Scanner(System.in);
        int frage = 1;
        String[] seasons = {"summer", "winter", "autumn", "spring"};
        String[] continents = {"europe", "america", "africa", "asia", "australia"};

        while (scan.hasNext()) {
            s = scan.nextLine();

            //Antwort 1.Frage (Jahreszeit) && 2.Frage stellen (Temp)
            if (frage == 1 && checkSeason(s, seasons)) {
                int random = (int) (Math.random() * 4 + 1);
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("Good choice. Do you want a city where the temperatures are over 20 degrees in " + season + " ?");
                        break;
                    }
                    case 2: {
                        System.out.println("Should it be hot in " + season + " there ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Ok ... next question: Should I search for a city where it's hot in " + season + " ?");
                        break;
                    }
                    case 4: {
                        System.out.println("Yay! " + season + " is my fav season ... now I need to know if you want hot weather during this season ?");
                        break;
                    }
                }

                continue;
            }

            //Anwtort 2.Frage (Temp) && 3. Frage stellen (Groesse)
            if (frage == 2 && (s.contains("yes") || s.contains("yep") || s.contains("yo") || s.contains("yeah") || s.contains("of course") || s.contains("sure"))) {
                int random = (int) (Math.random() * 4 + 1);
                if (season == "spring") {
                    temp = "Aug > 20";
                }
                if (season == "winter") {
                    temp = "Dec > 20";
                }
                if (season == "autumn") {
                    temp = "Oct > 20";
                }
                if (season == "spring") {
                    temp = "Apr > 20";
                }
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("Ok, and should it be a big city ?");
                        break;
                    }
                    case 2: {
                        System.out.println("Great! Do you wanna go to a big city with alot of people living there ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Personally, I prefer cold weather cause I am sweating alot when it's hot ... Nevermind .. is a big city good for you ?");
                        break;
                    }
                    case 4: {
                        System.out.println("And how about the size of the city .. should it be a big crowded city ?");
                        break;
                    }

                }

                continue;

            }

            if (frage == 2 && (s.contains("no") || s.contains("nope") || s.contains("not really"))) {
                int random = (int) (Math.random() * 4 + 1);
                if (season == "spring") {
                    temp = "Aug < 20";
                }
                if (season == "winter") {
                    temp = "Dec < 20";
                }
                if (season == "autumn") {
                    temp = "Oct < 20";
                }
                if (season == "spring") {
                    temp = "Apr < 20";
                }
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("Ok, and should it be a big city ?");
                        break;
                    }
                    case 2: {
                        System.out.println("Great! Do you wanna go to a big city with alot of people living there ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Personally, I prefer warm weather cause I am freezing alot when it's too cold ... Nevermind .. is a big city good for you ?");
                        break;
                    }
                    case 4: {
                        System.out.println("And how about the size of the city .. should it be a big crowded city ?");
                        break;
                    }

                }

                continue;

            }

            if (frage == 2 && (s.contains("don't care") || s.contains("dont care") || s.contains("dun't know") || s.contains("dont know") || s.contains("dunno") || s.contains("do not know") || s.contains("not sure") || s.contains("maybe"))) {
                int random = (int) (Math.random() * 4 + 1);
                temp = "";
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("Ok cool, and should it be a big city ?");
                        break;
                    }
                    case 2: {
                        System.out.println("No prob! Do you wanna go to a big city with alot of people living there ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Personally, I like cold weather cause I sweating alot when it's hot ... Nevermind .. is a big city good for you ?");
                        break;
                    }
                    case 4: {
                        System.out.println("And how about the size of the city .. should it be a big crowded city ?");
                        break;
                    }
                }

                continue;

            }

            //Antwort 3.Frage (Groesse) && 4.Frage stellen (Strand)
            if (frage == 3 && (s.contains("yes") || s.contains("yep") || s.contains("yo") || s.contains("yeah") || s.contains("of course") || s.contains("sure"))) {
                int random = (int) (Math.random() * 4 + 1);
                popsize = ">100000";
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("I love big cites too ! :D Next question: Should there be a beach ?");
                        break;
                    }
                    case 2: {
                        System.out.println("Hmmm I always get a little bit nervous if there are too many people... nevermind .... do you like beaches ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Ok, is it important for you that there is a beach nearby the city ?");
                        break;
                    }
                    case 4: {
                        System.out.println("I see. More people, more fun right ? :D ... And how about a beach ? Should there be one ?");
                        break;
                    }

                }

                continue;
            }

            if (frage == 3 && (s.contains("no") || s.contains("nope") || s.contains("not really"))) {
                int random = (int) (Math.random() * 4 + 1);
                popsize = "<10000";
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("Small cities are great :D Next question: Should there be a beach ?");
                        break;
                    }
                    case 2: {
                        System.out.println("Hmmm .... do you like beaches ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Ok, is it important for you that there is a beach nearby the city ?");
                        break;
                    }
                    case 4: {
                        System.out.println("And how about a beach ? Should there be one ?");
                        break;
                    }

                }

                continue;

            }

            if (frage == 3 && (s.contains("don't care") || s.contains("dont care") || s.contains("don't know") || s.contains("dont know") || s.contains("dunno") || s.contains("do not know") || s.contains("not sure") || s.contains("maybe"))) {
                int random = (int) (Math.random() * 4 + 1);
                popsize = "";
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("No problem ..Next question: Should there be a beach ?");
                        break;
                    }
                    case 2: {
                        System.out.println("Hmmm .... do you like beaches ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Ok, is it important for you that there is a beach nearby the city ?");
                        break;
                    }
                    case 4: {
                        System.out.println("And how about a beach ? Should there be one ?");
                        break;
                    }

                }

                continue;

            }

            //Antwort 4.Frage (Strand) && 5.Frage stellen (Kontinent)
            if (frage == 4 && (s.contains("yes") || s.contains("yep") || s.contains("yo") || s.contains("yeah") || s.contains("of course") || s.contains("sure"))) {
                int random = (int) (Math.random() * 4 + 1);
                beach = "beach";
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("Great ! Who doesn't like beaches ? :D so last question: Which continent should it be ? Europe, America, Africa, Asia or Australia ?");
                        break;
                    }
                    case 2: {
                        System.out.println("I love swimming in the sea too. :D So do you wanna stay in a city located in Europe, America, Africa, Asia or Australia ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Ok ... Please choose a continent: Europe,America, Africa, Asia or Australia ?");
                        break;
                    }
                    case 4: {
                        System.out.println("Chilling at the beach and drinking a cocktail ... what could be better ?! :D Anyway, do you wanna stay in Europe, America, Africa, Asia or Australia");
                        break;
                    }

                }

                continue;
            }

            if (frage == 4 && (s.contains("no") || s.contains("nope") || s.contains("not really"))) {
                int random = (int) (Math.random() * 4 + 1);
                beach = "no beach";
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("Beaches are overrated anyway ... ok now my last question is: Europe, America, Africa, Asia or Australia?");
                        break;
                    }
                    case 2: {
                        System.out.println("What ? No beach ? hmmm I see ... I should continue with my last question: Do you wanna stay in Europe, America, Africa, Asia or Australia ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Sooo .... Should the city be located in Europe, America, Africa, Asia or Australia ?");
                        break;
                    }
                    case 4: {
                        System.out.println("Ok, so for the last question you have to choose between those continents: Europe, America, Africa, Asia or Australia?");
                        break;
                    }

                }

                continue;

            }

            if (frage == 4 && (s.contains("don't care") || s.contains("dont care") || s.contains("don't know") || s.contains("dont know") || s.contains("dunno") || s.contains("do not know") || s.contains("not sure") || s.contains("maybe"))) {
                int random = (int) (Math.random() * 4 + 1);
                beach = "";
                frage++;

                switch (random) {
                    case 1: {
                        System.out.println("I wouldn't care either. Now you have to choose one of thoose continent: Europe, America, Africa, Asia or Australia");
                        break;
                    }
                    case 2: {
                        System.out.println("Last question: Europe, America, Africa, Asia or Australia");
                        break;
                    }
                    case 3: {
                        System.out.println("Ok. Do you wanna stay in Europe, America, Africa, Asia or Australia?");
                        break;
                    }
                    case 4: {
                        System.out.println("after this last question, I will find a city for you: Europe, America, Africa, Asia or Australia ?");
                        break;
                    }

                }

                continue;

            }

            //Antwort 5.Frage
            if (frage == 5 && checkContinent(s, continents)) {
                // nun wird eine Stadt gewählt     
                continue;

            } //User antwortet irgendetwas
            else {

                switch (frage) {
                    case 1: {
                        System.out.println("I am pretty sure that this is not the answer to my question or maybe you missspelled something  ... Please answer it again: summer, autumn, spring or winter ?");
                        break;
                    }
                    case 2: {
                        System.out.println("Interesting, but please answer my question ... I need to know the answer.. Should it be hot in  " + season + " ?");
                        break;
                    }
                    case 3: {
                        System.out.println("Huh, what ? Sorry but I need to find a city for you first ... so answer my question please: do you wanna go to a big city ? ");
                        break;
                    }
                    case 4: {
                        System.out.println("How about you answer my question first ? ... should there be a beach ?");
                        break;
                    }
                    case 5: {
                        System.out.println("Hmmmmm .... I am not sure if that is the answer to my question ... my question was: Do you wanna stay in Europe, America, Asia, Australia or Africa ?");
                        break;
                    }
                }
                continue;
            }

        }
    }
}
