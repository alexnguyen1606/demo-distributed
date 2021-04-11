package utils;

import java.io.*;

/**
 * @author:Nguyen Anh Tuan
 * <p>
 * 11:29 PM ,April 11,2021
 */
public class SerializationUtils {
    public static byte[] serialize(Object object){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = null;
        try {
            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject(object);
            objectOutput.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }
    
    public static Object deserialize(byte[] data){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try {
            ObjectInput objectInput = new ObjectInputStream(byteArrayInputStream);
            return objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
