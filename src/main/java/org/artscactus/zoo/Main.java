package org.artscactus.zoo;

import org.artscactus.zoo.view.UI;

/**
 * Main entry point for the Zoo application.
 * This JavaFX application provides a database management interface
 * for managing zoo animals, employees, and related data.
 */
public class Main {

    /**
     * Launches the Zoo application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
       UI ui = new UI();
        ui.run(args);
    }
}
