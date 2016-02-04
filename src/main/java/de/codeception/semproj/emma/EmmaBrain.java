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
import static de.codeception.semproj.knowledge.Util.has;
import static de.codeception.semproj.knowledge.Util.rand;

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
        CHOOSE_CONTINENT,
        CHOOSE_SIZE,
        CHOOSE_AIRPORT,
        CHOOSE_SEASON,
        CHOOSE_TEMPR,
        CHOOSE_RAIN,
        CHOOSE_SUN,
        CHOOSE_CITY,
        /* city questionaire */
        QUESTION_CITY
    };

    /* Emmas brain state */
    private State state;

    /* destination */
    private String city;

    /* destination criteria */
    private String continent;
    private String size;
    private boolean airport;
    private String season;
    private String tempr;
    private boolean rainy;
    private boolean sunny;

    /* wiki article of city */
    private String cityWiki;

    /* emmas response when data missing */
    private static final String DONT_KNOW = "Hm ... I don't know anything about that. Sorry";
    private static final String ANOTHER_Q = " ... Do you have another question ?";
    private static final String WHATEVER = "Then you might as well choose ...";

    public EmmaBrain() {
        state = State.INIT;
    }

    public String process(String input) {

        System.out.println("State: " + state + " Input: " + input);

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
                    state = State.USER_WANTS_TRAVEL;
                    return rand("I can help you to choose a city and make a decision then. "
                            + "Which season is the best to travel ?",
                            "No Problem. I will ask you some question to help you with your decision."
                            + " Would you like to travel in summer, autumn, winter or spring ?",
                            "Ok ok .... I love to travel in summer because the weather is always nice."
                            + "Which season is your fav ?",
                            "Well, it's good that you texted me then. I will find a city for you."
                            + " Please just answer a few questions: Would you like to travel in summer, autumn, winter or summer ?");
                }
                if (has(input, "no", "nope", "not really")) {
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
                    state = State.QUESTION_CITY;
                    return "Do you have any questions about " + city.replaceAll("_", " ") + " ?";
                }
                if (has(input, "don't know", "dunno", "do not know", "have not decide", "dont know")) {
                    state = State.CHOOSE_SEASON;
                    return rand("Well, then it is good I am here. I will help you to find one."
                            + " Do you want to travel in summer, autumn, winter or spring ?",
                            "Ok. In which season do you want to travel ?",
                            "I know, I know. There are too many beautiful cities ... I will find the perfect one for you. "
                            + "So tell me: Should your journey take place in summer, autumn, winter or spring ?",
                            "Yay, so you need my help here :D What is your fav season to travel ?");
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
                /* cache wiki travel article on city */
                cityWiki = KnowledgeBase.getWikiOn(city);
                if (has(input, "eat", "food", "dish", "dishes", "cuisine")) {
                    String resp = KnowledgeBase.getWikiSection(cityWiki, "Eat");
                    resp = (resp != null) ? resp : DONT_KNOW;
                    return resp + ANOTHER_Q;
                }
                if (has(input, "history", "story")) {
                    String resp = KnowledgeBase.getWikiSection(cityWiki, "History");
                    resp = (resp != null) ? resp : DONT_KNOW;
                    return resp + ANOTHER_Q;
                }
                if (has(input, "climate", "weather")) {
                    String resp = KnowledgeBase.getWikiSection(cityWiki, "Climate");
                    resp = (resp != null) ? resp : DONT_KNOW;
                    return resp + ANOTHER_Q;
                }
                if (has(input, "general", "generally")) {
                    String resp = KnowledgeBase.getWikiSection(cityWiki, null);
                    resp = (resp != null) ? resp : DONT_KNOW;
                    return resp + ANOTHER_Q;
                }
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    return "Ask away!";
                }
                if (has(input, "done", "bye", "cya", "thanks", "thank you", "no", "nope", "not really")) {
                    state = State.END;
                    if (city != null) {
                        return "Have fun in " + city + ". Bye.";
                    }
                    return "Come back anytime!";
                } else {
                    return DONT_KNOW + ANOTHER_Q;
                }
            }

            /*
             * ask for continent
             */
            case CHOOSE_CONTINENT: {
                continent = KnowledgeBase.getContinent(input);
                if (continent != null) {
                    state = State.CHOOSE_RAIN;
                    return rand("I have been to " + continent + " several times. It's such a beautiful continent!"
                            + "Anyway.. Do you wanna go to a city where it rains alot ?",
                            "Ok ... " + continent + " has alot of beautiful countries."
                            + "I will find the perfect one for you. Should the city have a lot of rainy days in " + season + " ?",
                            "Yay! " + continent + " is my fav too. We have so much in common! :D"
                            + "Do you like rainy weather as much as me ?");
                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                }
                return "Hmmmmm .... I am not sure if that is the answer to my question ... my question was: "
                        + "Do you wanna stay in Europe, America, Asia, Australia or Africa ?";
            }

            /* 
             * ask user for city size
             */
            case CHOOSE_SIZE: {
                size = KnowledgeBase.getCitySize(input);
                if (size != null) {
                    state = State.CHOOSE_CONTINENT;
                    return rand("Next question: Which continent should it be on ?",
                            "So do you wanna stay in a city located in Europe, America, Africa, Asia or Australia ?",
                            "Ok ... Now choose a continent please..");
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
                }
                if (has(input, "no", "nope", "not really")) {
                    airport = true;
                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                }
                return "... I'll ask again: Should there be an airport ?";
            }


            /*
             * ask user for season 
             */
            case CHOOSE_SEASON: {
                season = KnowledgeBase.getSeason(input);
                if (season != null) {
                    state = State.CHOOSE_TEMPR;
                    return rand("Should the weather be warm, mild or cold while you are staying there ?",
                            "Good choice. Do you want a city where the temperatures are mild in " + season
                            + " ? Or maybe you prefer warm or even cold weather ?",
                            "Ok ... next question: Should I search for a city where it's warm in " + season
                            + " ? Or do you prefer cold weather ? Or maybe mild ? ... ",
                            "Yay! " + season + " is my fav season ... now I need to know what kind of weather you prefer."
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
                            "And how about the size of the city .. should it be a big, crowded city or a small one ?");
                }
                if (has(input, "do not care", "don't care", "dont care", "doesn't matter", "does not matter", "no matter")) {
                    return WHATEVER;
                }
                return rand("Interesting, but please answer my question ... "
                        + "How should be the temperature in " + season + " ? Warm, mild or cold ?",
                        "I need to know the answer ... Should it be warm, mild or cold in  " + season + " ?");
            }

            /*
             * ask for niederschlag
             */
            case CHOOSE_RAIN: {
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    rainy = true;
                    state = State.CHOOSE_SUN;
                    return rand("I knew it. We are indeed soulmates! I love rainy weather!!"
                            + "Next question: Do you wanna have alot of sunny days as well ?",
                            "I see ... how about sunny days ? Should there be alot of them in " + season + " ?",
                            "Good decision! But you probably want alot of sunny days too, don't you ?");

                }
                if (has(input, "no", "nope", "not really")) {
                    rainy = true;
                    state = State.CHOOSE_SUN;
                    return rand("Oh ok ... I like rainy days."
                            + "So I assume you want to have many days of sunshine while traveling to " + continent + " ?",
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
                    return rand("Great! I am always in a good mood when the sun is shining"
                            + "Final question: Is an airport important to you ?",
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
             * should never happen
             */
            default:
                return "I'm confused, call a doctor.";
        }
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
