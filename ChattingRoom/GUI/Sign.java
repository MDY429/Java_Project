import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * First user interface to user and it will connect to server.
 * 
 * @author Ta-Yu Mar, Mingxi Li, Weijian Lin
 * @version 0.2 Beta 2020-03-18
 */
public class Sign extends Application {

	private Text title, subtitle;
	private VBox titleBox, labelBox, textBox;
	private Label userNameLabel, passwordLabel;
	private TextField userNameText;
	private PasswordField passwordText;
	private HBox loginTextBox, buttonBox;
	private Button registerButton, signInButton, forgetPasswordButton;
	private BorderPane pane;
	private Scene scene;

	// Constructor variable.
	private ChatClient chatClient;
	private IntegerProperty integerProperty;
	private ListProperty<User> listProperty;
	private StringProperty stringProperty;

	/**
	 * Constructor for Sign.
	 */
	public Sign() {
		this.chatClient = new ChatClient();
		this.integerProperty = new SimpleIntegerProperty(0);
		this.listProperty = new SimpleListProperty<User>();
		this.stringProperty = new SimpleStringProperty();
	}

	/**
	 * The start method.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		// Title
		title = new Text("WELCOME");
		title.setFill(Color.BLACK);
		title.setStyle("-fx-font-family:Verdana;-fx-font-size: 70;");

		// Subtitle
		subtitle = new Text("Chicago Chatroom");
		subtitle.setFill(Color.BLACK);
		subtitle.setStyle("-fx-font-family:Verdana;-fx-font-size: 18;");

		// Main Box
		titleBox = new VBox();
		titleBox.getChildren().add(title);
		titleBox.getChildren().add(subtitle);
		titleBox.setAlignment(Pos.BASELINE_CENTER);

		// Label & Text field
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

		// Register Button
		registerButton = new Button();
		registerButton.setText("REGISTER");
		registerButton.setPrefHeight(30);
		registerButton.setPrefWidth(150);
		// action for register button
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				popUpRegisterUI(primaryStage, chatClient);
				primaryStage.hide();
			}
		});

		// SignIn Button
		signInButton = new Button("Sign IN");
		signInButton.setPrefHeight(30);
		signInButton.setPrefWidth(150);
		
		// Action for sign in button
		signInButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				// Before send to SignIn, set integerProperty be 0.
				integerProperty.set(0);

				// Send information to login account.
				chatClient.sendSignIn(userNameText.getText(), passwordText.getText());
				
				// Create a thread to handle signIn process.
				Thread thread = new Thread(new Runnable() {
					int tryError = 15;

					@Override
					public void run() {
						Runnable signInProcess = new Runnable() {
							@Override
							public void run() {
								tryError--;

								// If integerProperty is 1 or tryError becomes 0 show login error.
								if(integerProperty.getValue() == 1 || tryError == 0) {
									Alert alert = new Alert(AlertType.ERROR);
									alert.setTitle("Login Fail");
									alert.setHeaderText("Login Fail");
									alert.setContentText("Please check your username and password.");
									alert.showAndWait();
									userNameText.clear();
									passwordText.clear();
								}

								// If integerProperty is 2, turn to another stage.
								if(integerProperty.getValue() == 2) {
									chatClient.findOnlineUsers();
									popUpOnlineListUI(primaryStage);
									primaryStage.hide();
								}
							}
						};

						while (tryError > 0) {
							if (integerProperty.getValue() > 0) {
								break;
							}
							try {
								Thread.sleep(300);
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
							Platform.runLater(signInProcess);
						}
					}
				});
				thread.setDaemon(true);
				thread.start();
			}
		});

		// Forget Password Button
		forgetPasswordButton = new Button("FORGET PASSWORD");
		forgetPasswordButton.setPrefHeight(30);
		forgetPasswordButton.setPrefWidth(150);

		// Action for forget button
		forgetPasswordButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				popUpForgetUI();
				primaryStage.hide();
			}
		});

		// Button's property.
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
		
		// Create a thread to connect server and collect the data from server.
		Thread thread = new Thread(new Runnable() {

			// Connect to Server.
			boolean isConnected = chatClient.userConnectToServer(50000);
			int tryError = 0;
			Stage alertStage;

			@Override
			public void run() {
				
				// If isConnected is true, run the corresponding process.
				Runnable executeChatClient = new Runnable() {
					@Override
					public void run() {

						// Get server feedback.
						int ret = chatClient.runMain(integerProperty, listProperty, stringProperty);
						
						if (ret < -1) {
							// Cannot connect server.
							isConnected = false;
						} else {
							tryError = 0;
						}
                    }
				};
				
				// If isConnected is false, try to reconnect to server for 10 times.
				Runnable waitConnect = new Runnable() {
					@Override
					public void run() {

						tryError++;

						if(alertStage != null && alertStage.isShowing()) {
							alertStage.close();
						}

						// Draw Alert
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Connection Fail");
						alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
						alertStage.setAlwaysOnTop(true);
						if(tryError <= 10) {
							alert.setHeaderText("Check Your Internet");
							String s = "Please check your Internet.\nSystem will reconnect 10 times.\n"
										+ "Re-Try: " + tryError;
							alert.setContentText(s);
							alertStage.show();
							isConnected = chatClient.userConnectToServer(50000);
						}
						else {
							alert.setHeaderText("CLOSE APPLICATION");
							String s = "Oops, Still cannot connect to Server.\nPlease try later.\nApplication will be closed.";
							alert.setContentText(s);
							alertStage.showAndWait();
							Platform.exit();
						}
					}
				};

                while (true) {
                    try {
						if(isConnected) {
							Thread.sleep(20);
						}
						else {
							Thread.sleep(5000);
							// If retry over 10 times will break.
							if(tryError > 10) {
								break;
							}
						}
					}
					catch (InterruptedException ex) {
						ex.printStackTrace();
					}

					if(isConnected) {
						Platform.runLater(executeChatClient);
					}
					else {
						Platform.runLater(waitConnect);
					}
                }
            }

        });
        thread.setDaemon(true);
		thread.start();
		
		// When User close the application.
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				System.out.println("user close window");
				Platform.exit();
				System.exit(0);
			}
		});
	}

	/**
	 * Pop up the register window.
	 * 
	 * @param primaryStage The main stage.
	 * @param chatClient   The ChatClient data.
	 */
	private void popUpRegisterUI(Stage primaryStage, ChatClient chatClient) {

		Register register=new Register(primaryStage, chatClient, integerProperty);
		try {
			register.start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pop up the Online List.
	 * 
	 * @param primaryStage The main stage.
	 */
	private void popUpOnlineListUI(Stage primaryStage) {
		
		OnlineList onlineList = new OnlineList(primaryStage, chatClient, listProperty, integerProperty, stringProperty);
		try {
			onlineList.start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void popUpForgetUI() {
		// TODO Auto-generated method stub
		
	}

}

