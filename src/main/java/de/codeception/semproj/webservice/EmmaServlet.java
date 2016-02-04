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
package de.codeception.semproj.webservice;

import de.codeception.semproj.emma.Emma;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmmaServlet extends HttpServlet {

    private static final String HTTP_INPUT_PARAM = "input";
    private static final String HTTP_SESSION_ID = "sessionID";

    private static final HashMap<String, Emma> EMMAS = new HashMap<>(1);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* get session or create one */
        String sessionID = request.getParameter(HTTP_SESSION_ID);
        if (sessionID == null) {
            return;
        }

        sessionID = sessionID.trim();
        if (sessionID.isEmpty() || !EMMAS.containsKey(sessionID)) {
            sessionID = UUID.randomUUID().toString();
            EMMAS.put(sessionID, new Emma());
            respond(response, sessionID);
            return;
        }

        /* get emma instance by session id */
        Emma emma = EMMAS.get(sessionID);

        /* get input parameter value */
        String input = request.getParameter(HTTP_INPUT_PARAM);
        if (input == null) {
            return;
        }

        /* get emmas response */
        String emmaResponse = emma.address(input);
        respond(response, emmaResponse);
    }

    /* write response */
    private void respond(HttpServletResponse response, String msg)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println(msg);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
