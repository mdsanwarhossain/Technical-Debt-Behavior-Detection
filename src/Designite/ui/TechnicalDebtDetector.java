////package Designite.ui;
////
////import Designite.Designite;
////
////import javax.swing.*;
////import javax.swing.filechooser.FileNameExtensionFilter;
////import java.awt.*;
////import java.io.ByteArrayOutputStream;
////import java.io.File;
////import java.io.IOException;
////import java.io.PrintStream;
////import java.nio.file.Files;
////import java.nio.file.Path;
////import java.nio.file.Paths;
////import java.util.concurrent.ExecutionException;
////
////public class TechnicalDebtAnalyzerUI extends JFrame {
////    private JTextArea codeInputArea;
////    private JTextArea outputArea;
////    private JButton analyzeButton;
////    private JButton loadFileButton;
////    private JButton saveResultsButton;
////    private JButton clearButton;
////    private JComboBox<String> smellTypeComboBox;
////    private JTextField inputPathField;
////    private JTextField outputPathField;
////    private JButton browseInputButton;
////    private JButton browseOutputButton;
////    private File tempInputDir;
////    private File tempOutputDir;
////
////    public TechnicalDebtAnalyzerUI() {
////        super("Technical Debt Analyzer");
////        setupUI();
////    }
////
////    private void setupUI() {
////        // Set application icon and look and feel
////        try {
////            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////        setSize(900, 700);
////        setLocationRelativeTo(null);
////
////        // Create panels
////        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
////        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
////
////        // Set up paths panel
////        JPanel pathsPanel = createPathsPanel();
////        mainPanel.add(pathsPanel, BorderLayout.NORTH);
////
////        // Set up code input panel
////        JPanel codePanel = createCodePanel();
////        mainPanel.add(codePanel, BorderLayout.CENTER);
////
////        // Set up buttons panel
////        JPanel buttonsPanel = createButtonsPanel();
////        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
////
////        // Add main panel to frame
////        add(mainPanel);
////    }
////
////    private JPanel createPathsPanel() {
////        JPanel pathsPanel = new JPanel(new GridBagLayout());
////        pathsPanel.setBorder(BorderFactory.createTitledBorder("Project Paths"));
////
////        GridBagConstraints gbc = new GridBagConstraints();
////        gbc.fill = GridBagConstraints.HORIZONTAL;
////        gbc.insets = new Insets(5, 5, 5, 5);
////
////        // Input path
////        gbc.gridx = 0;
////        gbc.gridy = 0;
////        gbc.weightx = 0;
////        pathsPanel.add(new JLabel("Input Source Path:"), gbc);
////
////        gbc.gridx = 1;
////        gbc.weightx = 1.0;
////        inputPathField = new JTextField(30);
////        pathsPanel.add(inputPathField, gbc);
////
////        gbc.gridx = 2;
////        gbc.weightx = 0;
////        browseInputButton = new JButton("Browse...");
////        browseInputButton.addActionListener(e -> browseForInputFolder());
////        pathsPanel.add(browseInputButton, gbc);
////
////        // Output path
////        gbc.gridx = 0;
////        gbc.gridy = 1;
////        pathsPanel.add(new JLabel("Output Path:"), gbc);
////
////        gbc.gridx = 1;
////        gbc.weightx = 1.0;
////        outputPathField = new JTextField(30);
////        pathsPanel.add(outputPathField, gbc);
////
////        gbc.gridx = 2;
////        gbc.weightx = 0;
////        browseOutputButton = new JButton("Browse...");
////        browseOutputButton.addActionListener(e -> browseForOutputFolder());
////        pathsPanel.add(browseOutputButton, gbc);
////
////        return pathsPanel;
////    }
////
////    private JPanel createCodePanel() {
////        JPanel codePanel = new JPanel(new BorderLayout(5, 5));
////
////        // Code input area
////        codeInputArea = new JTextArea();
////        codeInputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
////        JScrollPane codeScrollPane = new JScrollPane(codeInputArea);
////        codeScrollPane.setBorder(BorderFactory.createTitledBorder("Java Code Input"));
////        codePanel.add(codeScrollPane, BorderLayout.CENTER);
////
////        // Output area
////        outputArea = new JTextArea();
////        outputArea.setEditable(false);
////        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
////        JScrollPane outputScrollPane = new JScrollPane(outputArea);
////        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Analysis Results"));
////
////        // Add both panels to a split pane
////        JSplitPane splitPane = new JSplitPane(
////                JSplitPane.VERTICAL_SPLIT,
////                codeScrollPane,
////                outputScrollPane);
////        splitPane.setResizeWeight(0.5);
////        codePanel.add(splitPane, BorderLayout.CENTER);
////
////        return codePanel;
////    }
////
////    private JPanel createButtonsPanel() {
////        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
////
////        // Smell type selection
////        buttonsPanel.add(new JLabel("Analysis Type:"));
////        smellTypeComboBox = new JComboBox<>(new String[]{
////                "All Smells",
////                "Design Smells",
////                "Implementation Smells",
////                "Metrics Only"
////        });
////        buttonsPanel.add(smellTypeComboBox);
////
////        // Action buttons
////        loadFileButton = new JButton("Load Java File");
////        loadFileButton.addActionListener(e -> loadJavaFile());
////        buttonsPanel.add(loadFileButton);
////
////        analyzeButton = new JButton("Analyze Code");
////        analyzeButton.addActionListener(e -> analyzeCode());
////        buttonsPanel.add(analyzeButton);
////
////        saveResultsButton = new JButton("Save Results");
////        saveResultsButton.addActionListener(e -> saveResults());
////        saveResultsButton.setEnabled(false);
////        buttonsPanel.add(saveResultsButton);
////
////        clearButton = new JButton("Clear");
////        clearButton.addActionListener(e -> {
////            codeInputArea.setText("");
////            outputArea.setText("");
////            saveResultsButton.setEnabled(false);
////        });
////        buttonsPanel.add(clearButton);
////
////        return buttonsPanel;
////    }
////
////    private void browseForInputFolder() {
////        JFileChooser fileChooser = new JFileChooser();
////        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
////        fileChooser.setDialogTitle("Select Input Source Folder");
////
////        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
////            inputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
////        }
////    }
////
////    private void browseForOutputFolder() {
////        JFileChooser fileChooser = new JFileChooser();
////        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
////        fileChooser.setDialogTitle("Select Output Folder");
////
////        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
////            outputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
////        }
////    }
////
////    private void loadJavaFile() {
////        JFileChooser fileChooser = new JFileChooser();
////        fileChooser.setDialogTitle("Load Java File");
////        fileChooser.setFileFilter(new FileNameExtensionFilter("Java Files", "java"));
////
////        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
////            try {
////                String content = Files.readString(fileChooser.getSelectedFile().toPath());
////                codeInputArea.setText(content);
////            } catch (IOException ex) {
////                JOptionPane.showMessageDialog(this,
////                        "Error loading file: " + ex.getMessage(),
////                        "Error",
////                        JOptionPane.ERROR_MESSAGE);
////            }
////        }
////    }
////
////    private void analyzeCode() {
////        // Validate input and output paths
////        String inputPath = inputPathField.getText().trim();
////        String outputPath = outputPathField.getText().trim();
////
////        if (inputPath.isEmpty() && codeInputArea.getText().trim().isEmpty()) {
////            JOptionPane.showMessageDialog(this,
////                    "Please either specify an input path or enter Java code.",
////                    "Input Required",
////                    JOptionPane.WARNING_MESSAGE);
////            return;
////        }
////
////        if (outputPath.isEmpty()) {
////            JOptionPane.showMessageDialog(this,
////                    "Please specify an output path.",
////                    "Output Required",
////                    JOptionPane.WARNING_MESSAGE);
////            return;
////        }
////
////        // Disable UI during analysis
////        setUIEnabled(false);
////        outputArea.setText("Analyzing code...\n");
////
////        // Run analysis in background thread
////        SwingWorker<String, Void> worker = new SwingWorker<>() {
////            @Override
////            protected String doInBackground() throws Exception {
////                return performAnalysis(inputPath, outputPath);
////            }
////
////            @Override
////            protected void done() {
////                try {
////                    String result = get();
////                    outputArea.setText(result);
////                    saveResultsButton.setEnabled(true);
////                } catch (InterruptedException | ExecutionException e) {
////                    outputArea.setText("Error during analysis: " + e.getMessage());
////                } finally {
////                    setUIEnabled(true);
////                }
////            }
////        };
////
////        worker.execute();
////    }
////
////    private String performAnalysis(String inputPath, String outputPath) {
////        try {
////            // If code is provided in the textarea, save it to a temporary file
////            if (!codeInputArea.getText().trim().isEmpty()) {
////                // Create temp directories if needed
////                tempInputDir = createTempDirectory("designite_input");
////
////                if (inputPath.isEmpty()) {
////                    inputPath = tempInputDir.getAbsolutePath();
////                }
////
////                // Save code to a file in the input directory
////                Path javaFilePath = Paths.get(inputPath, "AnalyzedCode.java");
////                Files.writeString(javaFilePath, codeInputArea.getText());
////            }
////
////            // Create temp output directory if needed
////            if (tempOutputDir == null) {
////                tempOutputDir = createTempDirectory("designite_output");
////            }
////
////            String effectiveOutputPath = outputPath.isEmpty() ? tempOutputDir.getAbsolutePath() : outputPath;
////
////            // Redirect System.out to capture console output
////            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
////            PrintStream originalOut = System.out;
////            System.setOut(new PrintStream(outputStream));
////
////            // Run Designite analysis
////            String[] args = {"-i", inputPath, "-o", effectiveOutputPath};
////            Designite.main(args);
////
////            // Restore original System.out
////            System.setOut(originalOut);
////
////            // Read output files
////            StringBuilder results = new StringBuilder();
////            results.append("Analysis completed. Results saved to: ").append(effectiveOutputPath).append("\n\n");
////            results.append("Console output:\n").append(outputStream.toString()).append("\n\n");
////
////            // Read result files from output directory
////            try {
////                Files.walk(Paths.get(effectiveOutputPath))
////                        .filter(Files::isRegularFile)
////                        .filter(p -> p.toString().endsWith(".csv"))
////                        .forEach(file -> {
////                            try {
////                                results.append("File: ").append(file.getFileName()).append("\n");
////                                results.append("---------------------------------------------------\n");
////                                results.append(Files.readString(file)).append("\n\n");
////                            } catch (IOException e) {
////                                results.append("Error reading file: ").append(e.getMessage()).append("\n");
////                            }
////                        });
////            } catch (IOException e) {
////                results.append("Error reading output directory: ").append(e.getMessage()).append("\n");
////            }
////
////            return results.toString();
////
////        } catch (Exception e) {
////            return "Error during analysis: " + e.getMessage();
////        }
////    }
////
////    private File createTempDirectory(String prefix) throws IOException {
////        return Files.createTempDirectory(prefix).toFile();
////    }
////
////    private void saveResults() {
////        JFileChooser fileChooser = new JFileChooser();
////        fileChooser.setDialogTitle("Save Analysis Results");
////        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
////
////        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
////            File file = fileChooser.getSelectedFile();
////            if (!file.getName().endsWith(".txt")) {
////                file = new File(file.getAbsolutePath() + ".txt");
////            }
////
////            try {
////                Files.writeString(file.toPath(), outputArea.getText());
////                JOptionPane.showMessageDialog(this,
////                        "Results saved successfully.",
////                        "Success",
////                        JOptionPane.INFORMATION_MESSAGE);
////            } catch (IOException ex) {
////                JOptionPane.showMessageDialog(this,
////                        "Error saving results: " + ex.getMessage(),
////                        "Error",
////                        JOptionPane.ERROR_MESSAGE);
////            }
////        }
////    }
////
////    private void setUIEnabled(boolean enabled) {
////        inputPathField.setEnabled(enabled);
////        outputPathField.setEnabled(enabled);
////        browseInputButton.setEnabled(enabled);
////        browseOutputButton.setEnabled(enabled);
////        codeInputArea.setEnabled(enabled);
////        analyzeButton.setEnabled(enabled);
////        loadFileButton.setEnabled(enabled);
////        clearButton.setEnabled(enabled);
////        smellTypeComboBox.setEnabled(enabled);
////    }
////
////    public static void main(String[] args) {
////        SwingUtilities.invokeLater(() -> {
////            new TechnicalDebtAnalyzerUI().setVisible(true);
////        });
////    }
////}
//package Designite.ui;
//
//import Designite.Designite;
//import Designite.InputArgs;
//import Designite.SourceModel.SM_Project;
//import Designite.utils.Constants;
//import Designite.utils.Logger;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.concurrent.ExecutionException;
//
//public class DesigniteUI extends JFrame {
//    private JTextField sourcePathField;
//    private JTextField outputPathField;
//    private JTextArea logArea;
//    private JButton analyzeButton;
//    private JProgressBar progressBar;
//    private JButton sourceSelectButton;
//    private JButton outputSelectButton;
//    private JPanel resultPanel;
//    private JTabbedPane resultTabs;
//
//    public DesigniteUI() {
//        setTitle("Designite Java - Technical Debt Analyzer");
//        setSize(800, 600);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        initComponents();
//        layoutComponents();
//    }
//
//    private void initComponents() {
//        // Initialize text fields
//        sourcePathField = new JTextField(30);
//        outputPathField = new JTextField(30);
//
//        // Initialize buttons
//        sourceSelectButton = new JButton("Browse...");
//        outputSelectButton = new JButton("Browse...");
//        analyzeButton = new JButton("Analyze Code");
//
//        // Initialize log area
//        logArea = new JTextArea();
//        logArea.setEditable(false);
//
//        // Initialize progress bar
//        progressBar = new JProgressBar();
//        progressBar.setStringPainted(true);
//
//        // Initialize result panel
//        resultPanel = new JPanel(new BorderLayout());
//        resultTabs = new JTabbedPane();
//        resultPanel.add(resultTabs, BorderLayout.CENTER);
//
//        // Add file chooser functionality
//        sourceSelectButton.addActionListener(e -> selectSourceFolder());
//        outputSelectButton.addActionListener(e -> selectOutputFolder());
//
//        // Add analyze functionality
//        analyzeButton.addActionListener(e -> analyzeCode());
//    }
//
//    private void layoutComponents() {
//        // Main panel with padding
//        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
//        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        // Input panel
//        JPanel inputPanel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        // Source folder row
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        inputPanel.add(new JLabel("Source Folder:"), gbc);
//
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        inputPanel.add(sourcePathField, gbc);
//
//        gbc.gridx = 2;
//        gbc.weightx = 0.0;
//        inputPanel.add(sourceSelectButton, gbc);
//
//        // Output folder row
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        inputPanel.add(new JLabel("Output Folder:"), gbc);
//
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        inputPanel.add(outputPathField, gbc);
//
//        gbc.gridx = 2;
//        gbc.weightx = 0.0;
//        inputPanel.add(outputSelectButton, gbc);
//
//        // Analyze button row
//        gbc.gridx = 1;
//        gbc.gridy = 2;
//        gbc.gridwidth = 2;
//        gbc.anchor = GridBagConstraints.EAST;
//        inputPanel.add(analyzeButton, gbc);
//
//        // Progress bar
//        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
//        progressPanel.add(progressBar, BorderLayout.CENTER);
//
//        // Log panel
//        JPanel logPanel = new JPanel(new BorderLayout());
//        logPanel.setBorder(BorderFactory.createTitledBorder("Log"));
//        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
//
//        // Add components to main panel
//        mainPanel.add(inputPanel, BorderLayout.NORTH);
//        mainPanel.add(progressPanel, BorderLayout.SOUTH);
//
//        // Split pane for results and log
//        JSplitPane splitPane = new JSplitPane(
//                JSplitPane.VERTICAL_SPLIT,
//                resultPanel,
//                logPanel
//        );
//        splitPane.setResizeWeight(0.7);
//        mainPanel.add(splitPane, BorderLayout.CENTER);
//
//        setContentPane(mainPanel);
//    }
//
//    private void selectSourceFolder() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        fileChooser.setDialogTitle("Select Source Folder");
//
//        int result = fileChooser.showOpenDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File selectedFolder = fileChooser.getSelectedFile();
//            sourcePathField.setText(selectedFolder.getAbsolutePath());
//        }
//    }
//
//    private void selectOutputFolder() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        fileChooser.setDialogTitle("Select Output Folder");
//
//        int result = fileChooser.showOpenDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File selectedFolder = fileChooser.getSelectedFile();
//            outputPathField.setText(selectedFolder.getAbsolutePath());
//        }
//    }
//
//    private void analyzeCode() {
//        String sourcePath = sourcePathField.getText().trim();
//        String outputPath = outputPathField.getText().trim();
//
//        if (sourcePath.isEmpty() || outputPath.isEmpty()) {
//            JOptionPane.showMessageDialog(this,
//                    "Please specify both source and output folders.",
//                    "Missing Input",
//                    JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        // Disable UI components during analysis
//        setComponentsEnabled(false);
//        progressBar.setIndeterminate(true);
//
//        // Reset log area
//        logArea.setText("");
//
//        // Create a custom Logger instance that redirects to our UI
//        Logger.setLogHandler(message -> {
//            SwingUtilities.invokeLater(() -> {
//                logArea.append(message + "\n");
//                // Auto-scroll to the bottom
//                logArea.setCaretPosition(logArea.getDocument().getLength());
//            });
//        });
//
//        // Run analysis in a background thread to keep UI responsive
//        new SwingWorker<Void, Void>() {
//            @Override
//            protected Void doInBackground() throws Exception {
//                try {
//                    // Prepare arguments
//                    String[] args = {"-i", sourcePath, "-o", outputPath};
//
//                    // Run the Designite tool
//                    Designite.main(args);
//
//                } catch (Exception e) {
//                    SwingUtilities.invokeLater(() -> {
//                        logArea.append("ERROR: " + e.getMessage() + "\n");
//                        JOptionPane.showMessageDialog(DesigniteUI.this,
//                                "Analysis failed: " + e.getMessage(),
//                                "Analysis Error",
//                                JOptionPane.ERROR_MESSAGE);
//                    });
//                }
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                progressBar.setIndeterminate(false);
//                progressBar.setValue(100);
//                setComponentsEnabled(true);
//                loadResults(outputPath);
//            }
//        }.execute();
//    }
//
//    private void setComponentsEnabled(boolean enabled) {
//        sourcePathField.setEnabled(enabled);
//        outputPathField.setEnabled(enabled);
//        sourceSelectButton.setEnabled(enabled);
//        outputSelectButton.setEnabled(enabled);
//        analyzeButton.setEnabled(enabled);
//    }
//
//    private void loadResults(String outputPath) {
//        // Clear previous results
//        resultTabs.removeAll();
//
//        // Look for CSV files in the output directory
//        File outputDir = new File(outputPath);
//        File[] resultFiles = outputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
//
//        if (resultFiles != null && resultFiles.length > 0) {
//            for (File file : resultFiles) {
//                try {
//                    String content = Files.readString(file.toPath());
//                    JTextArea textArea = new JTextArea(content);
//                    textArea.setEditable(false);
//
//                    JScrollPane scrollPane = new JScrollPane(textArea);
//                    resultTabs.addTab(file.getName(), scrollPane);
//                } catch (IOException e) {
//                    logArea.append("Error loading result file: " + file.getName() + "\n");
//                }
//            }
//            logArea.append("Analysis completed. Results loaded.\n");
//        } else {
//            logArea.append("Analysis completed, but no result files were found.\n");
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            // Set system look and feel
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        SwingUtilities.invokeLater(() -> {
//            new DesigniteUI().setVisible(true);
//        });
//    }
//}
package Designite.ui;

import Designite.Designite;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TechnicalDebtDetector extends Application {

    private TextArea codeInputArea;
    private TabPane resultsTabPane;
    private Button analyzeButton;
    private Button loadFileButton;
    private Button saveResultsButton;
    private ProgressBar progressBar;
    private Label statusLabel;
    private ComboBox<String> themeComboBox;

    // Temporary directories for input/output
    private Path tempInputDir;
    private Path tempOutputDir;

    // Store the results for saving
    private Map<String, List<String[]>> currentResults;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Technical Debt Behaviors Detector");

        // Create the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Create the top section with title and toolbar
        VBox topSection = createTopSection();
        mainLayout.setTop(topSection);

        // Create the center section with split pane
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.4);

        // Left side - Code input
        VBox codeInputBox = createCodeInputSection();

        // Right side - Results with tabs
        VBox resultsBox = createResultsSection();

        splitPane.getItems().addAll(codeInputBox, resultsBox);
        mainLayout.setCenter(splitPane);

        // Create the bottom section with status bar
        HBox statusBar = createStatusBar();
        mainLayout.setBottom(statusBar);

        // Set up the scene
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Create temporary directories for processing
        try {
            tempInputDir = Files.createTempDirectory("code_input_");
            tempOutputDir = Files.createTempDirectory("TD_output_");

            // Make sure they're deleted when the application exits
            tempInputDir.toFile().deleteOnExit();
            tempOutputDir.toFile().deleteOnExit();
        } catch (IOException e) {
            showError("Failed to create temporary directories: " + e.getMessage());
        }
    }

    private VBox createTopSection() {
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(0, 0, 10, 0));

        // Title
        Label titleLabel = new Label("Technical Debt Behaviors Detector");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        loadFileButton = new Button("Upload File");
        loadFileButton.setOnAction(e -> loadJavaFile());

        saveResultsButton = new Button("Save Results");
        saveResultsButton.setOnAction(e -> saveResults());
        saveResultsButton.setDisable(true);

        Label themeLabel = new Label("Theme:");
        themeComboBox = new ComboBox<>(FXCollections.observableArrayList("Light", "Dark"));
        themeComboBox.setValue("Light");
        themeComboBox.setOnAction(e -> changeTheme(themeComboBox.getValue()));

        toolbar.getChildren().addAll(loadFileButton, saveResultsButton, new Separator());//, themeLabel, themeComboBox

        topSection.getChildren().addAll(titleLabel, toolbar);
        return topSection;
    }

    private VBox createCodeInputSection() {
        VBox codeBox = new VBox(10);
        codeBox.setPadding(new Insets(10));

        Label codeLabel = new Label("Java Code Input");
        codeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        codeInputArea = new TextArea();
        codeInputArea.setPromptText("Paste your Java code here or load a file...");
        codeInputArea.setWrapText(true);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> codeInputArea.clear());

        analyzeButton = new Button("Analyze Code");
        analyzeButton.setPrefWidth(150);
        analyzeButton.setOnAction(e -> analyzeCode());

        buttonBox.getChildren().addAll(clearButton, analyzeButton);

        codeBox.getChildren().addAll(codeLabel, codeInputArea, buttonBox);
        VBox.setVgrow(codeInputArea, Priority.ALWAYS);

        return codeBox;
    }

    private VBox createResultsSection() {
        VBox resultsBox = new VBox(10);
        resultsBox.setPadding(new Insets(10));

        Label resultsLabel = new Label("Analysis Results");
        resultsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        resultsTabPane = new TabPane();

        // Create tabs for different result types
        Tab designSmellsTab = new Tab("Design Smells");
        designSmellsTab.setClosable(false);
        designSmellsTab.setContent(createResultTabContent("Design Smells"));

        Tab implementationSmellsTab = new Tab("Implementation Smells");
        implementationSmellsTab.setClosable(false);
        implementationSmellsTab.setContent(createResultTabContent("Implementation Smells"));

        Tab typeMetricsTab = new Tab("Type Metrics");
        typeMetricsTab.setClosable(false);
        typeMetricsTab.setContent(createResultTabContent("Type Metrics"));

        Tab methodMetricsTab = new Tab("Method Metrics");
        methodMetricsTab.setClosable(false);
        methodMetricsTab.setContent(createResultTabContent("Method Metrics"));

        Tab summaryTab = new Tab("Summary");
        summaryTab.setClosable(false);
        summaryTab.setContent(createSummaryTabContent());

        resultsTabPane.getTabs().addAll(summaryTab, designSmellsTab, implementationSmellsTab, typeMetricsTab, methodMetricsTab);

        resultsBox.getChildren().addAll(resultsLabel, resultsTabPane);
        VBox.setVgrow(resultsTabPane, Priority.ALWAYS);

        return resultsBox;
    }

    private VBox createResultTabContent(String tabName) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TableView<String[]> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No data available"));

        content.getChildren().add(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return content;
    }

    private VBox createSummaryTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label summaryTitle = new Label("Analysis Summary");
        summaryTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        statsGrid.setPadding(new Insets(10));

        // Add placeholder stats
        statsGrid.add(new Label("Total Design Smells:"), 0, 0);
        statsGrid.add(new Label("0"), 1, 0);

        statsGrid.add(new Label("Total Implementation Smells:"), 0, 1);
        statsGrid.add(new Label("0"), 1, 1);

        statsGrid.add(new Label("Average Cyclomatic Complexity:"), 0, 2);
        statsGrid.add(new Label("0"), 1, 2);

        statsGrid.add(new Label("Average LCOM:"), 0, 3);
        statsGrid.add(new Label("0"), 1, 3);

        // Add placeholder chart
        PieChart smellsChart = new PieChart();
        smellsChart.setTitle("Technical Debt Behavioural Distribution");
        smellsChart.setLabelsVisible(true);
        smellsChart.setPrefHeight(300);

        content.getChildren().addAll(summaryTitle, statsGrid, smellsChart);

        return content;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10, 0, 0, 0));

        statusLabel = new Label("Ready");
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);

        statusBar.getChildren().addAll(statusLabel, progressBar);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        return statusBar;
    }

    private void loadJavaFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Java File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Java Files", "*.java")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String content = Files.readString(selectedFile.toPath());
                codeInputArea.setText(content);
                statusLabel.setText("File loaded: " + selectedFile.getName());
            } catch (IOException e) {
                showError("Failed to load file: " + e.getMessage());
            }
        }
    }

    private void saveResults() {
        if (currentResults == null || currentResults.isEmpty()) {
            showError("No results to save");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Results");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ZIP Files", "*.zip")
        );

        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            try {
                // Create a temporary directory to store CSV files
                Path tempDir = Files.createTempDirectory("TD_results_");

                // Write each result to a CSV file
                for (Map.Entry<String, List<String[]>> entry : currentResults.entrySet()) {
                    Path csvFile = tempDir.resolve(entry.getKey() + ".csv");
                    writeCsvFile(csvFile, entry.getValue());
                }

                // Create a ZIP file with all CSV files
                Path zipFile = selectedFile.toPath();
                if (!zipFile.toString().endsWith(".zip")) {
                    zipFile = Paths.get(zipFile.toString() + ".zip");
                }

                createZipFile(tempDir, zipFile);

                // Clean up
                deleteDirectory(tempDir);

                statusLabel.setText("Results saved to: " + zipFile.getFileName());
            } catch (IOException e) {
                showError("Failed to save results: " + e.getMessage());
            }
        }
    }

    private void writeCsvFile(Path filePath, List<String[]> rows) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }

    private void createZipFile(Path sourceDir, Path zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile.toFile());
             java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(fos)) {

            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            String relativePath = sourceDir.relativize(path).toString();
                            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(relativePath);
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private void changeTheme(String theme) {
        // In a real application, you would apply CSS styles based on the selected theme
        statusLabel.setText("Theme changed to: " + theme);
    }

    private void analyzeCode() {
        String code = codeInputArea.getText();
        if (code == null || code.trim().isEmpty()) {
            showError("Please enter Java code to analyze");
            return;
        }

        // Disable the analyze button and show progress
        analyzeButton.setDisable(true);
        saveResultsButton.setDisable(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        statusLabel.setText("Analyzing code...");

        // Run analysis in background thread
        CompletableFuture.runAsync(() -> {
            try {
                // Save code to a temporary file
                Path javaFilePath = tempInputDir.resolve("AnalysisTarget.java");
                Files.write(javaFilePath, code.getBytes());

                // Run TD analysis
                String[] args = {"-i", tempInputDir.toString(), "-o", tempOutputDir.toString()};
                Designite.main(args);

                // Read results from output files
                currentResults = readResultFiles();

                // Update UI with results
                Platform.runLater(() -> {
                    displayResults(currentResults);
                    saveResultsButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Analysis failed: " + e.getMessage()));
                e.printStackTrace();
            } finally {
                // Re-enable the analyze button and reset progress
                Platform.runLater(() -> {
                    analyzeButton.setDisable(false);
                    progressBar.setProgress(0);
                    statusLabel.setText("Analysis complete");
                });
            }
        });
    }

    private Map<String, List<String[]>> readResultFiles() throws IOException {
        Map<String, List<String[]>> results = new HashMap<>();

        // Read design code smells
        results.put("designCodeSmells", readCsvFile(tempOutputDir.resolve("designCodeSmells.csv")));

        // Read implementation code smells
        results.put("implementationCodeSmells", readCsvFile(tempOutputDir.resolve("implementationCodeSmells.csv")));

        // Read type metrics
        results.put("typeMetrics", readCsvFile(tempOutputDir.resolve("typeMetrics.csv")));

        // Read method metrics
        results.put("methodMetrics", readCsvFile(tempOutputDir.resolve("methodMetrics.csv")));

        return results;
    }

    private List<String[]> readCsvFile(Path filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] cells = line.split(",");
                    rows.add(cells);
                }
            }
        }
        return rows;
    }

    private void displayResults(Map<String, List<String[]>> results) {
        // Update Design Smells tab
        updateTableView((TableView<String[]>) ((VBox) resultsTabPane.getTabs().get(1).getContent()).getChildren().get(0),
                results.get("designCodeSmells"));

        // Update Implementation Smells tab
        updateTableView((TableView<String[]>) ((VBox) resultsTabPane.getTabs().get(2).getContent()).getChildren().get(0),
                results.get("implementationCodeSmells"));

        // Update Type Metrics tab
        updateTableView((TableView<String[]>) ((VBox) resultsTabPane.getTabs().get(3).getContent()).getChildren().get(0),
                results.get("typeMetrics"));

        // Update Method Metrics tab
        updateTableView((TableView<String[]>) ((VBox) resultsTabPane.getTabs().get(4).getContent()).getChildren().get(0),
                results.get("methodMetrics"));

        // Update Summary tab
        updateSummaryTab(results);
    }

    private void updateTableView(TableView<String[]> tableView, List<String[]> data) {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        if (data == null || data.isEmpty()) {
            return;
        }

        // Create columns based on the header row
        String[] headers = data.get(0);
        for (int i = 0; i < headers.length; i++) {
            final int columnIndex = i;
            TableColumn<String[], String> column = new TableColumn<>(headers[columnIndex]);
            column.setCellValueFactory(cellData -> {
                String[] row = cellData.getValue();
                if (columnIndex < row.length) {
                    return new javafx.beans.property.SimpleStringProperty(row[columnIndex]);
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
            tableView.getColumns().add(column);
        }

        // Add data rows (skip header)
        for (int i = 1; i < data.size(); i++) {
            tableView.getItems().add(data.get(i));
        }
    }

    private void updateSummaryTab(Map<String, List<String[]>> results) {
        VBox summaryContent = (VBox) resultsTabPane.getTabs().get(0).getContent();
        GridPane statsGrid = (GridPane) summaryContent.getChildren().get(1);
        PieChart smellsChart = (PieChart) summaryContent.getChildren().get(2);

        // Count design smells
        int designSmellsCount = results.get("designCodeSmells").size() > 1 ?
                results.get("designCodeSmells").size() - 1 : 0;

        // Count implementation smells
        int implSmellsCount = results.get("implementationCodeSmells").size() > 1 ?
                results.get("implementationCodeSmells").size() - 1 : 0;

        // Calculate average CC
        double avgCC = 0;
        List<String[]> methodMetrics = results.get("methodMetrics");
        if (methodMetrics != null && methodMetrics.size() > 1) {
            int ccColumnIndex = -1;
            for (int i = 0; i < methodMetrics.get(0).length; i++) {
                if (methodMetrics.get(0)[i].equals("CC")) {
                    ccColumnIndex = i;
                    break;
                }
            }

            if (ccColumnIndex != -1) {
                double sum = 0;
                int count = 0;
                for (int i = 1; i < methodMetrics.size(); i++) {
                    String[] row = methodMetrics.get(i);
                    if (ccColumnIndex < row.length) {
                        try {
                            sum += Double.parseDouble(row[ccColumnIndex]);
                            count++;
                        } catch (NumberFormatException e) {
                            // Skip non-numeric values
                        }
                    }
                }

                if (count > 0) {
                    avgCC = sum / count;
                }
            }
        }

        // Calculate average LCOM
        double avgLCOM = 0;
        List<String[]> typeMetrics = results.get("typeMetrics");
        if (typeMetrics != null && typeMetrics.size() > 1) {
            int lcomColumnIndex = -1;
            for (int i = 0; i < typeMetrics.get(0).length; i++) {
                if (typeMetrics.get(0)[i].equals("LCOM")) {
                    lcomColumnIndex = i;
                    break;
                }
            }

            if (lcomColumnIndex != -1) {
                double sum = 0;
                int count = 0;
                for (int i = 1; i < typeMetrics.size(); i++) {
                    String[] row = typeMetrics.get(i);
                    if (lcomColumnIndex < row.length) {
                        try {
                            double lcom = Double.parseDouble(row[lcomColumnIndex]);
                            if (lcom >= 0) {  // Skip -1 values (not applicable)
                                sum += lcom;
                                count++;
                            }
                        } catch (NumberFormatException e) {
                            // Skip non-numeric values
                        }
                    }
                }

                if (count > 0) {
                    avgLCOM = sum / count;
                }
            }
        }

        // Update stats grid
        ((Label) statsGrid.getChildren().get(1)).setText(String.valueOf(designSmellsCount));
        ((Label) statsGrid.getChildren().get(3)).setText(String.valueOf(implSmellsCount));
        ((Label) statsGrid.getChildren().get(5)).setText(String.format("%.2f", avgCC));
        ((Label) statsGrid.getChildren().get(7)).setText(String.format("%.2f", avgLCOM));

        // Update pie chart
        smellsChart.getData().clear();

        // Count different types of smells
        Map<String, Integer> smellCounts = new HashMap<>();

        // Count design smells by type
        if (results.get("designCodeSmells").size() > 1) {
            int smellColumnIndex = -1;
            for (int i = 0; i < results.get("designCodeSmells").get(0).length; i++) {
                if (results.get("designCodeSmells").get(0)[i].equals("Code Smell")) {
                    smellColumnIndex = i;
                    break;
                }
            }

            if (smellColumnIndex != -1) {
                for (int i = 1; i < results.get("designCodeSmells").size(); i++) {
                    String[] row = results.get("designCodeSmells").get(i);
                    if (smellColumnIndex < row.length) {
                        String smell = row[smellColumnIndex];
                        smellCounts.put(smell, smellCounts.getOrDefault(smell, 0) + 1);
                    }
                }
            }
        }

        // Count implementation smells by type
        if (results.get("implementationCodeSmells").size() > 1) {
            int smellColumnIndex = -1;
            for (int i = 0; i < results.get("implementationCodeSmells").get(0).length; i++) {
                if (results.get("implementationCodeSmells").get(0)[i].equals("Code Smell")) {
                    smellColumnIndex = i;
                    break;
                }
            }

            if (smellColumnIndex != -1) {
                for (int i = 1; i < results.get("implementationCodeSmells").size(); i++) {
                    String[] row = results.get("implementationCodeSmells").get(i);
                    if (smellColumnIndex < row.length) {
                        String smell = row[smellColumnIndex];
                        smellCounts.put(smell, smellCounts.getOrDefault(smell, 0) + 1);
                    }
                }
            }
        }

        // Add data to pie chart
        for (Map.Entry<String, Integer> entry : smellCounts.entrySet()) {
            smellsChart.getData().add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        // Clean up temporary directories
        try {
            deleteDirectory(tempInputDir);
            deleteDirectory(tempOutputDir);
        } catch (IOException e) {
            System.err.println("Failed to delete temporary directories: " + e.getMessage());
        }
    }

    private void deleteDirectory(Path directory) throws IOException {
        if (directory != null && Files.exists(directory)) {
            Files.walk(directory)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}