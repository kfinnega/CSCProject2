import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Kmedoids implements Serializable {

    public int k;
    public String[] medoidsFileName;
    public Business[] medoidsBusiness;
    public ExtensibleHashTable ht;


    public Kmedoids(ExtensibleHashTable ht) throws IOException, ClassNotFoundException {
        this.k = 7;
        this.ht = ht;
        Business business;
        this.medoidsFileName = new String[k];
        this.medoidsBusiness = new Business[k];

        medoidsFileName[0] = "data/restaurants/b_jg4hexYmhJoo8c4P6MoA.bin";
        medoidsFileName[1] = "data/restaurants/vAAlzuCZ4v-h4e2sQsR-fA.bin";
        medoidsFileName[2] = "data/restaurants/RY8I-VP7g-BMfGOSMA2lgg.bin";
        medoidsFileName[3] = "data/restaurants/X_P7ZO_ktlrL-5a9AOr_SQ.bin";
        medoidsFileName[4] = "data/restaurants/AxcyIxsjJbRNG2RFTguavw.bin";
        medoidsFileName[5] = "data/restaurants/pO26EJWkGf1J_QkI-B7Eeg.bin";
        medoidsFileName[6] = "data/restaurants/gD2sF9BKMP428y1NJEiPbQ.bin";

        for (int i = 0; i < k; i++) {
            business = Business.loadFromFile(medoidsFileName[i]);
            medoidsBusiness[i] = business;
        }
        this.createClustersFiles();
        this.fillClusters();
    }

    public void createClustersFiles() throws IOException {
        for (int i = 0; i < k; i++) {
            String path = "data/clusters/";
            int clustNum = i + 1;
            path = path + "cluster" + clustNum + ".txt";
            Files.createFile(Paths.get(path));
            saveFilePathToCluster(medoidsFileName[i], path);
        }
    }

    public static void saveFilePathToCluster(String businessFile, String clusterFile ) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(clusterFile))) {
            writer.write(businessFile);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing cluster file: " + e.getMessage());
        }
    }

    public void printMedoidsArr() {
        for(String file : medoidsFileName) {
            System.out.println(file);
        }
    }

    public void fillClusters() throws IOException, ClassNotFoundException {
        ht.createClusters(medoidsBusiness);
    }

    public String getCluster(Business business) {
        CosSimCalc cosSim = new CosSimCalc();
        String path = "data/clusters/";
        Business medoid;
        int bestFitMedoid = 0;
        double bestFitvalue = 0.0;
        double cosSimVal = 0.0;

        for (int i = 0; i < medoidsBusiness.length; i++) {
            medoid = medoidsBusiness[i];
            cosSimVal = cosSim.compareBusiness(medoid, business);

            if (cosSimVal >= bestFitvalue) {
                bestFitvalue = cosSimVal;
                bestFitMedoid = i + 1;
            }
        }
        return path + "cluster" + bestFitMedoid + ".txt";
    }
}
