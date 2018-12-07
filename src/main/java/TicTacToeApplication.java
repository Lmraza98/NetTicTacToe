/**
 * Created by lucasraza on 12/7/18.
 */

import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TicTacToeApplication extends Application {

    Private ClientNetworkConnection client = new ClientNetworkConnection();
    public static void main(String[] args) {
        launch(args);
    }

    private Parent createContent(){


    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

}
