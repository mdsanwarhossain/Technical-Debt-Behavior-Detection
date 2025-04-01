//package Designite.ui;
//
//import Designite.Designite;
//
//import javax.swing.*;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import java.awt.*;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.concurrent.ExecutionException;
//
//public class TechnicalDebtAnalyzerUI extends JFrame {
//    private JTextArea codeInputArea;
//    private JTextArea outputArea;
//    private JButton analyzeButton;
//    private JButton loadFileButton;
//    private JButton saveResultsButton;
//    private JButton clearButton;
//    private JComboBox<String> smellTypeComboBox;
//    private JTextField inputPathField;
//    private JTextField outputPathField;
//    private JButton browseInputButton;
//    private JButton browseOutputButton;
//    private File tempInputDir;
//    private File tempOutputDir;
//
//    public TechnicalDebtAnalyzerUI() {
//        super("Technical Debt Analyzer");
//        setupUI();
//    }
//
//    private void setupUI() {
//        // Set application icon and look and feel
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(900, 700);
//        setLocationRelativeTo(null);
//
//        // Create panels
//        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
//        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        // Set up paths panel
//        JPanel pathsPanel = createPathsPanel();
//        mainPanel.add(pathsPanel, BorderLayout.NORTH);
//
//        // Set up code input panel
//        JPanel codePanel = createCodePanel();
//        mainPanel.add(codePanel, BorderLayout.CENTER);
//
//        // Set up buttons panel
//        JPanel buttonsPanel = createButtonsPanel();
//        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
//
//        // Add main panel to frame
//        add(mainPanel);
//    }
//
//    private JPanel createPathsPanel() {
//        JPanel pathsPanel = new JPanel(new GridBagLayout());
//        pathsPanel.setBorder(BorderFactory.createTitledBorder("Project Paths"));
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.insets = new Insets(5, 5, 5, 5);
//
//        // Input path
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.weightx = 0;
//        pathsPanel.add(new JLabel("Input Source Path:"), gbc);
//
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        inputPathField = new JTextField(30);
//        pathsPanel.add(inputPathField, gbc);
//
//        gbc.gridx = 2;
//        gbc.weightx = 0;
//        browseInputButton = new JButton("Browse...");
//        browseInputButton.addActionListener(e -> browseForInputFolder());
//        pathsPanel.add(browseInputButton, gbc);
//
//        // Output path
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        pathsPanel.add(new JLabel("Output Path:"), gbc);
//
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        outputPathField = new JTextField(30);
//        pathsPanel.add(outputPathField, gbc);
//
//        gbc.gridx = 2;
//        gbc.weightx = 0;
//        browseOutputButton = new JButton("Browse...");
//        browseOutputButton.addActionListener(e -> browseForOutputFolder());
//        pathsPanel.add(browseOutputButton, gbc);
//
//        return pathsPanel;
//    }
//
//    private JPanel createCodePanel() {
//        JPanel codePanel = new JPanel(new BorderLayout(5, 5));
//
//        // Code input area
//        codeInputArea = new JTextArea();
//        codeInputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        JScrollPane codeScrollPane = new JScrollPane(codeInputArea);
//        codeScrollPane.setBorder(BorderFactory.createTitledBorder("Java Code Input"));
//        codePanel.add(codeScrollPane, BorderLayout.CENTER);
//
//        // Output area
//        outputArea = new JTextArea();
//        outputArea.setEditable(false);
//        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        JScrollPane outputScrollPane = new JScrollPane(outputArea);
//        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Analysis Results"));
//
//        // Add both panels to a split pane
//        JSplitPane splitPane = new JSplitPane(
//                JSplitPane.VERTICAL_SPLIT,
//                codeScrollPane,
//                outputScrollPane);
//        splitPane.setResizeWeight(0.5);
//        codePanel.add(splitPane, BorderLayout.CENTER);
//
//        return codePanel;
//    }
//
//    private JPanel createButtonsPanel() {
//        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
//
//        // Smell type selection
//        buttonsPanel.add(new JLabel("Analysis Type:"));
//        smellTypeComboBox = new JComboBox<>(new String[]{
//                "All Smells",
//                "Design Smells",
//                "Implementation Smells",
//                "Metrics Only"
//        });
//        buttonsPanel.add(smellTypeComboBox);
//
//        // Action buttons
//        loadFileButton = new JButton("Load Java File");
//        loadFileButton.addActionListener(e -> loadJavaFile());
//        buttonsPanel.add(loadFileButton);
//
//        analyzeButton = new JButton("Analyze Code");
//        analyzeButton.addActionListener(e -> analyzeCode());
//        buttonsPanel.add(analyzeButton);
//
//        saveResultsButton = new JButton("Save Results");
//        saveResultsButton.addActionListener(e -> saveResults());
//        saveResultsButton.setEnabled(false);
//        buttonsPanel.add(saveResultsButton);
//
//        clearButton = new JButton("Clear");
//        clearButton.addActionListener(e -> {
//            codeInputArea.setText("");
//            outputArea.setText("");
//            saveResultsButton.setEnabled(false);
//        });
//        buttonsPanel.add(clearButton);
//
//        return buttonsPanel;
//    }
//
//    private void browseForInputFolder() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        fileChooser.setDialogTitle("Select Input Source Folder");
//
//        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//            inputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
//        }
//    }
//
//    private void browseForOutputFolder() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        fileChooser.setDialogTitle("Select Output Folder");
//
//        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//            outputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
//        }
//    }
//
//    private void loadJavaFile() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Load Java File");
//        fileChooser.setFileFilter(new FileNameExtensionFilter("Java Files", "java"));
//
//        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//            try {
//                String content = Files.readString(fileChooser.getSelectedFile().toPath());
//                codeInputArea.setText(content);
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(this,
//                        "Error loading file: " + ex.getMessage(),
//                        "Error",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    private void analyzeCode() {
//        // Validate input and output paths
//        String inputPath = inputPathField.getText().trim();
//        String outputPath = outputPathField.getText().trim();
//
//        if (inputPath.isEmpty() && codeInputArea.getText().trim().isEmpty()) {
//            JOptionPane.showMessageDialog(this,
//                    "Please either specify an input path or enter Java code.",
//                    "Input Required",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        if (outputPath.isEmpty()) {
//            JOptionPane.showMessageDialog(this,
//                    "Please specify an output path.",
//                    "Output Required",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        // Disable UI during analysis
//        setUIEnabled(false);
//        outputArea.setText("Analyzing code...\n");
//
//        // Run analysis in background thread
//        SwingWorker<String, Void> worker = new SwingWorker<>() {
//            @Override
//            protected String doInBackground() throws Exception {
//                return performAnalysis(inputPath, outputPath);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    String result = get();
//                    outputArea.setText(result);
//                    saveResultsButton.setEnabled(true);
//                } catch (InterruptedException | ExecutionException e) {
//                    outputArea.setText("Error during analysis: " + e.getMessage());
//                } finally {
//                    setUIEnabled(true);
//                }
//            }
//        };
//
//        worker.execute();
//    }
//
//    private String performAnalysis(String inputPath, String outputPath) {
//        try {
//            // If code is provided in the textarea, save it to a temporary file
//            if (!codeInputArea.getText().trim().isEmpty()) {
//                // Create temp directories if needed
//                tempInputDir = createTempDirectory("designite_input");
//
//                if (inputPath.isEmpty()) {
//                    inputPath = tempInputDir.getAbsolutePath();
//                }
//
//                // Save code to a file in the input directory
//                Path javaFilePath = Paths.get(inputPath, "AnalyzedCode.java");
//                Files.writeString(javaFilePath, codeInputArea.getText());
//            }
//
//            // Create temp output directory if needed
//            if (tempOutputDir == null) {
//                tempOutputDir = createTempDirectory("designite_output");
//            }
//
//            String effectiveOutputPath = outputPath.isEmpty() ? tempOutputDir.getAbsolutePath() : outputPath;
//
//            // Redirect System.out to capture console output
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            PrintStream originalOut = System.out;
//            System.setOut(new PrintStream(outputStream));
//
//            // Run Designite analysis
//            String[] args = {"-i", inputPath, "-o", effectiveOutputPath};
//            Designite.main(args);
//
//            // Restore original System.out
//            System.setOut(originalOut);
//
//            // Read output files
//            StringBuilder results = new StringBuilder();
//            results.append("Analysis completed. Results saved to: ").append(effectiveOutputPath).append("\n\n");
//            results.append("Console output:\n").append(outputStream.toString()).append("\n\n");
//
//            // Read result files from output directory
//            try {
//                Files.walk(Paths.get(effectiveOutputPath))
//                        .filter(Files::isRegularFile)
//                        .filter(p -> p.toString().endsWith(".csv"))
//                        .forEach(file -> {
//                            try {
//                                results.append("File: ").append(file.getFileName()).append("\n");
//                                results.append("---------------------------------------------------\n");
//                                results.append(Files.readString(file)).append("\n\n");
//                            } catch (IOException e) {
//                                results.append("Error reading file: ").append(e.getMessage()).append("\n");
//                            }
//                        });
//            } catch (IOException e) {
//                results.append("Error reading output directory: ").append(e.getMessage()).append("\n");
//            }
//
//            return results.toString();
//
//        } catch (Exception e) {
//            return "Error during analysis: " + e.getMessage();
//        }
//    }
//
//    private File createTempDirectory(String prefix) throws IOException {
//        return Files.createTempDirectory(prefix).toFile();
//    }
//
//    private void saveResults() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Analysis Results");
//        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
//
//        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
//            File file = fileChooser.getSelectedFile();
//            if (!file.getName().endsWith(".txt")) {
//                file = new File(file.getAbsolutePath() + ".txt");
//            }
//
//            try {
//                Files.writeString(file.toPath(), outputArea.getText());
//                JOptionPane.showMessageDialog(this,
//                        "Results saved successfully.",
//                        "Success",
//                        JOptionPane.INFORMATION_MESSAGE);
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(this,
//                        "Error saving results: " + ex.getMessage(),
//                        "Error",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    private void setUIEnabled(boolean enabled) {
//        inputPathField.setEnabled(enabled);
//        outputPathField.setEnabled(enabled);
//        browseInputButton.setEnabled(enabled);
//        browseOutputButton.setEnabled(enabled);
//        codeInputArea.setEnabled(enabled);
//        analyzeButton.setEnabled(enabled);
//        loadFileButton.setEnabled(enabled);
//        clearButton.setEnabled(enabled);
//        smellTypeComboBox.setEnabled(enabled);
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new TechnicalDebtAnalyzerUI().setVisible(true);
//        });
//    }
//}
package Designite.ui;

import Designite.Designite;
import Designite.InputArgs;
import Designite.SourceModel.SM_Project;
import Designite.utils.Constants;
import Designite.utils.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;

public class DesigniteUI extends JFrame {
    private JTextField sourcePathField;
    private JTextField outputPathField;
    private JTextArea logArea;
    private JButton analyzeButton;
    private JProgressBar progressBar;
    private JButton sourceSelectButton;
    private JButton outputSelectButton;
    private JPanel resultPanel;
    private JTabbedPane resultTabs;

    public DesigniteUI() {
        setTitle("Designite Java - Technical Debt Analyzer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        // Initialize text fields
        sourcePathField = new JTextField(30);
        outputPathField = new JTextField(30);

        // Initialize buttons
        sourceSelectButton = new JButton("Browse...");
        outputSelectButton = new JButton("Browse...");
        analyzeButton = new JButton("Analyze Code");

        // Initialize log area
        logArea = new JTextArea();
        logArea.setEditable(false);

        // Initialize progress bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // Initialize result panel
        resultPanel = new JPanel(new BorderLayout());
        resultTabs = new JTabbedPane();
        resultPanel.add(resultTabs, BorderLayout.CENTER);

        // Add file chooser functionality
        sourceSelectButton.addActionListener(e -> selectSourceFolder());
        outputSelectButton.addActionListener(e -> selectOutputFolder());

        // Add analyze functionality
        analyzeButton.addActionListener(e -> analyzeCode());
    }

    private void layoutComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Source folder row
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Source Folder:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(sourcePathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        inputPanel.add(sourceSelectButton, gbc);

        // Output folder row
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Output Folder:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(outputPathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        inputPanel.add(outputSelectButton, gbc);

        // Analyze button row
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(analyzeButton, gbc);

        // Progress bar
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        progressPanel.add(progressBar, BorderLayout.CENTER);

        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log"));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);

        // Split pane for results and log
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                resultPanel,
                logPanel
        );
        splitPane.setResizeWeight(0.7);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void selectSourceFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Source Folder");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            sourcePathField.setText(selectedFolder.getAbsolutePath());
        }
    }

    private void selectOutputFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Output Folder");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            outputPathField.setText(selectedFolder.getAbsolutePath());
        }
    }

    private void analyzeCode() {
        String sourcePath = sourcePathField.getText().trim();
        String outputPath = outputPathField.getText().trim();

        if (sourcePath.isEmpty() || outputPath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please specify both source and output folders.",
                    "Missing Input",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable UI components during analysis
        setComponentsEnabled(false);
        progressBar.setIndeterminate(true);

        // Reset log area
        logArea.setText("");

        // Create a custom Logger instance that redirects to our UI
        Logger.setLogHandler(message -> {
            SwingUtilities.invokeLater(() -> {
                logArea.append(message + "\n");
                // Auto-scroll to the bottom
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        });

        // Run analysis in a background thread to keep UI responsive
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Prepare arguments
                    String[] args = {"-i", sourcePath, "-o", outputPath};

                    // Run the Designite tool
                    Designite.main(args);

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        logArea.append("ERROR: " + e.getMessage() + "\n");
                        JOptionPane.showMessageDialog(DesigniteUI.this,
                                "Analysis failed: " + e.getMessage(),
                                "Analysis Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
                setComponentsEnabled(true);
                loadResults(outputPath);
            }
        }.execute();
    }

    private void setComponentsEnabled(boolean enabled) {
        sourcePathField.setEnabled(enabled);
        outputPathField.setEnabled(enabled);
        sourceSelectButton.setEnabled(enabled);
        outputSelectButton.setEnabled(enabled);
        analyzeButton.setEnabled(enabled);
    }

    private void loadResults(String outputPath) {
        // Clear previous results
        resultTabs.removeAll();

        // Look for CSV files in the output directory
        File outputDir = new File(outputPath);
        File[] resultFiles = outputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        if (resultFiles != null && resultFiles.length > 0) {
            for (File file : resultFiles) {
                try {
                    String content = Files.readString(file.toPath());
                    JTextArea textArea = new JTextArea(content);
                    textArea.setEditable(false);

                    JScrollPane scrollPane = new JScrollPane(textArea);
                    resultTabs.addTab(file.getName(), scrollPane);
                } catch (IOException e) {
                    logArea.append("Error loading result file: " + file.getName() + "\n");
                }
            }
            logArea.append("Analysis completed. Results loaded.\n");
        } else {
            logArea.append("Analysis completed, but no result files were found.\n");
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new DesigniteUI().setVisible(true);
        });
    }
}