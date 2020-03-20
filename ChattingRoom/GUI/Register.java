import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * The register user interface.
 * 
 * @author Ta-Yu Mar, Mingxi Li, Weijian Lin
 * @version 0.2 Beta 2020-03-18
 */
public class Register extends Application {

	private Label userNameLabel, passwordLabel, eMailLabel;
	private TextField userNameText, emailText;
	private PasswordField passwordText;
	private VBox labelBox, textBox;
	private HBox signUpTextBox, buttonBox;
	private Button submitButton, cancelButton;
	private BorderPane pane;
	private Scene scene;

	// Constructor variable.
	private Stage primaryStage;
	private ChatClient chatClient;
	private IntegerProperty integerProperty;

	// Constructor for Register.
	public Register(Stage primaryStage, ChatClient chatClient, IntegerProperty integerProperty) {
		this.primaryStage = primaryStage;
		this.chatClient = chatClient;
		this.integerProperty = integerProperty;
	}

	/**
	 * The start method.
	 */
	@Override
	public void start(Stage stage) throws Exception {

		// Username Label
		userNameLabel = new Label("Username :");
		userNameLabel.setPrefHeight(30);
		userNameLabel.setPrefWidth(150);

		// Username TextField
		userNameText = new TextField();
		userNameText.setPrefHeight(30);
		userNameText.setPrefWidth(200);

		// UserNameLabel for the UserNameText
		userNameLabel.setLabelFor(userNameText);

		// PasswordLabel
		passwordLabel = new Label("Password :");
		passwordLabel.setPrefHeight(30);
		passwordLabel.setPrefWidth(150);
		
		// Password TextField
		passwordText = new PasswordField();
		passwordText.setPrefHeight(30);
		passwordText.setPrefWidth(200);
		// PasswordLabel for the PasswordText
		passwordLabel.setLabelFor(passwordText);

		// E-mail Label
		eMailLabel = new Label("E-mail :");
		eMailLabel.setPrefHeight(30);
		eMailLabel.setPrefWidth(150);
		// E-mail TextField
		emailText = new TextField();
		emailText.setPrefHeight(30);
		emailText.setPrefWidth(200);
		// E-mailLabel for the E-mailText
		eMailLabel.setLabelFor(emailText);

		labelBox = new VBox();
		labelBox.getChildren().add(userNameLabel);
		labelBox.getChildren().add(passwordLabel);
		labelBox.getChildren().add(eMailLabel);
		labelBox.setAlignment(Pos.CENTER);
		labelBox.setSpacing(20);

		textBox = new VBox();
		textBox.getChildren().add(userNameText);
		textBox.getChildren().add(passwordText);
		textBox.getChildren().add(emailText);
		textBox.setAlignment(Pos.CENTER);
		textBox.setSpacing(20);

		signUpTextBox = new HBox();
		signUpTextBox.getChildren().add(labelBox);
		signUpTextBox.getChildren().add(textBox);
		signUpTextBox.setAlignment(Pos.CENTER);
		signUpTextBox.setSpacing(-10);

		// Register Button
		submitButton = new Button("Submit");
		submitButton.setPrefHeight(30);
		submitButton.setPrefWidth(150);

		// Action for submit Button
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				// Before send to register, set integerProperty be 0.
				integerProperty.set(0);

				// Send information to register a new account.
				chatClient.sendSignUp(userNameText.getText(), passwordText.getText(), emailText.getText());

				// Create a thread to handle register process.
				Thread thread = new Thread(new Runnable() {
				int tryError = 15;

					@Override
					public void run() {
						Runnable registerProcess = new Runnable() {
							@Override
							public void run() {
								tryError--;
								// Show alert.
								drawAlert(integerProperty.getValue(), stage, tryError);
							}
						};

						while (tryError > 0) {
							if (integerProperty.getValue() > 0) {
								break;
							}
							try {
								Thread.sleep(300);
							} catch (InterruptedException ex) {

							}
												
							Platform.runLater(registerProcess);
						}						
					}
				});
				thread.setDaemon(true);
				thread.start();
			}
		});

		// If integerProperty becomes less than -1, the connection is broken.
		integerProperty.addListener((observable, oldValue, newValue) -> {
			if((int)newValue < -1) {
				stage.close();
				primaryStage.show();
			}
		});

		// Cancel Button
		cancelButton = new Button("Cancel");
		cancelButton.setPrefHeight(30);
		cancelButton.setPrefWidth(150);

		// Action for cancel button
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				primaryStage.show();
				stage.hide();
			}
		});

		buttonBox = new HBox();
		buttonBox.getChildren().add(submitButton);
		buttonBox.getChildren().add(cancelButton);
		buttonBox.setPrefHeight(50);
		buttonBox.setPrefWidth(100);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(10);

		pane = new BorderPane();
		pane.setCenter(signUpTextBox);
		pane.setBottom(buttonBox);
		scene = new Scene(pane, 600, 400);

		stage.setTitle("Register");
		stage.setScene(scene);
		stage.initStyle(StageStyle.DECORATED);
		stage.show();

		// If user close the window, turn back to signIn window.
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				primaryStage.show();
			}
		});

	}

	/**
	 * Draw alert.
	 * 
	 * @param type  Type of alert.
	 * @param stage Stage.
	 * @param tryError Try Error number.
	 */
	public void drawAlert(int type, Stage stage, int tryError) {
		Alert alert;
		String str;
		if(tryError > 0) {
			if(type == 2) {
				alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Register Success");
				alert.setHeaderText("Register Success");
				str = "Register Success!!!";
			}
			else if(type == 3) {
				alert = new Alert(AlertType.ERROR);
				alert.setTitle("Register Fail");
				alert.setHeaderText("Cannot Connect DB");
				str = "Cannot Connect DataBase.\nPlease try later.";
			}
			else if(type == 4) {
				alert = new Alert(AlertType.ERROR);
				alert.setTitle("Register Fail");
				alert.setHeaderText("Check Your Information");
				str = "Please check your username length longer than 6.\n"
							+"Password longer than 6.\n"
							+ "Check your Email is correct.";
			}
			else if(type == 5) {
				alert = new Alert(AlertType.ERROR);
				alert.setTitle("Register Fail");
				alert.setHeaderText("Duplicate User Name");
				str = "The User Name is duplicate!\nPlease try another.";
			}
			else {
				alert = new Alert(AlertType.ERROR);
				alert.setTitle("Register Fail");
				alert.setHeaderText("Unknown Error");
				str = "The bad happen.\nPlease try later.";
			}

			alert.setContentText(str);
			alert.showAndWait();
			if(!alert.isShowing()){
				primaryStage.show();
				stage.hide();
			}
		}
		else {
			// tryError <= 0
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Register Fail");
			alert.setHeaderText("Unknown Error");
			str = "The bad happen.\nPlease try later.";
			alert.setContentText(str);
			alert.showAndWait();
			if(!alert.isShowing()){
				primaryStage.show();
				stage.hide();
			}
		}
		
	}

}
