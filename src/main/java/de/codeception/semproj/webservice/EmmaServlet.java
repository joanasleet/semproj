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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EmmaServlet extends HttpServlet {

    private static final String HTTP_INPUT_PARAM = "input";
    private static final String EMMA_INSTANCE_PARAM = "emma";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sess = request.getSession();
        Object emmaObj = sess.getAttribute(EMMA_INSTANCE_PARAM);

        /* get emma instance */
        Emma emma;
        if (emmaObj == null) {
            emma = new Emma();
            sess.setAttribute(EMMA_INSTANCE_PARAM, emma);
        } else {
            emma = (Emma) emmaObj;
        }

        /* get input parameter value */
        String input = request.getParameter(HTTP_INPUT_PARAM);
        if (input == null) {
            return;
        }

        /* get emmas response */
        String emmaResponse = emma.address(input);

        /* write response */
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println(emmaResponse);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
