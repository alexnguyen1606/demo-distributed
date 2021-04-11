package cluster.management;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * March 15,2021
 */
public interface OnElectionCallback {
    void onElectedToBeLeader();
    void onWorker();
    
}
