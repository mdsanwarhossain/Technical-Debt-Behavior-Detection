//package Designite;
//
//import Designite.ui.TechnicalDebtAnalyzerUI;
//
//import javax.swing.*;
//
///**
// * Launcher class for the Designite UI
// */
//public class DesigniteUILauncher {
//    public static void main(String[] args) {
//        // Check if command line arguments are provided
//        if (args.length > 0) {
//            // If arguments are provided, use the command line interface
//            try {
//                Designite.main(args);
//            } catch (Exception e) {
//                System.err.println("Error executing Designite: " + e.getMessage());
//                e.printStackTrace();
//            }
//        } else {
//            // If no arguments are provided, launch the GUI
//            SwingUtilities.invokeLater(() -> {
//                TechnicalDebtAnalyzerUI ui = new TechnicalDebtAnalyzerUI();
//                ui.setVisible(true);
//            });
//        }
//    }
//}

package Designite;

import Designite.ui.DesigniteUI;

/**
 * Launcher class to start Designite with a graphical user interface
 */
public class DesigniteUILauncher {
    public static void main(String[] args) {
        DesigniteUI.main(args);
    }
}