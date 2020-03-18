
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChatBox extends Application {

    ChatClient chatClient;
    User chatUser;
    IntegerProperty integerProperty;
    StringProperty stringProperty;
    Stage stage;

    public ChatBox(ChatClient chatClient, User chatUser, IntegerProperty integerProperty, StringProperty stringProperty) {
        this.chatClient = chatClient;
        this.chatUser = chatUser;
        this.integerProperty = integerProperty;
        this.stringProperty = stringProperty;
        this.stage = new Stage();
        try {
            start(this.stage);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Stage getStage(){
        return this.stage;
    }

    public void start(Stage stage) throws Exception {


        //title and textArea
        Text title = new Text(this.chatUser.userName);
        title.setFill(Color.BLACK);
        title.setStyle("-fx-font-family:Verdana;-fx-font-size: 20;");
        Text subtitle = new Text("online or not");
        subtitle.setFill(Color.BLACK);
        subtitle.setStyle("-fx-font-family:Verdana;-fx-font-size: 10;");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        stringProperty.addListener(event -> {
            String str = stringProperty.getValue();
            if(str.length() > 0 && chatUser.userName.equals(str.subSequence(0, chatUser.userName.length()))){
                textArea.appendText(str);
            }
        });


        VBox titleBox = new VBox();
        titleBox.getChildren().addAll(title, subtitle, textArea);
        titleBox.setAlignment(Pos.CENTER);


        // ScrollPane
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setVvalue(1);


        Button sendButton = new Button("Send");
        sendButton.setPrefHeight(30);
        sendButton.setPrefWidth(70);


        //textField
        TextField message = new TextField();
        message.setPrefHeight(30);
        message.setPrefWidth(320);
        message.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                String str = message.getText();
                textArea.appendText("ME: " + str + "\n");
                sendMessage(str);
                message.clear();
            }
        });

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(message, sendButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPrefHeight(50);

        // friendList

        BorderPane pane = new BorderPane();
        pane.setTop(titleBox);
        pane.setBottom(buttonBox);
        Scene scene = new Scene(pane, 600, 400);


        stage.setTitle("Chatting to " + chatUser.userName);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();        
        message.requestFocus();

        // send button action
        sendButton.setOnMouseClicked(event -> {
            String str = message.getText();
            textArea.appendText("ME: " + str + "\n");
            sendMessage(str);
            message.clear();
            message.requestFocus();
        });

        integerProperty.addListener((observable, oldValue, newValue) -> {
			if((int)newValue < -1) {
				stage.close();
			}
		});

    }

    public void sendMessage(String msg) {
        chatClient.sendMsg(chatUser.userId, chatUser.userName, msg);
    }

    public static void main(String[] args) {
        launch(args);
    }
}