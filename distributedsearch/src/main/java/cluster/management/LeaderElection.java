package cluster.management;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author:Nguyen Anh Tuan
 *     <p>3:31 PM ,March 14,2021
 */
public class LeaderElection implements Watcher {
  private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
  private static final String ELECTION_NAMESPACE = "/election";
  private static final String TARGET_ZNODE = "/target_znode";
  private static final int SESSION_TIMEOUT = 3000;
  private static final int DEFAULT_PORT = 8080;
  private ZooKeeper zooKeeper;
  private String currentZNode;
  private final OnElectionCallBack onElectionCallBack;
  private static final Logger logger = LogManager.getLogger(LeaderElection.class);

  public LeaderElection(ZooKeeper zooKeeper, OnElectionCallBack onElectionCallBack) {
    this.zooKeeper = zooKeeper;
    this.onElectionCallBack = onElectionCallBack;
  }

  public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
//      int currentPort = args.length == 1 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
//      LeaderElection leaderElection = new LeaderElection();
//      leaderElection.connectToZookeeper();
//      leaderElection.volunteerForLeaderShip();
//      leaderElection.relectiontLeader();
//      //      leaderElection.watchTagretZNode();
//      leaderElection.run();
//      leaderElection.close();
//      System.out.println("Dissconnect");
   
  }

  public void connectToZookeeper() throws IOException {
    this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
  }

  public void run() throws InterruptedException {
    synchronized (zooKeeper) {
      zooKeeper.wait();
    }
  }

  public void volunteerForLeaderShip() throws KeeperException, InterruptedException {
    String znodePrefix = ELECTION_NAMESPACE + "/c_";
    String zNoddeFullPateh =
        zooKeeper.create(
            znodePrefix,
            new byte[] {},
            ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL);
    currentZNode = zNoddeFullPateh.replace(ELECTION_NAMESPACE + "/", "");
    System.out.println("znode:" + zNoddeFullPateh);
  }

  public void relectiontLeader() throws KeeperException, InterruptedException {
    Stat predecessorStat = null;
    String predecessorZnode = "";
    while (predecessorStat == null) {
      List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, false);
      Collections.sort(children);
      String smallestChild = children.get(0);
      if (smallestChild.equals(currentZNode)) {
        System.out.println("Im leader");
        onElectionCallBack.onElecterBeLeader();
        return;
      } else {
        System.out.println("I not leader," + smallestChild + " is leader");
        int preprocessorIndex = Collections.binarySearch(children, currentZNode) - 1;
        predecessorZnode = children.get(preprocessorIndex);
        predecessorStat = zooKeeper.exists(ELECTION_NAMESPACE + "/" + predecessorZnode, this);
      }
    }
    onElectionCallBack.onWorker();
    System.out.println("Watching node" + predecessorZnode);
  }

  public void close() throws InterruptedException {
    zooKeeper.close();
  }

  @Override
  public void process(WatchedEvent watchedEvent) {
    switch (watchedEvent.getType()) {
      case None:
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
          logger.info("Success to zookeeper");
        } else {
          synchronized (zooKeeper) {
            System.out.println("dissconection event");
            zooKeeper.notifyAll();
          }
        }
        break;
      case NodeDeleted:
        System.out.println(TARGET_ZNODE + " was deleted");
        try {
          relectiontLeader();
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        break;
      case NodeCreated:
        System.out.println(TARGET_ZNODE + " was created");
        break;
      case NodeDataChanged:
        System.out.println(TARGET_ZNODE + " data change");
        break;
      case NodeChildrenChanged:
        System.out.println(TARGET_ZNODE + " child change");
        break;
    }
    try {
      watchTagretZNode();
    } catch (KeeperException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void watchTagretZNode() throws KeeperException, InterruptedException {
    Stat stat = zooKeeper.exists(TARGET_ZNODE, this);
    if (stat == null) {
      return;
    }
    byte[] data = zooKeeper.getData(TARGET_ZNODE, this, stat);
    List<String> chidren = zooKeeper.getChildren(TARGET_ZNODE, this);
    System.out.println("data:" + data + " childern" + chidren);
  }
}
