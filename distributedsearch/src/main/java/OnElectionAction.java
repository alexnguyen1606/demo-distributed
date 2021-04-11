import cluster.management.OnElectionCallBack;
import cluster.management.ServerRegistry;
import networking.WebServer;
import search.SearchWorker;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * 11:54 PM ,April 11,2021
 */
public class OnElectionAction implements OnElectionCallBack {
    private final ServerRegistry serverRegistry;
    private final int port;
    private WebServer webServer;
    
    public OnElectionAction(ServerRegistry serverRegistry, int port) {
        this.serverRegistry = serverRegistry;
        this.port = port;
    }
    
    
    @Override
    public void onElecterBeLeader() {
        serverRegistry.unregistryFromCluster();
        serverRegistry.registerForUpdate();
        
    }
    
    @Override
    public void onWorker() {
        SearchWorker searchWorker = new SearchWorker();
        webServer = new WebServer(port,searchWorker);
    }
}
