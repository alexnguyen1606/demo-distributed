package cluster.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author:Nguyen Anh Tuan
 *     <p>4:24 PM ,March 15,2021
 */
public class ServerRegistry implements Watcher {
  private static final String REGISTRY_NODE = "/service_registry";
  private final ZooKeeper zooKeeper;

  private String currentNode = null;
  private List<String> allServiceAddress = null;

  public ServerRegistry(ZooKeeper zooKeeper) {
    this.zooKeeper = zooKeeper;
  }

  public void registryToCluster(String metadata) throws KeeperException, InterruptedException {
    this.currentNode =
        zooKeeper.create(
            REGISTRY_NODE + "/n_",
            metadata.getBytes(),
            ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL);
    System.out.println("Registrered to service registry");
  }

  public void registerForUpdate()  {
      try {
          updateAddresses();
      } catch (KeeperException e) {
          e.printStackTrace();
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }

  public synchronized void updateAddresses() throws KeeperException, InterruptedException {
    List<String> workerNode = zooKeeper.getChildren(REGISTRY_NODE, this);
    List<String> addresses = new ArrayList<>(workerNode.size());
    for (String workerZNode : workerNode) {
      String fullPathZNode = REGISTRY_NODE + "/" + workerZNode;
      Stat stat = zooKeeper.exists(fullPathZNode, false);
      if (stat == null) {
        return;
      }
      byte[] addressByte = zooKeeper.getData(fullPathZNode, false, stat);
      String address = new String(addressByte);
      addresses.add(address);
    }
    this.allServiceAddress = Collections.unmodifiableList(addresses);
    System.out.println("Current Cluster address" + allServiceAddress);
  }

  public synchronized List<String> getAllServiceAddress()
      throws KeeperException, InterruptedException {
    if (allServiceAddress == null) {
      updateAddresses();
    }
    return allServiceAddress;
  }

  public void unregistryFromCluster() {
    try {
      if (currentNode != null && zooKeeper.exists(currentNode, false) != null) {
        zooKeeper.delete(currentNode, -1);
      }
    } catch (KeeperException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void createServiceRegistryZNode() {
    try {
      if (zooKeeper.exists(REGISTRY_NODE, false) == null) {
        zooKeeper.create(
            REGISTRY_NODE, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
      }
    } catch (KeeperException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void process(WatchedEvent watchedEvent) {
    try {
      updateAddresses();
    } catch (KeeperException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
