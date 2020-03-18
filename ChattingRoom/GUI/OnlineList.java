
import java.util.HashMap;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OnlineList extends Application {

    Stage primaryStage;
    ChatClient chatClient;
    ListProperty<User> listProperty;
    IntegerProperty integerProperty;
    StringProperty stringProperty;
    HashMap<User, ChatBox> chatWindow = new HashMap<>();

    public OnlineList(Stage primaryStage, ChatClient chatClient, ListProperty<User> listProperty, IntegerProperty integerProperty, StringProperty stringProperty) {
        this.primaryStage = primaryStage;
        this.chatClient = chatClient;
        this.listProperty = listProperty;
        this.integerProperty = integerProperty;
        this.stringProperty = stringProperty;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(chatClient.user.userName);

        ListView<String> listView = new ListView<>();

        listProperty.addListener(new ListChangeListener<User>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends User> change) {
                System.out.println("ListChangeListener");
                while (change.next()) {
                    listView.getItems().clear();
                    for (int i = 0; i < change.getAddedSubList().size(); i++) {
                        System.out.println(change.getAddedSubList().get(i).userName);

                        listView.getItems().add(change.getAddedSubList().get(i).userName);
                    }
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            ObservableList<Integer> selectedIndices = listView.getSelectionModel().getSelectedIndices();
            if (event.getClickCount() == 2) {
                for (Object o : selectedIndices) {
                    if (chatClient.user.userName.equals(listView.getItems().get((int) o))) {
                        continue;
                    }
                    // System.out.println("o = " + o + " (" + o.getClass() + ")");
                    // System.out.println(listView.getItems().get((int) o));
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

        integerProperty.addListener((intObservable, intOldValue, intNewValue) -> {
            // System.out.println(intOldValue + " ->> " + intNewValue);
            if((int)intNewValue == -1){
                return;
            }

            if((int)intNewValue < -1) {
                stage.close();
                primaryStage.show();
			}
            
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

        VBox vBox = new VBox(listView);

        Scene scene = new Scene(vBox, 250, 300);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}