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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class Emma {

    private File chatLog = null;
    private final EmmaBrain brain;

    public Emma() {
        brain = new EmmaBrain();

        try {
            chatLog = File.createTempFile("emma", ".txt", new File("chats"));
        } catch (IOException ex) {
            System.out.println("Failed to create chat log" + ex);
        }
    }

    public String address(String query) {

        String emmaResponse = brain.process(query.trim());

        if (chatLog != null) {

            List<String> conv = Arrays.asList(query, emmaResponse);
            try {
                Files.write(chatLog.toPath(), conv, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException ex) {
                System.out.println("Failed to write chat log" + ex);
            }
        }
        return emmaResponse;
    }
}
