package networking;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * April 11,2021
 */
public interface OnRequestCallback {
    byte[] handleRequest(byte[] requestPayLoad);
    
    String getEndpoint();
    
}
