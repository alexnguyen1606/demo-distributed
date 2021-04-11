import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * 11:21 PM ,April 06,2021
 */
public class Test2 {
  public static void main(String[] args) {
    Multimap<String,String> multimap = ArrayListMultimap.create();
    multimap.put("Fruite","Banana");
    multimap.put("Fruite","Apple");
    multimap.put("Meat","Steak");
    multimap.put("Meat","Ribs");
    Map<String,Integer> prices = new HashMap<>();
    prices.put("banana",1000);
    prices.put("apple",900);
    prices.put("water_melon",null);
    CacheLoader<String, Integer> cacheLoader =
        new CacheLoader<String, Integer>() {
          @Override
          public Integer load(String integer) throws Exception {
            System.out.println("Check :"+integer);
            return prices.get(integer);
          }
        };
    LoadingCache<String,Integer> loadingCache = CacheBuilder.newBuilder().build(cacheLoader);
    for (int i = 0;i<10;i++){
      if (i==5){
        prices.put("banana",900);
        loadingCache.refresh("banana");
      }
      System.out.println(loadingCache.getUnchecked("banana"));
    }
   
  }
}
