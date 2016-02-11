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
package de.codeception.semproj.emma;

import de.codeception.semproj.knowledge.KnowledgeBase;
import de.codeception.semproj.knowledge.Util;
import static de.codeception.semproj.knowledge.Util.has;
import static de.codeception.semproj.knowledge.Util.rand;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

public class EmmaBrain {

    private enum State {

        /* context info */
        INIT,
        GREET,
        USER_HAS_CITY,
        USER_NO_TRAVEL,
        END,
        /* preconditions */
        ASK_IF_TRAVEL,
        ASK_IF_TRAVEL_WAIT_FOR_ANSWER,
        ASK_IF_TRAVEL_STAY_ON_TOPIC,
        USER_WANTS_TRAVEL,
        /* destination criteria */
        CHOOSE_SIZE,
        CHOOSE_AIRPORT,
        CHOOSE_MONTH,
        CHOOSE_TEMPR,
        CHOOSE_RAIN,
        CHOOSE_SUN,
        CHOOSE_CITY,
        /* city questionaire */
        NEXT_CITY,
        NO_CITY_MATCH,
        QUESTION_CITY
    };

    /* Emmas brain state */
    private State state;

    /* destination info */
    private String city;
    private String country;
    private String wikiPage;

    /* city iterator */
    private int cityIndex;
    private JSONArray cityList;

    /* wiki article of city */
    private String cityWikiKeys;
    private HashMap<String, String> cityWiki;

    /* destination criteria */
    private String size;
    private boolean airport;
    private String month;
    private String tempr;
    private boolean rainy;
    private boolean sunny;

    /* emmas response when data missing */
    private static final String DONT_KNOW = "Hm ... I don't know anything about that. Sorry";
    private static final String ANOTHER_Q = " ... <br/>Do you have another question ?";
    private static final String WHATEVER = "Then you might as well choose ...";

    public EmmaBrain() {
        state = State.INIT;
        cityIndex = 0;
    }

