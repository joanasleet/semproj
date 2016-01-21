/*
 * ScannerExample.java
 * 
 * Copyright 2015 Julia <julia@julia-laptop>
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


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Math;

public class CBrain {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = "";
        boolean decis = false;
        
        System.out.println("Hi there, do you wanna go on a journey in the near future ?");
        
        while (scanner.hasNext()){
            s = scanner.nextLine(); 
            s = s.toLowerCase();
             
             if(s.contains("yes") || s.contains("yep") || s.contains("yo") || s.contains("yeah") || s.contains("of course") || s.contains("sure")){
				 int random = (int) (Math.random()* 4 + 1);
				 switch(random){
					 case 1: System.out.println("Great! Which city do you want to visit ? "); break;
					 case 2: System.out.println("That's awesome. Tell me the name of the city you have chosen."); break;
					 case 3: System.out.println("Yay! I love to travel. What is your destination ?" ); break;
					 case 4: System.out.println("So where are you going ?");break;
				 }
				 decis = true;
				 break;
			}
			
			 if(s.contains("don't know") || s.contains("dunno") || s.contains("do not know") || s.contains("have not decide") || s.contains("maybe") || s.contains("not sure") || s.contains("dont know")){
				 int rand = (int) (Math.random()* 4 + 1);
				 switch(rand){
					 case 1: {System.out.println("I can help you to choose a city and make a decision then. Which season is the best to travel: summer, autumn, winter or spring ? Please choose only one,"); break;}
					 case 2: {System.out.println("No Problem. I will ask you some question to help you with your decision. Would you like to travel in summer, autumn, winter or spring ? You need to decide for one."); break;}
					 case 3: {System.out.println("Ok ok .... I love to travel in summer because the weather is always nice. How about you ? Summer, autumn, winter or spring ? "); break;}
					 case 4: {System.out.println("Well, it's good that you texted me then. I will find a city for you. Please just answer a few questions: Would you like to travel in summer, autumn, winter or summer ?"); break;}
				 }
				 
				CBrainWahl.choose(s);
			 }
			 
			  if(s.contains("no") || s.contains("nope") || s.contains("not really")){
				 int rand = (int) (Math.random()* 4 + 1);
				 switch(rand){
					 case 1: {System.out.println("Too bad. I won't be help for you then. But I would be happy if you contact me again if you plan to travel. See ya"); break;}
					 case 2: {System.out.println("I cannot help you then. I hope we will chat again soon. Byebye"); break;}
					 case 3: {System.out.println("Oh, but traveling is so much fun! :( byeee"); break;}
					 case 4: {System.out.println("Ok no prob. Please contact me again if you need my help. Have a nice day my friend.."); break;}
				 }
				 System.exit(-1);
			 }
			 
			 if(s.contains("?")){
				 int rand = (int) (Math.random()* 3 + 1);
				 switch(rand){
					 case 1: {System.out.println("Good question. But I would like to get back to the topic... soo do you want to travel soon ?"); break;}
					 case 2: {System.out.println("Hey! I ask you something... Please answer my question first: do you want to go on a journey soon ?"); break;}
				     case 3: {System.out.println("Pleaseeee, no more questions for now. You need to answer my question first!"); break;}
				}
			 }
			 
			 else {
				  int rand = (int) (Math.random()* 4 + 1);
				 switch(rand){
					 case 1: {System.out.println("Hmmm ? I ask you something .... Do you want to on a journey soon ?"); break;}
					 case 2: {System.out.println("Interesting. But I think that is not an answer to my question... Are you gonna travel soon ?"); break;}
				     case 3: {System.out.println("Please answer my question ! "); break;}
					 case 4: {System.out.println("..... Ist that an answer to my question ? I think not. Do you want to go on a journey in the near future ?"); break; }	 
				}
			 }
		 }
		 
	  while(scanner.hasNext() && decis){
		  s = scanner.nextLine();
		  
		 // if(s.contains( city -> Textdatei mit Städtenamen und prüfen ob das Geschriebene eine Stadt ist -> falls ja Stadtname speicher; Hast du Fragen ? quest(s, stadt) aufrufen; falls nein -> nochmal fragen
		  
		  if(s.contains("don't know") || s.contains("dunno") || s.contains("do not know") || s.contains("have not decide") || s.contains("dont know")){
			  int rand = (int) (Math.random()* 4 + 1);
				 switch(rand){
					 case 1: {System.out.println("Well, then it is good I am here. I will help you to find one. Do you want to travel in summer, autumn, winter or spring ? Please decide for only one. "); break;}
					 case 2: {System.out.println("Ok. In which season do you want to travel ? Summer, autumn, winter or spring ?"); break;}
				     case 3: {System.out.println("I know, I know. There are too many beautiful cities ... I will find the perfect one for you. So tell me: Should your journey take place in summer, autumn, winter or spring ?"); break;}
				     case 4: {System.out.println("Yay, so you need my help here :D What is your fav season to travel ? Summer, winter, spring or autumn ?"); break; }	 
				}
			  CBrainWahl.choose(s);
		  }
		  
		  else {
			   int rand = (int) (Math.random()* 4 + 1);
				 switch(rand){
					 case 1: {System.out.println("Hmmmm..... I am not sure if you answered my question ... I asked you to which city you want to travel .."); break;}
					 case 2: {System.out.println("I don't understand. Is that a city ? I think not. Please tell me the name of the city ..."); break;}
				     case 3: {System.out.println("I think that is not a city 'cause I have never heard of it. And I know alot. Please tell me the name of the city you would like to travel to !"); break;}
					 case 4: {System.out.println("Interesting. Where does the city lie ? In Neverland ? Or maybe in Wonderland ? Please give me an useful answer to my question buddy !"); break; }	 
				}
		  }
	  }
    
        scanner.close();
    }
}


