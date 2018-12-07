
/**
 * Created by lucasraza on 12/7/18.
 */


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import java.io.ObjectInputStream;

public abstract class ClientNetworkConnection {

    private ConnectionThread connThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallback;

    public ClientNetworkConnection(Consumer<Serializable> onReceiveCallback){
        this.onReceiveCallback = onReceiveCallback;
        connThread.setDaemon(true);

    }
    public void startConnection() throws Exception {
        connThread.start();

    }
    public void send(Serializable data) throws Exception{
        connThread.out.writeObject(data);

    }
    public void closeConnection() throws Exception{
        connThread.socket.close();

    }
    protected abstract String getIP();
    protected abstract int getPort();

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run(){

        }


    }


}