    public String process(String input) {

        System.out.println("State: " + state + " Input: " + input);
        input = input.toLowerCase();

        switch (state) {

            /*
             * greet user, explain bots function
             */
            case INIT: {
                state = State.ASK_IF_TRAVEL;
                return "Hi, I'm Emma. I can help you find a travel destination.";
            }

            /* disable user input in ui */
            case ASK_IF_TRAVEL: {
                state = State.ASK_IF_TRAVEL_WAIT_FOR_ANSWER;
                return "Do you wanna go on a journey in the near future ?";
            }

            /*
             * get answer for above 
             */
            case ASK_IF_TRAVEL_WAIT_FOR_ANSWER: {
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    state = State.USER_HAS_CITY;
                    return rand("So where are you going ?",
                            "Great! Which city do you want to visit ? ",
                            "Yay! I love to travel. What is your destination ?",
                            "That's awesome. Tell me the name of the city you have chosen.");
                }
                if (has(input, "don't know", "dunno", "do not know", "maybe",
                        "have not decide", "not sure", "dont know")) {
                    state = State.CHOOSE_MONTH;
                    return rand("I can help you to choose a city and make a decision then. "
                            + "Which month is the best to travel ?",
                            "No Problem. I will ask you some question to help you with your decision."
                            + " Which month would you like to travel in ?",
                            "Ok ok .... I love to travel in summer because the weather is always nice."
                            + " Which month do you prefer to travel in ?",
                            "Well, it's good that you texted me then. I will find a city for you."
                            + " Please just answer a few questions: Which month do you want to travel in ?");
                }
                if (has(input, "dont", "don't", "do not", "no", "nope", "not really")) {
                    state = State.END;
                    return rand("Too bad. I won't be any help for you then."
                            + " But I would be happy if you contact me again if you plan to travel. See ya",
                            "I cannot help you then. I hope we will chat again soon. Byebye",
                            "Oh, but traveling is so much fun! :( byeee",
                            "Ok no prob. Please contact me again if you need my help. Have a nice day my friend..");
                }
                if (has(input, "?")) {
                    return rand(
                            "Good question. But I would like to get back to the topic... soo do you want to travel soon ?",
                            "Hey! I ask you something... Please answer my question first: do you want to go on a journey soon ?",
                            "Pleaseeee, no more questions for now. You need to answer my question first!");
                } else {
                    return rand("Please answer my question ! ",
                            "Hmmm ? I ask you something .... Do you want to on a journey soon ?",
                            "Interesting. But I think that is not an answer to my question... Are you gonna travel soon ?",
                            "..... Ist that an answer to my question ? I think not. Do you want to go on a journey in the near future ?");
                }
            }

            /*
             * user already has a city or needs help finding one
             */
            case USER_HAS_CITY: {
                city = KnowledgeBase.confirmCity(input);
                if (city != null) {
                    wikiPage = KnowledgeBase.getWikiPage(city);
                    if (wikiPage == null) {
                        return city + "seems to be real, but I know nothing about it. Sorry.";
                    }
                    cityWiki = KnowledgeBase.getWikiOn(wikiPage);
                    cityWikiKeys = wikiKeys(cityWiki.keySet().toArray(new String[1]));
                    state = State.QUESTION_CITY;
                    return "Do you have any questions about " + city + " ? "
                            + topics();
                }
                if (has(input, "don't know", "dunno", "do not know", "have not decide", "dont know")) {
                    state = State.CHOOSE_MONTH;
                    return rand("Well, then it is good I am here. I will help you to find one."
                            + " Which month do you want to travel in ?",
                            "Ok. In which month do you want to travel ?",
                            "I know, I know. There are too many beautiful cities ... I will find the perfect one for you. "
                            + "So tell me: Your journey should take place in which month ?",
                            "Yay, so you need my help here :D Which month do you like to travel in ?");
                } else {
                    return rand("Hmmmm..... I am not sure if you answered my question "
                            + "... I asked you to which city you want to travel ..",
                            "I don't understand. Is that a city ? I think not. Please tell me the name of the city ...",
                            "I think that is not a city 'cause I have never heard of it. And I know alot. "
                            + "Please tell me the name of the city you would like to travel to !",
                            "Interesting. Where does the city lie ? In Neverland ? Or maybe in Wonderland ?"
                            + " Please give me an useful answer to my question buddy !");
                }
            }

            /*
             * user has a travel destination, offer info about it 
             */
            case QUESTION_CITY: {
                String key = Util.match(input, cityWikiKeys);
                System.out.println("Matched key: " + key);
                String answ = cityWiki.get(key);
                if (answ != null) {
                    answ = answ.substring(0, Math.min(500, answ.length() - 1));
                    return answ + ANOTHER_Q + topics();
                }
                if (has(input, "map", "location", "located")) {
                    return map() + topics();
                }
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    return "Ask away!" + topics();
                }
                if (has(input, "no", "nope", "not really")) {
                    if (cityList != null) {
                        state = State.NEXT_CITY;
                        return "Do you want to try another city then ?";
                    }
                    state = State.USER_HAS_CITY;
                    return "Give me another city you want to know more about then! ( city name only please )";
                }
                if (has(input, "done", "bye", "cya", "thanks", "thank you")) {
                    state = State.END;
                    if (city != null) {
                        return "Have fun in " + city + ". Bye.";
                    }
                    return "Come back anytime!";
                } else {
                    return DONT_KNOW + ANOTHER_Q + topics();
                }
            }

