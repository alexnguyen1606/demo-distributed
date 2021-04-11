import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * @author:Nguyen Anh Tuan
 *     <p>12:06 AM ,March 30,2021
 */
public class WebServer {

  private static final String TASK_ENDPOINT = "/task";
  private static final String STATUS_ENDPOINT = "/status";

  private final int port;
  private HttpServer httpServer;

  public WebServer(int port) {
    this.port = port;
  }

  public void startServer() {
    try {
      this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return;
    }

    HttpContext taskContext = httpServer.createContext(TASK_ENDPOINT);
    HttpContext statusContext = httpServer.createContext(STATUS_ENDPOINT);
    statusContext.setHandler(this::handleStatusCheckRequest);
    taskContext.setHandler(this::handleTaskRequest);
    httpServer.setExecutor(Executors.newFixedThreadPool(3));
    httpServer.start();
    System.out.println("Server started at port "+port);
  }

  private void handleStatusCheckRequest(HttpExchange httpExchange) throws IOException {
    if (!httpExchange.getRequestMethod().equalsIgnoreCase("get")) {
      httpExchange.close();
      return;
    }
    String message = "Service still alive";
    sendResponse(httpExchange, message.getBytes());
  }

  private void handleTaskRequest(HttpExchange httpExchange) throws IOException {
    if (!httpExchange.getRequestMethod().equalsIgnoreCase("post")) {
      httpExchange.close();
      return;
    }
    Headers headers = httpExchange.getRequestHeaders();
    if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) {
      String dumpResponse = "1232";
      sendResponse(httpExchange, dumpResponse.getBytes());
      return;
    }
    boolean isDebug = false;
    if (headers.containsKey("X-debug") && headers.get("X-debug").get(0).equalsIgnoreCase("true")) {
      isDebug = true;
    }
    long startTime = System.nanoTime();
        byte[] requestBytes = new byte[1000];
    httpExchange.getRequestBody().read(requestBytes);
    byte[] responseBytes = calculateResponse(requestBytes);
    long finish = System.nanoTime();
    if (isDebug){
      String debugMessage = String.format("Operation %d",finish-startTime);
      httpExchange.getResponseHeaders().put("X-Debug-info", Arrays.asList(debugMessage));
    }
    sendResponse(httpExchange,responseBytes);
  }
  
  private byte[] calculateResponse(byte[] requestBytes) {
    String body = new String(requestBytes);
    String[] split = body.split(",");
    BigInteger result = BigInteger.ONE;
    for (String number : split){
      BigInteger bigInteger = new BigInteger(number);
      result=result.multiply(bigInteger);
    }
    return String.format("Result %d",result).getBytes();
  }
  
  private void sendResponse(HttpExchange httpExchange, byte[] data) throws IOException {
    httpExchange.sendResponseHeaders(200, data.length);
    OutputStream outputStream = httpExchange.getResponseBody();
    outputStream.write(data);
    outputStream.flush();
    outputStream.close();
  }
}
