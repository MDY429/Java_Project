
import java.util.HashMap;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * After logIn user interface.
 * 
 * @author Ta-Yu Mar
 * @version 0.2 Beta 2020-03-18
 */
public class OnlineList extends Application {

    // Recode every chat window, prevent duplicate.
    HashMap<User, ChatBox> chatWindow = new HashMap<>();

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

        ListView<String> listView = new ListView<>();

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
                        listView.getItems().add(change.getAddedSubList().get(i).userName);
                    }
                }
            }
        });

        // Click event for open chat window.
        listView.setOnMouseClicked(event -> {

            ObservableList<Integer> selectedIndices = listView.getSelectionModel().getSelectedIndices();

            // If click twice, means want to chat with this user.
            if (event.getClickCount() == 2) {
                for (Object o : selectedIndices) {
                    if (chatClient.user.userName.equals(listView.getItems().get((int) o))) {
                        // If user click themselves
                        continue;
                    }
                    // System.out.println("o = " + o + " (" + o.getClass() + ")");
                    // System.out.println(listView.getItems().get((int) o));

                    // Get the user info and open chat window.
                    User chatUser = listProperty.get((int)o);
                    if(chatWindow.get(chatUser) == null) {
                        System.out.println("create new chatWindow");
                        ChatBox chatBox = new ChatBox(chatClient, chatUser, integerProperty, stringProperty);
                        chatWindow.put(chatUser, chatBox);
                        Stage chatStage = chatBox.getStage();
                        chatStage.show();

                        // User close window
                        chatStage.showingProperty().addListener((observable, oldValue, newValue) -> {
                            if (oldValue == true && newValue == false) {
                                System.out.println("The window close");
                                chatWindow.remove(chatUser);
                            }
                        });
                    }
                    else {
                        System.out.println("The chatWindow already exist.");
                    }
                }
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
                        if(chatWindow.get(chatUser) == null) {
                            System.out.println("create new chatWindow");
                            ChatBox chatBox = new ChatBox(chatClient, chatUser, integerProperty, stringProperty);
                            chatWindow.put(chatUser, chatBox);
                            Stage chatStage = chatBox.getStage();
                            chatStage.show();                            
        
                            // User close window
                            chatStage.showingProperty().addListener((observable, oldValue, newValue) -> {
                                if (oldValue == true && newValue == false) {
                                    System.out.println("The window close");
                                    chatWindow.remove(chatUser);
                                }
                            });
                        }
                        else {
                            System.out.println("The chatWindow already exist.");
                        }
                    }
                    break;
                }
            }            
        });

        // Show the list on VBox.
        VBox vBox = new VBox(listView);
        Scene scene = new Scene(vBox, 250, 300);
        stage.setScene(scene);
        stage.show();

        // When User close the application, ask them to check.
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
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
                    t.consume();
                }
			}
		});

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}