            case NEXT_CITY: {
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    nextCityMatch();
                    if (city == null) {
                        state = State.NO_CITY_MATCH;
                        return "Sorry. I was unable to find a match... Do you wanna try again ?";
                    }
                    state = State.QUESTION_CITY;
                    return "Looks like " + city + " in " + country + " is another good match! Any questions ?"
                            + topics();
                }
                if (has(input, "no", "nope", "not really")) {
                    state = State.END;
                    return rand("Too bad. I won't be any help for you then."
                            + " But I would be happy if you contact me again if you plan to travel. See ya",
                            "I cannot help you then. I hope we will chat again soon. Byebye",
                            "Oh, but traveling is so much fun! :( byeee",
                            "Ok no prob. Please contact me again if you need my help. Have a nice day my friend..");
                } else {
                    return "... I'll ask again: Do you want me to give you another city ?";
                }
            }

            /*
             * ask for continent
             */
            case CHOOSE_SIZE: {
                size = KnowledgeBase.getCitySize(input);
                if (size != null) {
                    state = State.CHOOSE_RAIN;
                    return rand("Do you wanna go to a city where it rains alot ?",
                            "Should the city have a lot of rainy days in " + month + " ?",
                            "Do you like rainy weather as much as me ?");
                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                }
                return rand("Huh, what ? Sorry but I need to find a city for you first "
                        + "... so answer my question please: do you wanna go to a small, medium or big city ?",
                        "... Please answer my question: do you wanna go to a small, medium or big city ?");
            }

            /*
             * ask for ex. airport
             */
            case CHOOSE_AIRPORT: {
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    airport = true;
                    cityList = KnowledgeBase.getCity(size, month, tempr, airport, rainy, sunny);
                    nextCityMatch();
                    if (city == null) {
                        state = State.NO_CITY_MATCH;
                        return "Sorry. I was unable to find a match... Do you wanna try again ?";
                    }
                    state = State.QUESTION_CITY;
                    return "Looks like " + city + " in " + country + " is a perfect match! Do you have any question about it ?"
                            + topics();
                }
                if (has(input, "no", "nope", "not really")) {
                    airport = false;
                    cityList = KnowledgeBase.getCity(size, month, tempr, airport, rainy, sunny);
                    nextCityMatch();
                    if (city == null) {
                        state = State.NO_CITY_MATCH;
                        return "Sorry. I was unable to find a match... Do you wanna try again ?";
                    }
                    state = State.QUESTION_CITY;
                    return "Looks like " + city + " in " + country + " is a perfect match! Do you have any question about it ?"
                            + topics();
                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                } else {
                    return "... I'll ask again: Should there be an airport ?";
                }
            }

            case NO_CITY_MATCH: {
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    state = State.CHOOSE_MONTH;
                    return rand("Well, then it is good I am here. I will help you to find one."
                            + " Which month do you want to travel in ?",
                            "Ok. In which month do you want to travel ?",
                            "I know, I know. There are too many beautiful cities ... I will find the perfect one for you. "
                            + "So tell me: Your journey should take place in which month ?",
                            "Yay, so you need my help here :D Which month do you like to travel in ?");
                }
                if (has(input, "no", "nope", "not really")) {
                    state = State.END;
                    return rand("Too bad. I won't be any help for you then."
                            + " But I would be happy if you contact me again if you plan to travel. See ya",
                            "I cannot help you then. I hope we will chat again soon. Byebye",
                            "Oh, but traveling is so much fun! :( byeee",
                            "Ok no prob. Please contact me again if you need my help. Have a nice day my friend..");
                }
                state = State.END;
                return "Well then... See you next time!";
            }

            /*
             * ask user for month 
             */
            case CHOOSE_MONTH: {
                month = KnowledgeBase.getMonth(input);
                if (month != null) {
                    state = State.CHOOSE_TEMPR;
                    return rand("Should the weather be warm, mild or cold while you are staying there ?",
                            "Good choice. Do you want a city where the temperatures are mild in " + month
                            + " ? Or maybe you prefer warm or even cold weather ?",
                            "Ok ... next question: Should I search for a city where it's warm in " + month
                            + " ? Or do you prefer cold weather ? Or maybe mild ? ... ",
                            "Yay! " + month + " is my fav month ... now I need to know what kind of weather you prefer."
                            + " Warm, mild or maybe cold weather ?");
                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                }
                return "I am pretty sure that this is not the answer to my question or maybe you misspelled"
                        + " something ... Please answer it again: summer, autumn, spring or winter ?";
            }

            /*
             * ask for temperature
             */
            case CHOOSE_TEMPR: {
                tempr = KnowledgeBase.getTemperature(input);
                if (tempr != null) {
                    state = State.CHOOSE_SIZE;
                    return rand("Ok, and should it be a big, medium or a small city ?",
                            "Personally, I prefer cold weather cause I am sweating alot when it's hot"
                            + " ... Nevermind .. do you prefer big, medium or small cities ?",
                            "And how about the size of the city .. should it be a big, crowded city or a medium or small one ?");
                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                }
                return rand("Interesting, but please answer my question ... "
                        + "How should be the temperature in " + month + " ? Warm, mild or cold ?",
                        "I need to know the answer ... Should it be warm, mild or cold in  " + month + " ?");
            }

            /*
             * ask for sun
             */
            case CHOOSE_RAIN: {
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    rainy = true;
                    state = State.CHOOSE_SUN;
                    return rand("I knew it. We are indeed soulmates! I love rainy weather!!"
                            + " Next question: Do you wanna have alot of sunny days as well ?",
                            "I see ... how about sunny days ? Should there be alot of them in " + month + " ?",
                            "Good decision! But you probably want alot of sunny days too, don't you ?");

                }
                if (has(input, "no", "nope", "not really")) {
                    rainy = true;
                    state = State.CHOOSE_SUN;
                    return rand("Oh ok ... I like rainy days. So I assume you want to have many days of sunshine during your travels ?",
                            "Two question are still yet to come: If you don't like rainy days, you probably love sunny days, right ?",
                            "How about sunny days ? Should there be alot of them ?");

                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                }
                return "Interesting, but obviously not the answer to my very important question:"
                        + "Do you like rainy days ?";
            }

            /*
             * ask for ex. airport
             */
            case CHOOSE_SUN: {
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    sunny = true;
                    state = State.CHOOSE_AIRPORT;
                    return rand("Great! I am always in a good mood when the sun is shining."
                            + " Final question: Is an airport important to you ?",
                            "Perfect. Should the city be reachable by plane ?");
                }
                if (has(input, "no", "nope", "not really")) {
                    sunny = false;
                    state = State.CHOOSE_AIRPORT;
                    return rand("Last question for now: Should there be an airport in or nearby the city ?",
                            "Should the city be served ?");
                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                }
                return "Please answer my question first!"
                        + "Should there be alot of sunny days ?";
            }

            /*
            *  end 
             */
            case END: {
                return "That's pretty much it! See you next time.";
            }

            /*
             * should never happen
             */
            default:
                return "I'm confused, call a doctor.";
        }
    }

    private void nextCityMatch() {

        if (cityList == null || cityIndex > cityList.length() - 1) {
            city = null;
            return;
        }

        JSONObject obj = cityList.getJSONObject(cityIndex);

        city = obj.getJSONObject("city").getString("value");
        country = obj.getJSONObject("country").getString("value");
        wikiPage = obj.getJSONObject("wiki").getString("value");
        String[] split = wikiPage.split("/");
        wikiPage = split[split.length - 1];

        cityWiki = KnowledgeBase.getWikiOn(wikiPage);
        cityWikiKeys = wikiKeys(cityWiki.keySet().toArray(new String[1]));

        cityIndex++;
    }

    private String wikiKeys(String[] keys) {
        String wkeys = "(" + keys[0];
        for (int i = 1; i < keys.length; i++) {
            wkeys += "|" + keys[i];
        }
        wkeys += "|map|location)";
        return wkeys;
    }

    private String map() {

        Locale.setDefault(Locale.ENGLISH);

        double[] loc = KnowledgeBase.getLoc(wikiPage);

        if (loc == null) {
            return "Oops, I don't know where that is...";
        }

        String map = "<iframe width='600' height='250' frameborder='0' style='border:0' "
                + "src='https://www.google.com/maps/embed/v1/view?key=AIzaSyBbB8aOymNe97b6oFSG0-X21NDHVWLPgvg"
                + "&center=%.4f,%.4f&zoom=9' </iframe>";

        return String.format(map, loc[0], loc[1]);
    }

    private String topics() {

        String topics = "<br/><span id='topics'>(I know about these topics: "
                + cityWikiKeys.replaceAll("[|]", ", ").replaceFirst("[(]", "")
                + "</span>";
        return topics;
    }
}


/* TODO:
- remove choices from questions
- add random defaults to criteria if user doesnt care - tell user about it
- change questions to reflect how criteria are chosen
- make new globe image
- input in some states need to be lower cased
- ask questions about city
- the big query to bind them all
 */
