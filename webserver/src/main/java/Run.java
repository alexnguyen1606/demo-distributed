/**
 * @author:Nguyen Anh Tuan
 * <p>
 * 12:07 AM ,March 30,2021
 */
public class Run {
    public static void main(String[] args) {
        int port = 9000;
        if (args.length==1){
            port = Integer.parseInt(args[0]);
        }
        WebServer webServer = new WebServer(port);
        webServer.startServer();
    }
}
