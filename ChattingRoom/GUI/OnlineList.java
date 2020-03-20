
import java.util.HashMap;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * After logIn user interface.
 * 
 * @author Ta-Yu Mar, Mingxi Li
 * @version 0.2 Beta 2020-03-18
 */
public class OnlineList extends Application {

    // Recode every chat window, prevent duplicate.
    HashMap<User, ChatBox> chatWindow = new HashMap<>();
    HashMap<Integer, BorderPane> chatPane = new HashMap<>();

    // JavaFx variable
    private static final double X_WIDTH = 900;
    private static final double Y_HEIGHT = 500;
    private ListView<String> listView;
    private Label onlineListLabel;
    private TextArea ChattingText;
    private TextField typingText;
    private HBox hBox;
    private VBox vBox, vhBox;
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem exitMenuItem;
    private BorderPane pane;
    private Scene scene;

    // Constructor variable.
    Stage primaryStage;
    ChatClient chatClient;
    ListProperty<User> listProperty;
    IntegerProperty integerProperty;
    StringProperty stringProperty;

    /**
     * ConStructor for OnlineList.
     * @param primaryStage The input of stage from Sign.
     * @param chatClient The input of chatClient data.
     * @param listProperty The input of listProperty.
     * @param integerProperty The input of integerProperty.
     * @param stringProperty The input of stringProperty.
     */
    public OnlineList(Stage primaryStage,
                      ChatClient chatClient,
                      ListProperty<User> listProperty,
                      IntegerProperty integerProperty,
                      StringProperty stringProperty) {
        this.primaryStage = primaryStage;
        this.chatClient = chatClient;
        this.listProperty = listProperty;
        this.integerProperty = integerProperty;
        this.stringProperty = stringProperty;
    }

    /**
     * The start method.
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Show the User Name on window title.
        stage.setTitle(chatClient.user.userName);

        //onlineList
        onlineListLabel = new Label("Online Users");
        onlineListLabel.setStyle("-fx-font-family:Verdana;-fx-font-size: 24;");
        onlineListLabel.setAlignment(Pos.CENTER);

        listView = new ListView<>();
        listView.setPrefSize(300, 450);

        // Listen for new user login.
        listProperty.addListener(new ListChangeListener<User>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends User> change) {

                while (change.next()) {

                    // Init the listView.
                    listView.getItems().clear();
                    // Get the change and update the list.
                    for (int i = 0; i < change.getAddedSubList().size(); i++) {
                        // Update the list.
                        if(change.getAddedSubList().get(i).userName.equals(chatClient.user.userName)) {
                            listView.getItems().add(change.getAddedSubList().get(i).userName + "\tThis is me");
                        }
                        else {
                            listView.getItems().add(change.getAddedSubList().get(i).userName);
                        }
                    }
                }
            }
        });

        // Click event for open chat window.
        listView.setOnMouseClicked(event -> {

            ObservableList<Integer> selectedIndices = listView.getSelectionModel().getSelectedIndices();

            // If click twice, means want to chat with this user.
            for (Object o : selectedIndices) {
                if(chatClient.user.userId == listProperty.get((int)o).userId){
                    // If user click themselves
                    pane.setCenter(vhBox);
                    continue;
                }
                // System.out.println("o = " + o + " (" + o.getClass() + ")");
                // System.out.println(listView.getItems().get((int) o));

                // Get the user info and open chat window.
                User chatUser = listProperty.get((int)o);
                showChatSlice(chatUser);
            }

        });

        // Listen for integer change.
        integerProperty.addListener((intObservable, intOldValue, intNewValue) -> {

            // This is init signal. Do nothing.
            if((int)intNewValue == -1){
                return;
            }

            // Server connection is broken.
            if((int)intNewValue < -1) {
                stage.close();
                primaryStage.show();
			}
            
            // Got someone want to chat to you. And open chat window.
            for(User chatUser: listProperty) {
                if(chatUser.userId == (int)intNewValue) {                    
                    if(chatUser != null) {
                        // jumpChatBox(chatUser);
                        System.out.println("someone height light");
                    }
                    break;
                }
            }            
        });
        
        Button sendButton = new Button("SEND");
		sendButton.setPrefHeight(30);
		sendButton.setPrefWidth(150);

        typingText = new TextField();
		typingText.setPrefHeight(30);
		typingText.setPrefWidth(400);
		
		hBox = new HBox();
		hBox.getChildren().add(typingText);
		hBox.getChildren().add(sendButton);
		hBox.setPrefHeight(50);
		hBox.setPrefWidth(150);
		hBox.setAlignment(Pos.BASELINE_CENTER);
		hBox.setSpacing(10);
		
		ChattingText = new TextArea();
		ChattingText.setPrefHeight(500);
        ChattingText.setPrefWidth(400);
        ChattingText.setDisable(true);
        
        vhBox = new VBox();
		vhBox.getChildren().add(ChattingText);
		vhBox.getChildren().add(hBox);
		vhBox.setPrefHeight(300);
		vhBox.setPrefWidth(150);
		vhBox.setAlignment(Pos.TOP_CENTER);
		vhBox.setSpacing(0);
        
        // Set menu bar
        menuBar = new MenuBar();
	    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());	    

        // Set menu
        fileMenu = new Menu("File");

        // Set menu item
	    exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitMenuItem);

        // Set exit event        
        exitMenuItem.setOnAction((ActionEvent e) -> {
            exitEvent(e);
        });

        // Combine all menu items.
	    menuBar.getMenus().addAll(fileMenu);	    

        // Show the list on VBox.
        vBox = new VBox();
        vBox.getChildren().add(onlineListLabel);
        vBox.getChildren().add(listView);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(15);

        pane = new BorderPane();
        pane.setTop(menuBar);
        pane.setLeft(vBox);
        pane.setCenter(vhBox);

        scene = new Scene(pane, X_WIDTH, Y_HEIGHT, Color.WHITE);

        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setMinWidth(X_WIDTH);
        stage.show();

        // When User close the application, ask them to check.
        stage.setOnCloseRequest((WindowEvent e) -> {
            exitEvent(e);
        });

    }

    /**
     * Show chat slice.
     * 
     * @param chatUser The input of chat user.
     */
    public void showChatSlice(User chatUser) {
        System.out.println(chatUser);
        if(chatPane.get(chatUser.userId) == null) {
            System.out.println("create new chatWindow");
            ChatSlice chatSlice = new ChatSlice(chatClient, chatUser, integerProperty, stringProperty);
            chatPane.put(chatUser.userId, chatSlice.getChatBox());
            pane.setCenter(chatSlice.getChatBox());
        }
        else {
            System.out.println("The chatWindow already exist.");
            pane.setCenter(chatPane.get(chatUser.userId));
        }    
    }

    /**
     * Execute the exit process.
     * @param event The input of event.
     */
    public void exitEvent(Event event){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Leave chatting");
        alert.setHeaderText("Do You Want To Leave ??");
        alert.setContentText("If yes, Application will be closed.\nAre you sure?");
        
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);

        if (button == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        } else {
            event.consume();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}