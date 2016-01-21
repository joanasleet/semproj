package de.codeception.semproj.webservice;

import de.codeception.semproj.emma.Emma;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmmaServlet extends HttpServlet {

    private static final String HTTP_INPUT_PARAM = "input";

    private Emma emma;

    // TODO: Test ob mehrere Instanzen erzeugt werden
    // - sollte aber nicht ?_?
    public EmmaServlet() {
        super();
        emma = new Emma();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
