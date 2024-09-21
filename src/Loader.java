import java.io.IOException;
import java.util.Map;

public class Loader {
    static ExtensibleHashTable ht;

    static {
        try {
            ht = new ExtensibleHashTable("data/buckets");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Loader() throws IOException, ClassNotFoundException {
    }

    public static void createClusters(ExtensibleHashTable ht) throws IOException, ClassNotFoundException {
        ht.kmedoids = new Kmedoids(ht);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        float start = System.nanoTime();
        TfidfCalc tfidfCalc = new TfidfCalc();
        Business business;
        String directory = "data/restaurants";

        for (Map.Entry<String, Business> entry : tfidfCalc.businessMap.entrySet()) {
            business = entry.getValue();
            business.saveToFile(directory);
            ht.add(business.name, directory + "/" + business.business_id + ".bin"); // adds the business file to EHT
        }
        createClusters(ht);
        ht.saveToFile();

        float end = System.nanoTime();
        float totalTime = end - start;
        System.out.println("SUCCESS \nTOTAL TIME WAS: " + totalTime/1000000000 + " seconds");
    }
}
