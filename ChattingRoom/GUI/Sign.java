import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Sign extends Application{
	
	private Text title, subtitle;
	private VBox titleBox, labelBox, textBox;
	private Label userNameLabel, passwordLabel;
	private TextField userNameText;
	private PasswordField passwordText;
	private HBox loginTextBox, buttonBox;
	private Button registerButton, signInButton, forgetPasswordButton;
	private BorderPane pane;
	private Scene scene;
	ChatClient chatClient = new ChatClient();
	private BooleanProperty booleanProperty = new SimpleBooleanProperty(false);

	@Override
	public void start(Stage primaryStage) throws Exception {

		// title
		title = new Text("WELCOME");
		title.setFill(Color.BLACK);
		title.setStyle("-fx-font-family:Verdana;-fx-font-size: 70;");
		subtitle = new Text("Chicago Chatroom");
		subtitle.setFill(Color.BLACK);
		subtitle.setStyle("-fx-font-family:Verdana;-fx-font-size: 18;");

		titleBox = new VBox();
		titleBox.getChildren().add(title);
		titleBox.getChildren().add(subtitle);
		titleBox.setAlignment(Pos.BASELINE_CENTER);

		// label&text field
		userNameLabel = new Label("USERNAME:");
		userNameLabel.setPrefHeight(30);
		userNameLabel.setPrefWidth(200);

		userNameText = new TextField();
		userNameText.setPrefHeight(30);
		userNameText.setPrefWidth(200);

		passwordLabel = new Label("PASSWORD:");
		passwordLabel.setPrefHeight(30);
		passwordLabel.setPrefWidth(200);

		passwordText = new PasswordField();
		passwordText.setPrefHeight(30);
		passwordText.setPrefWidth(200);
		passwordLabel.setLabelFor(passwordText);

		labelBox = new VBox();
		labelBox.getChildren().add(userNameLabel);
		labelBox.getChildren().add(passwordLabel);
		labelBox.setAlignment(Pos.CENTER);
		labelBox.setSpacing(15);

		textBox = new VBox();
		textBox.getChildren().add(userNameText);
		textBox.getChildren().add(passwordText);
		textBox.setAlignment(Pos.CENTER);
		textBox.setSpacing(15);

		loginTextBox = new HBox();
		loginTextBox.getChildren().add(labelBox);
		loginTextBox.getChildren().add(textBox);
		loginTextBox.setAlignment(Pos.CENTER);
		loginTextBox.setSpacing(-40);

		// button
		registerButton = new Button();
		registerButton.setText("REGISTER");
		registerButton.setPrefHeight(30);
		registerButton.setPrefWidth(150);
		//action for register button
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				popUpRegisterUI(primaryStage, chatClient);
				primaryStage.hide();
			}
		});
		

		signInButton = new Button("Sign IN");
		signInButton.setPrefHeight(30);
		signInButton.setPrefWidth(150);
		//action for sign in button
		signInButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				//TODO Need check function and update.
				System.out.println("Sign in succeed:");
				System.out.println("Username: "+userNameText.getText());
				System.out.println("Password: "+passwordText.getText());
				popUpClientUI();
				primaryStage.hide();
			}
		});

		forgetPasswordButton = new Button("FORGET PASSWORD");
		forgetPasswordButton.setPrefHeight(30);
		forgetPasswordButton.setPrefWidth(150);
		//action for forget button
		forgetPasswordButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				popUpForgetUI();
				primaryStage.hide();
			}

		});

		buttonBox = new HBox();
		buttonBox.getChildren().add(registerButton);
		buttonBox.getChildren().add(signInButton);
		buttonBox.getChildren().add(forgetPasswordButton);
		buttonBox.setPrefHeight(50);
		buttonBox.setPrefWidth(100);
		buttonBox.setAlignment(Pos.BASELINE_CENTER);
		buttonBox.setSpacing(15);

		pane = new BorderPane();
		pane.setTop(titleBox);
		pane.setCenter(loginTextBox);
		pane.setBottom(buttonBox);
		scene = new Scene(pane, 600, 400);

		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.DECORATED);
		primaryStage.show();

		// Connect to Server.
		chatClient.userConnectToServer(50000);
		Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable executeClient = new Runnable() {
                    @Override
                    public void run() {
						// Get server feedback.
						chatClient.runMain(booleanProperty);
                    }
                };

                while (true) {
                    try {
						Thread.sleep(20);
					}
					catch (InterruptedException ex) {

					}
                    Platform.runLater(executeClient);
                }
            }

        });
        thread.setDaemon(true);
		thread.start();
		
		// When User close the application.
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private void popUpRegisterUI(Stage primaryStage, ChatClient chatClient) {
		Register register=new Register(primaryStage, chatClient, booleanProperty);
		try {
			register.start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void popUpClientUI() {
		// TODO Auto-generated method stub
		
	}
	
	private void popUpForgetUI() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		launch(args);
	}


}

