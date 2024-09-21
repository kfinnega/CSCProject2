import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClusterViewer {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Cluster Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        try {
            displayCluster("data/clusters/cluster4.txt", textArea);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading cluster: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        frame.setVisible(true);
    }

    private static void displayCluster(String clusterFile, JTextArea textArea) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(clusterFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Here, you should deserialize the Business object and display its relevant information
                Business business = Business.loadFromFile(line);
                textArea.append(business.name + "\n");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void testMostSim(Business input, Business inClust) {
        if (input == inClust) return;

        CosSimCalc cosSim = new CosSimCalc();
        double simScore = cosSim.compareBusiness(input,inClust);

        if (input.mostSim == null) {
            input.mostSim = inClust.name;
            input.simScore = simScore;
            return;
        }

        if(input.simScore <= simScore) {
            input.mostSim = inClust.name;
            input.simScore = simScore;
        }
    }

    private static String findMostSim(Business input, String clusterFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(clusterFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Here, you should deserialize the Business object and display its relevant information
                Business business = Business.loadFromFile(line);
                testMostSim(input, business);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return input.mostSim;
    }

}

