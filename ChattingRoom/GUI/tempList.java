
import java.util.HashMap;

import javafx.application.Application;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class tempList extends Application {

    ChatClient chatClient;
    ListProperty<User> listProperty;
    StringProperty stringProperty;
    HashMap<User, Client> chatWindow = new HashMap<>();

    public tempList(ChatClient chatClient, ListProperty<User> listProperty, StringProperty stringProperty) {
        this.chatClient = chatClient;
        this.listProperty = listProperty;
        this.stringProperty = stringProperty;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(chatClient.user.userName);

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
                        Client client = new Client(chatClient, chatUser, stringProperty);
                        chatWindow.put(chatUser, client);
                        Stage sss = client.getStage();
                        sss.show();

                        // User close window
                        sss.showingProperty().addListener((observable, oldValue, newValue) -> {
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

        VBox vBox = new VBox(listView);

        Scene scene = new Scene(vBox, 250, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}