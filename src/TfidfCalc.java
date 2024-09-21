import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

public class TfidfCalc {
    public static HashMap<String, Business> businessMap;
    public static HashMap<String, String> businessNameMap;

    public TfidfCalc() throws IOException {
        businessNameMap = new HashMap<String, String>();
        businessMap = new HashMap<String, Business>();
        FT documentFrequencyTable = new FT();
        addBusinessDataToBusinessMap();
        addReviewDataToBusinessMap();
        removeProblemBizs();

        for (Map.Entry<String, Business> entry : businessMap.entrySet()) {
            Business biz = entry.getValue();
            biz.termFrequencyTable = fillTFTables(biz);
            getTFValue(biz.termFrequencyTable);
        }

        documentFrequencyTable = fillDocFreqTable(businessMap, documentFrequencyTable);
        getIDFValue(documentFrequencyTable, businessMap.size());
        int count = 0;
        for (Map.Entry<String, Business> entry : businessMap.entrySet()) {
            Business biz = entry.getValue();
            createTfidfValue(biz, documentFrequencyTable);
        }
    }


    public static void addBusinessDataToBusinessMap() throws IOException {
        int count = 0;
        Gson gson = new GsonBuilder().setLenient().create();
        BufferedReader reader = new BufferedReader(new FileReader("data/yelp_academic_dataset_business.json"));
        String currentLine;
        Business business;

        while (count < 10000) {
            currentLine = reader.readLine();
            business = gson.fromJson(currentLine, Business.class);
            String categories = business.categories;

            if (categories != null && categories.contains("Restaurants") && !(businessNameMap.containsKey(business.getName()) && business.review_count != 0)){
                businessNameMap.put(business.getName(), business.getBusiness_id());
                businessMap.put(business.getBusiness_id(), business);
                count++;
            }
        }

        reader.close();
    }

    public static void removeProblemBizs() {
        String[] bizIds = {"k3EItHl2rtEz4opr39b99g", "QXlQzuc_LIfln_lG0Nm4PA", "o1I-pKoa6WUNpMViWaA9Fw","BuEEKAmr1JDfhwG8Q0ODoA", "cU0_JyREJ-3vwaNxRdmmig", "AFU5ZlFCYA0_Eqw5MlqJOg", "VSQjT5mn45JKtSSL56jNPg", "RE9UD8J2t650OqDtqxPfEw", "JL6U8xzmWZJgVTZy6S4rsQ", "AqOSpSqdocwtIjqTECW2ng", "2VzbT8h9cct4UZdYMQlgrA", "HnESQwAjd3nZXkb9A_ixMg"};
        for (String id : bizIds) {
            businessMap.remove(id);
        }
    }

    public static void addReviewDataToBusinessMap() throws IOException {
        int count = 0;
        Gson gson = new GsonBuilder().setLenient().create();
        String currentLine;
        Business business;

        BufferedReader reader = new BufferedReader(new FileReader("data/yelp_academic_dataset_review.json"));

        while (count < 999018) {
            currentLine = reader.readLine();
            Review review = gson.fromJson(currentLine, Review.class);

            if (businessMap.containsKey(review.getBusiness_id())) {
                business = businessMap.get(review.getBusiness_id());
                business.addReview(review);
                count++;
            }
        }

        reader.close();

    }

    public static FT fillTFTables(Business biz) {
        FT tf = new FT();
        for (Review review : biz.reviews) {
            String[] splitter = review.getText().split(" ");
            for (String word : splitter) {
                tf.add(word);
            }
        }
        return tf;
    }



    public static FT fillDocFreqTable(HashMap<String, Business> bizs, FT docFT) {
        HashSet<String> checker = new HashSet<>();

        for (Map.Entry<String, Business> entry : bizs.entrySet()) {
            Business biz = entry.getValue();
            checker.clear(); // Clear the checker set for each business

            for (Review review : biz.reviews) {
                String preprocessedText = preprocessText(review.getText());
                StringTokenizer tokenizer = new StringTokenizer(preprocessedText);

                while (tokenizer.hasMoreTokens()) {
                    String word = tokenizer.nextToken();
                    if (!checker.contains(word)) {
                        checker.add(word);
                        docFT.add(word);
                    }
                }
            }
        }
        return docFT;
    }


    public static String preprocessText(String text) {
        String lowerCaseText = text.toLowerCase();
        String noPunctuationText = lowerCaseText.replaceAll("[^a-z0-9 ]", "");
        return noPunctuationText;
    }



    public static void getTFValue(FT ft) {
        FT.Node[] table = ft.table;
        for (int i = 0; i < table.length; ++i) {
            for (FT.Node e = table[i]; e != null; e = e.next) {
                e.value = e.count / ft.totalCount;
            }
        }
    }

    public static void getIDFValue(FT ft, int numOfDocs) {
        FT.Node[] table = ft.table;
        for(int i = 0; i < table.length; ++i){
            for(FT.Node e = table[i]; e != null; e = e.next) {
                e.value = Math.log10(numOfDocs/e.count);
            }
        }
    }

    public static void createTfidfValue(Business biz, FT idfvals) {
        HashMap<String, Double> tfidf = new HashMap<>();
        FT.Node[] table = biz.termFrequencyTable.table;
        for (int i = 0; i < table.length; i++) {
            for (FT.Node e = table[i]; e != null; e = e.next) {
                double idf = idfvals.getValue(e.key);
                double tfidfVal = e.value * idf;

                if (tfidfVal != 0) {
                    tfidf.put(e.key, tfidfVal);
                }
            }
        }
        biz.tfidfValues = tfidf;
    }
}
