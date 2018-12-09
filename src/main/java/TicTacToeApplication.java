/**
 * Created by lucasraza on 12/7/18.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.*;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class TicTacToeApplication extends Application {

    private NetworkConnection client = createClient();
    private ImageView icon;
    private ImageView opponentIcon;

    private Image cross = new Image("/Cross.png");
    private Image circle = new Image("/Circle.png");
    private GridPane grid;
    private ArrayList<Button> buttons = new ArrayList<>();


    private Text message;
    @Override
    public void init() throws Exception {

    }
    public static void main(String[] args) {
        launch(args);
    }

    private void disableButtons(){
        for(int i = 0; i < 9; i ++){
            buttons.get(i).setDisable(true);
        }
    }
    private void enableButtons(){
        for(int i = 0; i < 9; i ++){
            buttons.get(i).setDisable(false);
        }
    }

    private Parent createContent(){


        grid = new GridPane();

        int square = 0;
        for(int i = 0; i < 3; i++) {

            for (int a = 0; a < 3; a++) {
                Button button = new Button();

                button.setMinSize(100,100);
                button.setId(Integer.toString(square));
                button.setOnAction(event->{
                    try{
                            client.send("MOVE" + button.getId());
                            System.out.println("MOVE" + button.getId());
                            if(client.isValidMove()){
                                System.out.println("wtf");
                                setImage(button, icon);
                            }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                });
                grid.add(button, i, a);
                buttons.add(button);
                System.out.println("Creating button " + square++);
            }
        }

        HBox hbox = new HBox();
        Button play = new Button("PLAY");
        play.setAlignment(Pos.CENTER_RIGHT);
        message = new Text("Press play to play");
        message.setX(300);
        message.setY(30);
        hbox.setAlignment(Pos.TOP_CENTER);
        hbox.getChildren().addAll(message,play);

        play.setOnAction(event->{
            try {
                client.startConnection();
            }catch(Exception e){
                e.printStackTrace();
            }
            play.setDisable(true);
        });


        VBox root = new VBox();
        root.getChildren().addAll(hbox,grid);

        return root;
    }
    private NetworkConnection createClient(){
        return new NetworkConnection("127.0.0.1",55555, data ->{
            Platform.runLater(()-> {
                if(client.getIcon()=='X'){
                    message.setText("You are X: YOUR MOVE");
                    icon = new ImageView(cross);
                    opponentIcon = new ImageView(circle);

                    //wait for opponents move/ set opponets move
                }
                else{
                    icon = new ImageView(circle);
                    opponentIcon = new ImageView(cross);
                    message.setText("You are O: Opponent's move");
                }

                message.setText(data.toString());
            });
        });
    }
    private void setImage(Button button, ImageView icon){

        System.out.println("setting image");
        button.setGraphic(icon);
        button.setDisable(true);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }
}
