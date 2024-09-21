import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Application {
    private JFrame frame;
    private JPanel mainPanel;
    private JLabel businessIdLabel;
    private JTextField businessIdField;
    private JButton searchButton;
    private JTextArea resultsArea;

    public Application() {
        frame = new JFrame("Business Cluster");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        businessIdLabel = new JLabel("Enter Business Name:");
        searchPanel.add(businessIdLabel);

        businessIdField = new JTextField(20);
        searchPanel.add(businessIdField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    displayClusterAndMostSimilar();
                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        searchPanel.add(searchButton);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void displayClusterAndMostSimilar() throws IOException, ClassNotFoundException {
        ExtensibleHashTable ht = ExtensibleHashTable.loadFromFile();
        String businessName = businessIdField.getText();
        CosSimCalc cosSim = new CosSimCalc();

        if (businessName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a Business ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String businessFilePath = ht.get(businessName);
        Business inputBusiness = Business.loadFromFile(businessFilePath);
        inputBusiness.simScore = 0.0;
        Business comparee;
        String clusterFile = ht.kmedoids.getCluster(inputBusiness);
        String printedCluster = "";

        BufferedReader objReader = new BufferedReader(new FileReader(clusterFile));
        String strCurrentLine = objReader.readLine();

        while ((strCurrentLine = objReader.readLine()) != null) {
            String buisnessFile = ht.get(strCurrentLine);
            if (buisnessFile == null) continue;
            comparee = Business.loadFromFile(buisnessFile);
            if (inputBusiness.name.equals(comparee.name)) continue;
            double value = cosSim.compareBusiness(inputBusiness,comparee);
            clusterFile = clusterFile.concat(strCurrentLine +"\n");

            if (value > inputBusiness.simScore) {
                inputBusiness.simScore = value;
                inputBusiness.mostSim = comparee.name;
            }
        }

        objReader.close();

        String mostSimilar = inputBusiness.mostSim;

        resultsArea.setText("Most similar business: " + mostSimilar +"\n Cluster: " + clusterFile + "\n" + printedCluster);
    }


    private String[] findClusterAndMostSimilar(Business business) throws IOException, ClassNotFoundException {
        ExtensibleHashTable ht = ExtensibleHashTable.loadFromFile();
        String[] result = new String[2];

        String mostSimilarBusiness = business.mostSim;
        String clusterFile = ht.kmedoids.getCluster(business);
        result[0] = clusterFile;
        result[1] = mostSimilarBusiness;

        return result;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Application();
            }
        });
    }
}



//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//
//        float start = System.nanoTime();
//        Business business;
//
//
//        Application app = new Application();
//
//        float end = System.nanoTime();
//        float totalTime = end - start;
//
//        System.out.println("SUCCESS \nTOTAL TIME WAS: " + totalTime/1000000000 + " seconds");
//    }
//}
