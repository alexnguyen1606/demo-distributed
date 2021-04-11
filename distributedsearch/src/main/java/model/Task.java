package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * 11:34 PM ,April 11,2021
 */
public class Task implements Serializable {
    private final List<String> searchTerms ;
    private final List<String> documents;
    
    public Task(List<String> searchTerms, List<String> documents) {
        this.searchTerms = searchTerms;
        this.documents = documents;
    }
    public List<String> getSearchTerms(){
        return Collections.unmodifiableList(searchTerms);
    }
    
    public List<String> getDocuments() {
        return Collections.unmodifiableList(documents);
    }
}
