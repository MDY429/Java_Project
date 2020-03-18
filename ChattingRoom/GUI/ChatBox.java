
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

/**
 * The chat interface.
 * 
 * @author Ta-Yu Mar, Weijian Lin
 * @version 0.2 Beta 2020-03-18
 */
public class ChatBox extends Application {

    // Constructor variable
    ChatClient chatClient;
    User chatUser;
    IntegerProperty integerProperty;
    StringProperty stringProperty;
    Stage stage;

    /**
     * Constructor for ChatBox
     * 
     * @param chatClient      The input to chatClient data.
     * @param chatUser        The User you want to chat.
     * @param integerProperty The input of interProperty.
     * @param stringProperty  The input of stringProperty.
     */
    public ChatBox(ChatClient chatClient, User chatUser, IntegerProperty integerProperty, StringProperty stringProperty) {
        this.chatClient = chatClient;
        this.chatUser = chatUser;
        this.integerProperty = integerProperty;
        this.stringProperty = stringProperty;
        this.stage = new Stage();
        try {
            start(this.stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return chat box stage.
     * @return Stage.
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Start method.
     */
    public void start(Stage stage) throws Exception {

        // Title
        Text title = new Text(this.chatUser.userName);
        title.setFill(Color.BLACK);
        title.setStyle("-fx-font-family:Verdana;-fx-font-size: 20;");

        // TextArea
        TextArea textArea = new TextArea();
        // Disable edit function.
        textArea.setEditable(false);
        // Listen for coming string.
        stringProperty.addListener(event -> {
            String str = stringProperty.getValue();
            if(str.length() > 0 && chatUser.userName.equals(str.subSequence(0, chatUser.userName.length()))){
                textArea.appendText(str);
            }
        });

        VBox titleBox = new VBox();
        titleBox.getChildren().addAll(title, textArea);
        titleBox.setAlignment(Pos.CENTER);

        // ScrollPane
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setContent(textArea);

        // TextField
        TextField message = new TextField();
        message.setPrefHeight(30);
        message.setPrefWidth(320);

        // Set press Enter event.
        message.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                String str = message.getText();
                textArea.appendText("ME: " + str + "\n");
                sendMessage(str);
                message.clear();
            }
        });

        // Send Button.
        Button sendButton = new Button("Send");
        sendButton.setPrefHeight(30);
        sendButton.setPrefWidth(70);
        
        // send button action
        sendButton.setOnMouseClicked(event -> {
            String str = message.getText();
            textArea.appendText("ME: " + str + "\n");
            sendMessage(str);
            message.clear();
            message.requestFocus();
        });

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(message, sendButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPrefHeight(50);

        BorderPane pane = new BorderPane();
        pane.setTop(titleBox);
        pane.setBottom(buttonBox);
        Scene scene = new Scene(pane, 600, 400);        

        stage.setTitle(chatClient.user.userName + " chatting to " + chatUser.userName);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.show();
        
        // Set text filed focus.
        message.requestFocus();

        // Listen for server connection.
        integerProperty.addListener((observable, oldValue, newValue) -> {
			if((int)newValue < -1) {
				stage.close();
			}
		});

    }

    /**
     * After pressing send button or Enter, send message to chat user.
     * @param msg The input of message string.
     */
    public void sendMessage(String msg) {
        chatClient.sendMsg(chatUser.userId, chatUser.userName, msg);
    }

}