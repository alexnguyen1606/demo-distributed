package model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * 12:22 AM ,April 07,2021
 */
public class DocumentData {
    private Map<String,Double> termToFrequency = new HashMap<String, Double>();
    
    // Tần suất xuất hiện của term in document
    public void putTermFrequency(String term,Double frequency){
        termToFrequency.put(term,frequency);
    }
    
    //Get Frequency of Term
    public Double getFrequency(String term){
        return termToFrequency.get(term);
    }
}
