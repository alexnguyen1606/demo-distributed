import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * 12:12 AM ,April 02,2021
 */
public class Test {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
      Long before = System.currentTimeMillis();
      AtomicInteger num = new AtomicInteger(100);
      int cores = Runtime.getRuntime().availableProcessors();
      ExecutorService executorService = Executors.newFixedThreadPool(cores);
    List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
    for (int i =0;i<=3;i++){
        completableFutures.add(getExcute(i));
    }
    CompletableFuture<Void> completableFuture = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
    completableFuture.join();
    Long after = System.currentTimeMillis();
    System.out.println(after-before);
    System.out.println(cores);
    executorService.shutdownNow();
  }
  public static CompletableFuture<Void> getExcute(int i){
    return CompletableFuture.runAsync(
        () -> {
          try {
            System.out.println(i);
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        });
  }
    
    public static CompletableFuture<Integer> getExcute2(int i){
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        System.out.println(i);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });
    }
}
