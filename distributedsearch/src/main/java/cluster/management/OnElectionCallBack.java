package cluster.management;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * March 15,2021
 */
public interface OnElectionCallBack {
    void onElecterBeLeader();
    void onWorker();
    
}
