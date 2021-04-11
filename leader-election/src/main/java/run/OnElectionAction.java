package run;

import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * 5:02 PM ,March 15,2021
 */
public class OnElectionAction implements OnElectionCallBack {
    private final ServerRegistry serverRegistry;
    private final int port;
    
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
        try {
            String currentAddress = String.format("http://%s:%d", InetAddress.getLocalHost().getCanonicalHostName(),port);
            serverRegistry.registryToCluster(currentAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
