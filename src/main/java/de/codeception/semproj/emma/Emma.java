package de.codeception.semproj.emma;

public class Emma {

    private final EmmaBrain brain;

    public Emma() {
        brain = new EmmaBrain();
    }

    public String address(String query) {
        return brain.process(query.toLowerCase());
    }

}
