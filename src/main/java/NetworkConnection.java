
/**
 * Created by lucasraza on 12/7/18.
 */


import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import java.io.ObjectInputStream;

public class NetworkConnection {

    private ConnectionThread connThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallback;
    private char mark;
    private String ip;
    private int port;
    private boolean validMove = false;
    private boolean won = false;
    private boolean tie = false;
    private boolean move;
    private String message;
    private int pos;
    private int opponentMove;

    public NetworkConnection(String ip, int port, Consumer<Serializable> onReceiveCallback){
        this.onReceiveCallback = onReceiveCallback;
        connThread.setDaemon(true);
        this.ip = ip;
        this.port = port;

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
    public char getIcon(){
        return mark;
    }
    public void setIcon(char mark){
        this.mark = mark;
    }
    public boolean isValidMove(){
        return validMove;
    }
    public void setMove(boolean move){
        this.move = true;
    }
    public boolean getMove(){
        return true;
    }
    public void setVictory(boolean won){
        this.won = won;
    }
    public boolean victory(){
        return won;
    }
    public void setTie(boolean tie){
        this.tie = tie;
    }
    public boolean getTie(){
        return tie;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
    public void setOpponentMove(int opponentMove){
        this.opponentMove = opponentMove;
    }
    public int getOpponentMove(){
        return this.opponentMove;
    }
    public void setPos(int pos){
        this.pos = pos;
    }
    public int getPos(){
        return pos;
    }

    private String getIP() {
        return ip;
    }

    private int getPort() {
        return port;
    }

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run() {
            try{
                Socket socket = new Socket(getIP(), getPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while (true) {
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallback.accept(data);

                    String response;
                    try {
                        response = data.toString();
                        if (response.startsWith("WELCOME")) {
                            mark = response.charAt(7);
                            setIcon(mark);
                        }
                        if (response.startsWith("VALID_MOVE")) {

                            setMessage("Valid move, please wait");
                            pos = Character.getNumericValue(response.charAt(10));
                            setMove(true);
                            setPos(pos);
                        } else if (response.startsWith("OPPONENT_MOVED")) {
                            int loc = Integer.parseInt(response.substring(14));
                            setOpponentMove(loc);
                            setMessage("Opponent moved, your turn");
                        } else if (response.startsWith("VICTORY")) {
                            setVictory(true);
                        } else if (response.startsWith("DEFEAT")) {
                            setVictory(false);
                        } else if (response.startsWith("TIE")) {
                            setTie(true);
                        } else if (response.startsWith("MESSAGE")) {
                            setMessage(response.substring(7));
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    move = false;
                    pos = -1;
                }
            }
            catch (Exception e) {
                onReceiveCallback.accept("Connection closed");
            }
        }



    }


}
