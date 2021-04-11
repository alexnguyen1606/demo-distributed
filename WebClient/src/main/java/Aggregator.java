import networking.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author:Nguyen Anh Tuan
 *     <p>1:44 AM ,March 31,2021
 */
public class Aggregator {
  private WebClient webClient;

  public Aggregator() {
    this.webClient = new WebClient();
  }

  public List<String> sendTasks(List<String> workerAddress, List<String> tasks) {
    CompletableFuture<String>[] futures = new CompletableFuture[workerAddress.size()];
    for (int i = 0; i < workerAddress.size(); i++) {
      String workerAdd = workerAddress.get(i);
      String task = tasks.get(i);
      byte[] payload = task.getBytes();
      futures[i] = webClient.sendTask(workerAdd, payload);
    }
    List<String> result =
        Stream.of(futures).map(CompletableFuture::join).collect(Collectors.toList());
    return result;
  }
}
