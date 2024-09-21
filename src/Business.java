import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Objects.isNull;

public class Business implements Serializable{
    String business_id;
    String name;
    String city;
    float latitude;
    float longitude;
    int review_count;
    String categories;
    String mostSim;
    double simScore;

    public transient ArrayList<Review> reviews;
    public transient FT termFrequencyTable;
    HashMap<String, Double> tfidfValues;

    public String getBusiness_id(){ return this.business_id; }

    public String getName(){
        return this.name;
    }

    public void addReview(Review review) {
        if (isNull(reviews) || reviews.isEmpty()) {
            reviews = new ArrayList<>();
        }
        reviews.add(review);
    }


    public void saveToFile(String directory) throws IOException {
        String fileName = directory + "/" + business_id + ".bin";
        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
        }
    }

    public static Business loadFromFile(String fileName) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Business) ois.readObject();
        }
    }

    @Override
    public String toString() {
        return "Business{" +
                "business_id='" + business_id + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", review_count=" + review_count +
                ", categories=" + categories +
                '}';
    }
}
