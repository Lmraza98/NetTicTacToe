import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by lucasraza on 12/7/18.
 */
public class Server extends ServerNetworkConnection{

    private String ip;
    private int port;

    public Server(String ip, int port, Consumer<Serializable> onReceiveCallback){
        super(onReceiveCallback);
        this.ip = ip;
        this.port = port;
    }


    @Override
    protected String getIP(){
        return null;
    }

    @Override
    protected int getPort(){
        return 0;
    }



}
