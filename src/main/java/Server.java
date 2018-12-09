import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Created by lucasraza on 3/8/17
 *
 *
 */
public class Server {
    private static ServerSocket serverSocket;
    private static ArrayList<Socket> sockets;
    private static Socket socket;

    private int port;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(55555);

            sockets = new ArrayList<>();

            for (int i = 1; i < 3; i++) {
                socket = serverSocket.accept();
                System.out.println("Player " + i + " connected");
                sockets.add(socket);

            }
            Game game = new Game();
            System.out.println("Created game, waiting for players to connect");

            Game.PlayerHandler playerX = game.new PlayerHandler(sockets.remove(sockets.size() - 1), 'X');
            Game.PlayerHandler playerO = game.new PlayerHandler(sockets.remove(sockets.size() - 1), 'O');
            game.currentPlayer = playerX;

            playerX.start();
            playerO.start();
            System.out.println("Game started");


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//
//    public Server(int port, Consumer<Serializable> onReceiveCallback) {
//        this.onReceiveCallback = onReceiveCallback;
//        this.port = port;
//
//    }
//    public static Server createServer(){
//        return new Server(77777, data ->{
//
//        });
//    }
//
//    public static void send(Serializable data) throws IOException {
//        connThread.out.writeObject(data);
//    }
//
//    public static void startConnection() throws IOException {
//        connThread.start();
//    }
//
//    public void closeConnection() throws IOException {
//        connThread.socket.close();
//    }

    private int getPort() {
        return port;
    }



    static class Game {

        private PlayerHandler[] board = {
                null, null, null,
                null, null, null,
                null, null, null
        };
        PlayerHandler currentPlayer;

        public boolean isFull() {
            for (int i = 0; i < board.length; i++) {
                if (board[i] == null) {
                    return false;
                }
            }
            return true;
        }

        public boolean hasWinner() {
            if (checkHorizontalWin() || checkVerticalWin() || checkDiagonalWin()) {
                return true;
            } else {
                return false;
            }

        }

        public boolean checkHorizontalWin() {
            if (board[0] != null && board[0] == board[1] && board[0] == board[2]) {
                return true;
            } else if (board[3] != null && board[3] == board[4] && board[3] == board[5]) {
                return true;
            } else if (board[6] != null && board[6] == board[7] && board[6] == board[8]) {
                return true;
            } else {
                return false;
            }
        }

        public boolean checkVerticalWin() {
            if (board[0] != null && board[0] == board[3] && board[0] == board[6]) {
                return true;
            } else if (board[1] != null && board[1] == board[4] && board[1] == board[7]) {
                return true;
            } else if (board[2] != null && board[2] == board[5] && board[2] == board[8]) {
                return true;
            } else {
                return false;
            }
        }

        public boolean checkDiagonalWin() {
            if (board[0] != null && board[0] == board[4] && board[0] == board[8]) {
                return true;
            } else if (board[2] != null && board[2] == board[4] && board[2] == board[6]) {
                return true;
            } else {
                return false;
            }
        }

        public boolean move(PlayerHandler player, int location) { //A player moves based on their assigned Piece
            System.out.println("Player moved at location " + location);
            if (board[location] == null) {
                board[location] = player;
               // currentPlayer = currentPlayer.opponent;
                currentPlayer.opponentMoved(location);
                return true;
            }
            return false;
        }

        public class PlayerHandler extends Thread {
            private char mark;
            private String name;
            private Socket socket;

            private PlayerHandler opponent;

            private ObjectOutputStream out;
            private ObjectInputStream in;

            private PlayerHandler(Socket socket, char mark) {
                this.socket = socket;
                this.mark = mark;

                try {
                    socket.setTcpNoDelay(true);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            private void opponentMoved(int location) {
                try {
                    out.writeObject("OPPONENT_MOVED" + location);
                    out.writeObject(
                            hasWinner() ? "DEFEAT" : isFull() ? "TIE" : "");
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            public String toString(){
                String mark = "" + this.mark;
                return mark;
            }

            public void run() {
                System.out.println("running");


                try {
                    out.writeObject("WELCOME" + mark);

                    while(true) {
                        String response = in.readObject().toString();
                        while(response != null) {
                            if (response.startsWith("MOVE")) {
                                int location = Integer.parseInt(response.substring(4));
                                System.out.println(location);
                                System.out.println(this);
                                if (move(this, location)) {
                                    System.out.println("after move is called");
                                    out.writeObject("VALID_MOVE" + location);
                                    out.writeObject(hasWinner() ? "VICTORY"
                                            : isFull() ? "TIE"
                                            : "");
                                }
                            }
                        }

                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
