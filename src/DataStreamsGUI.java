import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsGUI extends JFrame
{
    private JTextArea originalArea;
    private JTextArea filteredArea;

    private JTextField searchField;

    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;

    private Path selectedFile;

    public DataStreamsGUI()
    {
        setTitle("Java Data Streams Search");
        setSize(900, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildGUI();

        searchButton.setEnabled(false);

        setVisible(true);
    }

    private void buildGUI()
    {
        searchField = new JTextField(20);

        originalArea = new JTextArea();
        filteredArea = new JTextArea();

        originalArea.setEditable(false);
        filteredArea.setEditable(false);

        JScrollPane originalScroll =
                new JScrollPane(originalArea);

        JScrollPane filteredScroll =
                new JScrollPane(filteredArea);

        JSplitPane splitPane =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        originalScroll,
                        filteredScroll);

        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        JPanel topPanel = new JPanel();

        topPanel.add(new JLabel("Search String:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        JPanel bottomPanel = new JPanel();

        bottomPanel.add(loadButton);
        bottomPanel.add(quitButton);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadFile());

        searchButton.addActionListener(e -> searchFile());

        quitButton.addActionListener(e -> System.exit(0));
    }

    private void loadFile()
    {
        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if(result == JFileChooser.APPROVE_OPTION)
        {
            selectedFile =
                    chooser.getSelectedFile().toPath();

            try
            {
                String content =
                        Files.readString(selectedFile);

                originalArea.setText(content);

                searchButton.setEnabled(true);
            }
            catch(IOException ex)
            {
                JOptionPane.showMessageDialog(
                        this,
                        "Error loading file.");
            }
        }
    }

    private void searchFile()
    {
        if(selectedFile == null)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Please load a file first.");

            return;
        }

        String searchTerm =
                searchField.getText().trim().toLowerCase();

        if(searchTerm.isEmpty())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a search string.");

            return;
        }

        try(Stream<String> lines =
                    Files.lines(selectedFile))
        {
            String results = lines

                    .filter(line ->
                            line.toLowerCase()
                                    .contains(searchTerm))

                    .collect(Collectors.joining("\n"));

            filteredArea.setText(results);
        }
        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Error searching file.");
        }
    }
}