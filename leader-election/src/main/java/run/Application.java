package run;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * @author:Nguyen Anh Tuan
 *     <p>5:13 PM ,March 15,2021
 */
public class Application implements Watcher {
  private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
  private static final String ELECTION_NAMESPACE = "/election";
  private static final String TARGET_ZNODE = "/target_znode";
  private static final int SESSION_TIMEOUT = 3000;
  private static final int DEFAULT_PORT = 8080;
    private static final Logger logger = LogManager.getLogger(Application.class);
  private ZooKeeper zooKeeper;

  public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
    Application application = new Application();
    ZooKeeper zooKeeper = application.connectToZookeeper();
    int currentPort = args.length == 1 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
    ServerRegistry serverRegistry = new ServerRegistry(zooKeeper);
    OnElectionCallBack onElectionCallBack = new OnElectionAction(serverRegistry, currentPort);
    LeaderElection leaderElection = new LeaderElection(zooKeeper, onElectionCallBack);
    leaderElection.volunteerForLeaderShip();
    leaderElection.relectiontLeader();
    application.run();
    application.close();
  }

  public ZooKeeper connectToZookeeper() throws IOException {
    zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    return zooKeeper;
  }

  public void run() throws InterruptedException {
    synchronized (zooKeeper) {
      zooKeeper.wait();
    }
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
//              try {
////                  relectiontLeader();
//              } catch (KeeperException e) {
//                  e.printStackTrace();
//              } catch (InterruptedException e) {
//                  e.printStackTrace();
//              }
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
          watchTargetZNode();
      } catch (KeeperException e) {
          e.printStackTrace();
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }

  public void watchTargetZNode() throws KeeperException, InterruptedException {
    Stat stat = zooKeeper.exists(TARGET_ZNODE, this);
    if (stat == null) {
      return;
    }
    byte[] data = zooKeeper.getData(TARGET_ZNODE, this, stat);
    List<String> chidren = zooKeeper.getChildren(TARGET_ZNODE, this);
    System.out.println("data:" + data + " childern" + chidren);
  }
}
