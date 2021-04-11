import model.DocumentData;
import search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author:Nguyen Anh Tuan
 *     <p>11:03 PM ,April 07,2021
 */
public class SequentialSearch {

  public static final String BOOKS_DIRECTORY =
      "E:\\udemy\\DistributedSystem_CloundComputing\\workspace\\tfidf\\resources\\books";

  private static String SEARCH_QUERY_1 = " class";
  private static String SEARCH_QUERY_2 = "";
  private static String SEARCH_QUERY_3 = "";
  private static final LogManager LOGGER = LogManager.getLogManager();
  public static void main(String[] args) throws FileNotFoundException {
    File documentDirectory = new File(BOOKS_DIRECTORY);
    List<String> documents =
        Arrays.stream(Objects.requireNonNull(documentDirectory.list()))
            .map(document -> BOOKS_DIRECTORY + "/" + document)
            .collect(Collectors.toList());
    List<String> terms = TFIDF.getWordsFromLine(SEARCH_QUERY_1);
    findMostRelevantDocuments(documents, terms);
  }

  private static void findMostRelevantDocuments(List<String> documents, List<String> terms)
      throws FileNotFoundException {
    Map<String, DocumentData> documentDataMap = new HashMap<>();
    for (String document : documents) {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(document));
      List<String> lines = bufferedReader.lines().collect(Collectors.toList());
      List<String> words = TFIDF.getWordsFromLines(lines);
      DocumentData documentData = TFIDF.createDocumentData(words, terms);
      documentDataMap.put(document, documentData);
    }
    Map<Double, List<String>> documentByScore =
        TFIDF.getDocumentSortedByScore(terms, documentDataMap);
    printResult(documentByScore);
  }

  private static void printResult(Map<Double, List<String>> documentByScore) {
    for (Map.Entry<Double, List<String>> docScorePair : documentByScore.entrySet()) {
      double score = docScorePair.getKey();
      for (String document : docScorePair.getValue()) {
        String[] list = document.split("/");
        System.out.println(String.format("Book: %s - score : %f  ", list[list.length-1],score));
      }
    }
  }
}
