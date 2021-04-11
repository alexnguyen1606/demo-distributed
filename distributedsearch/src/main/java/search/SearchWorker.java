package search;

import model.DocumentData;
import model.Result;
import model.Task;
import networking.OnRequestCallback;
import utils.SerializationUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:Nguyen Anh Tuan
 *     <p>11:39 PM ,April 11,2021
 */
public class SearchWorker implements OnRequestCallback {

  private static final String ENDPOINT = "/task";

  @Override
  public byte[] handleRequest(byte[] requestPayLoad) {
    Task task = (Task) SerializationUtils.deserialize(requestPayLoad);
    Result result = createResult(task);
    return SerializationUtils.serialize(result);
  }

  private Result createResult(Task task) {
    List<String> documents = task.getDocuments();
    System.out.println(String.format("Received %d documents to process", documents));
    Result result = new Result();
    for (String document : documents) {
      List<String> words = parseWordFromDocument(document);
      DocumentData documentData = TFIDF.createDocumentData(words, task.getSearchTerms());
      result.addDocumentData(document,documentData);
    }
    return result;
  }

  private List<String> parseWordFromDocument(String document) {
      FileReader fileReader = null;
      try {
          fileReader = new FileReader(document);
      } catch (FileNotFoundException e) {
         return Collections.EMPTY_LIST;
      }
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      List<String> lines = bufferedReader.lines().collect(Collectors.toList());
      return TFIDF.getWordsFromLines(lines);
  }

  @Override
  public String getEndpoint() {
    return ENDPOINT;
  }
}
