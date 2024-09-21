import java.io.Serializable;

public class Review implements Serializable {

    private final String business_id;
    private final String text;
    public FT termFreqTable;


    public Review(String business_id, String text) {
        this.business_id = business_id;
        this.text = text;

    }

    public FT getFrequencyTable() {
        return termFreqTable;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public String getText() {
        return text;
    }
}