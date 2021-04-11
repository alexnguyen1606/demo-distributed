package networking;

import com.sun.deploy.net.HttpRequest;
import com.sun.deploy.net.HttpResponse;
import sun.net.www.http.HttpClient;

import java.net.URI;

import java.util.concurrent.CompletableFuture;

/**
 * @author:Nguyen Anh Tuan
 *     <p>1:38 AM ,March 31,2021
 */
public class WebClient {
  private HttpClient httpClient;

  public WebClient() {
    this.httpClient =
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
  }

  public CompletableFuture<String> sendTask(String url, byte[] payload) {
    HttpRequest request =
        HttpRequest.registerNatives()
            .POST(HttpRequest.BodyPublishers.ofByteArray(payload))
            .uri(URI.create(url))
            .build();
    return httpClient
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body);
  }
}
