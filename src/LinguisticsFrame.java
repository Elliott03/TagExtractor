import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;

public class LinguisticsFrame extends JFrame  {
    JPanel mainPanel;
    JPanel controlPanel;
    JButton textFileButton;
    JButton stopWordButton;
    JButton analyzeButton;
    JTextField textFileTF;
    JTextField stopWordTF;

    JFileChooser textFileChooser;
    JFileChooser stopWordChooser;
    File selectedTextFile;
    File selectedStopWord;

    BufferedReader textFileReader;
    BufferedReader stopWordReader;
    HashSet stopWordSet;
    HashMap textFileMap;

    JPanel textPanel;

    JTextArea output;
    JScrollPane scroller;

    JPanel savePanel;
    JButton saveButton;
    JTextField fileNameTF;
    String fileName;

    Map<String, Integer> tempMap;
    public LinguisticsFrame() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        createTextPanel();
        mainPanel.add(textPanel, BorderLayout.CENTER);

        createSavePanel();
        mainPanel.add(savePanel, BorderLayout.SOUTH);
        add(mainPanel);
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createSavePanel() {
        savePanel = new JPanel();
        saveButton = new JButton("Save as File");
        fileNameTF = new JTextField("File Name", 15);
        saveButton.addActionListener((ActionEvent ae) -> {
            fileName = fileNameTF.getText();
            File workingDirectory = new File(System.getProperty("user.dir"));
            Path file = Paths.get(workingDirectory.getPath() + "\\src\\" + fileName + ".txt");
            try {
                OutputStream out = new BufferedOutputStream(Files.newOutputStream(file, CREATE));

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));


                for (Object key : tempMap.keySet()) {
                    String keyText = key.toString();
                    String value = tempMap.get(key).toString();
                    writer.write(keyText + "\t" + value);
                    writer.newLine();
                }

                writer.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        savePanel.add(fileNameTF);
        savePanel.add(saveButton);
    }

    private void createTextPanel() {
        textPanel = new JPanel();
        output = new JTextArea(15,50);
        scroller = new JScrollPane(output);
        output.setEditable(false);
        textPanel.add(scroller);
    }

    private void createControlPanel() {
        stopWordChooser = new JFileChooser();
        textFileChooser = new JFileChooser();
        controlPanel = new JPanel();
        stopWordSet = new HashSet();
        textFileMap = new HashMap<String, Integer>();
        analyzeButton = new JButton("Analyze");
        textFileButton = new JButton("Choose Text File");
        stopWordButton = new JButton("Choose Stop Word File");
        textFileTF = new JTextField(15);
        stopWordTF = new JTextField(15);
        textFileTF.setEditable(false);
        stopWordTF.setEditable(false);



        textFileButton.addActionListener((ActionEvent ae) -> {
            File workingDirectory = new File(System.getProperty("user.dir"));

            textFileChooser.setCurrentDirectory(workingDirectory);

            if (textFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                selectedTextFile = textFileChooser.getSelectedFile();
                Path file = selectedTextFile.toPath();
                InputStream in = null;
                try {
                    in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                textFileReader = new BufferedReader(new InputStreamReader(in));
                String[] fileArray = file.toString().split("\\\\");
                String fileName = fileArray[fileArray.length - 1];
                textFileTF.setText(fileName);
            }
        });
        stopWordButton.addActionListener((ActionEvent ae) -> {

            File workingDirectory = new File(System.getProperty("user.dir"));

            stopWordChooser.setCurrentDirectory(workingDirectory);

            if (stopWordChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                selectedStopWord = stopWordChooser.getSelectedFile();
                Path file = selectedStopWord.toPath();
                InputStream in = null;
                try {
                    in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                stopWordReader = new BufferedReader(new InputStreamReader(in));
                String[] fileArray = file.toString().split("\\\\");
                String fileName = fileArray[fileArray.length - 1];
                stopWordTF.setText(fileName);
            }
        });

        analyzeButton.addActionListener((ActionEvent ae) -> {
            String record = "";
            int line = 0;
            try {
                while (stopWordReader.ready()) {
                    record = stopWordReader.readLine();
                    stopWordSet.add(record);
                    line++;
                }
                stopWordReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            record = "";
            line = 0;
            try {
                while (textFileReader.ready()) {
                    record = textFileReader.readLine();
                    String[] tempArray = record.split(" ");
                    for (String s : tempArray) {
                        s = s.replaceAll("[^a-zA-Z]", "");
                        s = s.toLowerCase();
                        if (!stopWordSet.contains(s)) {
                            if (textFileMap.containsKey(s)) {
                                textFileMap.put(s, Integer.parseInt(textFileMap.get(s).toString()) + 1);
                            } else {
                                textFileMap.put(s, 1);
                            }
                        }
                    }
                    line++;
                }
                textFileReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            // System.out.println(Arrays.asList(textFileMap));
            tempMap = new TreeMap<>(textFileMap);
            for (Object key : tempMap.keySet()) {
                String keyText = key.toString();
                String value = tempMap.get(key).toString();
                output.append(keyText + "\t" + value + "\n");
            }
        });

        controlPanel.add(textFileButton);
        controlPanel.add(textFileTF);
        controlPanel.add(stopWordButton);
        controlPanel.add(stopWordTF);
        controlPanel.add(analyzeButton);

    }

}
