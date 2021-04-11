package search;

import model.DocumentData;

import java.util.*;

/**
 * @author:Nguyen Anh Tuan
 *     <p>12:19 AM ,April 07,2021
 */
public class TFIDF {

  public static double calculateTermFrequency(List<String> words, String term) {
    long count = 0;
    for (String word : words) {
      if (word.equalsIgnoreCase(term)) {
        count++;
      }
    }
    return (double) count / words.size();
  }

  public static DocumentData createDocumentData(List<String> words, List<String> terms) {
    DocumentData documentData = new DocumentData();
    for (String term : terms) {
      double termFreq = calculateTermFrequency(words, term);
      documentData.putTermFrequency(term, termFreq);
    }
    return documentData;
  }

  public static double getInverseDocumentFrequency(
      String term, Map<String, DocumentData> documentResult) {
    double nt = 0;
    for (String document : documentResult.keySet()) {
      DocumentData documentData = documentResult.get(document);
      double termFreq = documentData.getFrequency(term);
      if (termFreq > 0.0) {
        nt++;
      }
    }
    return nt == 0 ? 0 : Math.log10(documentResult.size() / nt);
  }

  public static Map<String, Double> getTermToInverseDocumentFrequencyMap(
      List<String> terms, Map<String, DocumentData> documentResult) {
    Map<String, Double> termToIDF = new HashMap<String, Double>();
    for (String term : terms) {
      double idf = getInverseDocumentFrequency(term, documentResult);
      termToIDF.put(term, idf);
    }
    return termToIDF;
  }

  private static double calculateDocumentScore(
      List<String> terms,
      DocumentData documentData,
      Map<String, Double> termToInverseDocumentFrequency) {
    double score = 0;
    for (String term : terms) {
      double termFrequency = documentData.getFrequency(term);
      double inverseTermFrequency = termToInverseDocumentFrequency.get(term);
      score += termFrequency * inverseTermFrequency;
    }
    return score;
  }

  public static Map<Double, List<String>> getDocumentSortedByScore(
      List<String> terms, Map<String, DocumentData> documentResult) {
    TreeMap<Double, List<String>> scoreToDocument = new TreeMap<Double, List<String>>();
    Map<String, Double> termToInverseDocumentFrequency =
        getTermToInverseDocumentFrequencyMap(terms, documentResult);
    for (String doucment : documentResult.keySet()) {
      DocumentData documentData = documentResult.get(doucment);
      double score = calculateDocumentScore(terms, documentData, termToInverseDocumentFrequency);
      addDocumentScoreToTreeMap(scoreToDocument, score, doucment);
    }
    return scoreToDocument.descendingMap();
  }

  private static void addDocumentScoreToTreeMap(
          TreeMap<Double, List<String>> scoreToDocument, double score, String doucment) {
    List<String> documentWithCurrentScore = scoreToDocument.get(score);
    if (documentWithCurrentScore == null) {
      documentWithCurrentScore = new ArrayList<String>();
    }
    documentWithCurrentScore.add(doucment);
    scoreToDocument.put(score, documentWithCurrentScore);
  }
  
  public static List<String> getWordsFromLine(String line){
      return Arrays.asList(line.split("(\\.)+|(,)+|( )+|(-)+|(\\?)+|(!)+|(;)+|(:)+|(/d)+|(/n)+"));
  }
  
  public static List<String> getWordsFromLines(List<String> lines){
      List<String> words = new ArrayList<String>();
      for (String line :lines){
          words.addAll(getWordsFromLine(line));
      }
      return words;
  }
}
