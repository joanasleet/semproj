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
import static de.codeception.semproj.knowledge.Util.matchNot;

public class EmmaBrain {

    private enum State {

        /* context info */
        INIT,
        GREET,
        USER_NAME,
        USER_HAS_CITY,
        USER_NO_TRAVEL,
        END,
        /* preconditions */
        ASK_IF_TRAVEL,
        ASK_IF_TRAVEL_WAIT_FOR_ANSWER,
        ASK_IF_TRAVEL_STAY_ON_TOPIC,
        USER_WANTS_TRAVEL,
        /* destination criteria */
        CHOOSE_CITY,
        CHOOSE_SIZE,
        CHOOSE_TEMPR,
        CHOOSE_SEASON,
        CHOOSE_ORIGIN,
        CHOOSE_CONTINENT,
    };

    /* Emmas brain state */
    private State state;

    /* users name */
    private String username;

    /* destination */
    private String city;

    /* destination criteria */
    private String size;
    private String tempr;
    private String season;
    private String continent;

    public EmmaBrain() {
        state = State.INIT;
    }

    public String process(String input) {

        System.out.println("State: " + state + " Input: " + input);

        switch (state) {

            /* greet, what does the bot do */
            case INIT:
                state = State.ASK_IF_TRAVEL;
                return "Hi, I'm Emma. I can help you find a travel destination.";

            /* TODO: maybe */
            case USER_NAME:
                username = matchNot(input, "(Hi|,|I'm|I|am|my|name|is)");
                state = State.ASK_IF_TRAVEL;
                return process(null);

            /* disable user input in ui */
            case ASK_IF_TRAVEL:
                state = State.ASK_IF_TRAVEL_WAIT_FOR_ANSWER;
                if (username != null) {
                    return username + ", do you wanna go on a journey in the near future ?";
                }
                return "Do you wanna go on a journey in the near future ?";

            case ASK_IF_TRAVEL_WAIT_FOR_ANSWER: {
                if (has(input, "yes", "yep", "yo", "yeah", "of course", "sure")) {
                    state = State.USER_WANTS_TRAVEL;
                    return rand(
                            "So where are you going ?",
                            "Great! Which city do you want to visit ? ",
                            "Yay! I love to travel. What is your destination ?",
                            "That's awesome. Tell me the name of the city you have chosen.");
                }
                if (has(input, "don't know", "dunno", "do not know", "maybe",
                        "have not decide", "not sure", "dont know")) {
                    state = State.USER_WANTS_TRAVEL; // ?? user doesnt know IF he wants to travel
                    return rand(
                            "I can help you to choose a city and make a decision then. "
                            + "Which season is the best to travel: summer, autumn, winter or spring ? Please choose only one,",
                            "No Problem. I will ask you some question to help you with your decision."
                            + " Would you like to travel in summer, autumn, winter or spring ? You need to decide for one.",
                            "Ok ok .... I love to travel in summer because the weather is always nice."
                            + "How about you ? Summer, autumn, winter or spring ? ",
                            "Well, it's good that you texted me then. I will find a city for you."
                            + " Please just answer a few questions: Would you like to travel in summer, autumn, winter or summer ?");
                }
                if (has(input, "no", "nope", "not really")) {
                    state = State.END;
                    return rand("Too bad. I won't be help for you then."
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

            case USER_HAS_CITY:
                return "|PH|";

            case USER_WANTS_TRAVEL: {
                city = KnowledgeBase.getCity(input);
                if (city != null) {
                    state = State.USER_HAS_CITY;
                    return "Do you have any questions about " + city + " ?";
                }
                if (has(input, "don't know", "dunno", "do not know", "have not decide", "dont know")) {
                    state = State.CHOOSE_SEASON;
                    return rand("Well, then it is good I am here. I will help you to find one."
                            + " Do you want to travel in summer, autumn, winter or spring ? Please decide for only one. ",
                            "Ok. In which season do you want to travel ? Summer, autumn, winter or spring ?",
                            "I know, I know. There are too many beautiful cities ... I will find the perfect one for you. "
                            + "So tell me: Should your journey take place in summer, autumn, winter or spring ?",
                            "Yay, so you need my help here :D What is your fav season to travel ? Summer, winter, spring or autumn ?");
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

            case CHOOSE_SEASON:
                season = KnowledgeBase.getSeason(input);
                if (season != null) {
                    state = State.CHOOSE_TEMPR;
                    return rand("Should it be hot in " + season + " there ?",
                            "Good choice. Do you want a city where the temperatures are over 20 degrees in " + season + " ?",
                            "Ok ... next question: Should I search for a city where it's hot in " + season + " ?",
                            "Yay! " + season + " is my fav season ... now I need to know if you want hot weather during this season ?");
                }
                return "I am pretty sure that this is not the answer to my question or maybe you missspelled"
                        + " something  ... Please answer it again: summer, autumn, spring or winter ?";

            case CHOOSE_TEMPR:
                tempr = KnowledgeBase.getTemperature(input);
                if (tempr != null) {
                    state = State.CHOOSE_SIZE;
                    return rand("Ok, and should it be a big city ?",
                            "Great! Do you wanna go to a big city with alot of people living there ?",
                            "Personally, I prefer cold weather cause I am sweating alot when it's hot"
                            + " ... Nevermind .. is a big city good for you ?",
                            "And how about the size of the city .. should it be a big crowded city ?"
                    );
                }
                return "Interesting, but please answer my question ... "
                        + "I need to know the answer.. Should it be hot in  " + season + " ?";

            case CHOOSE_SIZE:
                size = KnowledgeBase.getCitySize(input);
                if (size != null) {
                    state = State.CHOOSE_CONTINENT;
                    return rand("Last question: Which continent should it be ? Europe, America, Africa, Asia or Australia ?",
                            "So do you wanna stay in a city located in Europe, America, Africa, Asia or Australia ?",
                            "Ok ... Now please choose a continent: Europe,America, Africa, Asia or Australia ?");
                } else {
                    // TODO: dont care
                }
                return "Huh, what ? Sorry but I need to find a city for you first "
                        + "... so answer my question please: do you wanna go to a big city ? ";

            case CHOOSE_CONTINENT:
                continent = KnowledgeBase.getContinent(input);
                if (continent != null) {

                } else {
                    // TODO: dont care
                }
                return "Hmmmmm .... I am not sure if that is the answer to my question ... my question was: "
                        + "Do you wanna stay in Europe, America, Asia, Australia or Africa ?";

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
