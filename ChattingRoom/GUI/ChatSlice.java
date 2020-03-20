import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * The chat interface.
 * 
 * @author Ta-Yu Mar
 * @version 0.2 Beta 2020-03-20
 */
public class ChatSlice extends Pane {

    TextArea textArea;
    TextField textField;
    Button  sendButton;
    BorderPane chatBox;
    ScrollPane sp;

    // Constructor variable
    ChatClient chatClient;
    User chatUser;
    IntegerProperty integerProperty;
    StringProperty stringProperty;

   /**
     * Constructor for ChatSlice
     * 
     * @param chatClient      The input to chatClient data.
     * @param chatUser        The User you want to chat.
     * @param integerProperty The input of interProperty.
     * @param stringProperty  The input of stringProperty.
     */
    public ChatSlice(ChatClient chatClient, User chatUser, IntegerProperty integerProperty, StringProperty stringProperty) {
        this.chatClient = chatClient;
        this.chatUser = chatUser;
        this.integerProperty = integerProperty;
        this.stringProperty = stringProperty;
        this.chatBox = new BorderPane();
        chatSlice();
    }

    /**
     * Return chat box pane.
     * 
     * @return BorderPane
     */
    public BorderPane getChatBox() {
        return this.chatBox;
    }

    /**
     * Draw the pane.
     */
    public void chatSlice(){

        textArea = new TextArea();
        textArea.setPrefHeight(400);
        // Disable edit function.
        textArea.setEditable(false);
        // Listen for coming string.
        stringProperty.addListener(event -> {
            String str = stringProperty.getValue();
            if(str.length() > 0 && chatUser.userName.equals(str.subSequence(0, chatUser.userName.length()))) {
                textArea.appendText(str);
            }
        });

        VBox titleBox = new VBox();
        titleBox.getChildren().addAll(textArea);
        titleBox.setAlignment(Pos.CENTER);

        // ScrollPane
        sp = new ScrollPane();
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
        sendButton = new Button("Send");
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

        chatBox.setTop(titleBox);
        chatBox.setBottom(buttonBox);

        // return chatBox;
    }

    /**
     * After pressing send button or Enter, send message to chat user.
     * 
     * @param msg The input of message string.
     */
    public void sendMessage(String msg) {
        chatClient.sendMsg(chatUser.userId, chatUser.userName, msg);
    }

}