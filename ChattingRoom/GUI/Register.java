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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Register extends Application {

	private Label firstNameLabel, lastNameLabel, userNameLabel, passwordLabel, eMailLabel;
	private TextField firstNameText, lastNameText, userNameText, passwordText, emailText;
	private VBox labelBox, textBox;
	private HBox signUpTextBox, buttonBox;
	private Button submitButton, cancelButton;
	private BorderPane pane;
	private Scene scene;

	Stage primaryStage;
	ChatClient chatClient;
	private IntegerProperty integerProperty;

	public Register(Stage primaryStage, ChatClient chatClient, IntegerProperty integerProperty) {
		this.primaryStage = primaryStage;
		this.chatClient = chatClient;
		this.integerProperty = integerProperty;
	}

	@Override
	public void start(Stage stage) throws Exception {

		// FirstName Label
		firstNameLabel = new Label("First Name :");
		firstNameLabel.setPrefHeight(30);
		firstNameLabel.setPrefWidth(150);
		// FirstName TextField
		firstNameText = new TextField();
		firstNameText.setPrefHeight(30);
		firstNameText.setPrefWidth(200);
		// FirstNameLabel for the FirstNameText
		firstNameLabel.setLabelFor(firstNameText); // label is for this input box

		// LastName Label
		lastNameLabel = new Label("Last Name :");
		lastNameLabel.setPrefHeight(30);
		lastNameLabel.setPrefWidth(150);
		// LastName TextField
		lastNameText = new TextField();
		lastNameText.setPrefHeight(30);
		lastNameText.setPrefWidth(200);
		// LastNameLabel for the LastNameText
		lastNameLabel.setLabelFor(lastNameText);

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
		passwordText = new TextField();
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
		labelBox.getChildren().add(firstNameLabel);
		labelBox.getChildren().add(lastNameLabel);
		labelBox.getChildren().add(userNameLabel);
		labelBox.getChildren().add(passwordLabel);
		labelBox.getChildren().add(eMailLabel);
		labelBox.setAlignment(Pos.CENTER);
		labelBox.setSpacing(20);

		textBox = new VBox();
		textBox.getChildren().add(firstNameText);
		textBox.getChildren().add(lastNameText);
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

		// button registerButton
		submitButton = new Button("Submit");
		submitButton.setPrefHeight(30);
		submitButton.setPrefWidth(150);
		// action for submitButton
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				// TODO Need check function and update
				System.out.println("Register succeed with the information bellow.");
				System.out.println("First Name: " + firstNameText.getText());
				System.out.println("Last Name: " + lastNameText.getText());
				System.out.println("UserName: " + userNameText.getText());
				System.out.println("password: " + passwordText.getText());
				System.out.println("Email: " + emailText.getText());
				System.out.println("Press register submit");

				// Before send to register, set integerProperty be 0.
				integerProperty.set(0);
				// Send information to register a new account.
				chatClient.sendSignUp(userNameText.getText(), passwordText.getText(), emailText.getText());
				Thread thread = new Thread(new Runnable() {
				int tryError = 15;

					@Override
					public void run() {
						Runnable registerProcess = new Runnable() {
							@Override
							public void run() {
								tryError--;
								if (integerProperty.getValue() == 1) {
									Alert alert = new Alert(AlertType.ERROR);
									alert.setTitle("Register Fail");
									alert.setHeaderText("Check Your Information");
									String s = "Please check your username length longer than 6.\n"
												+"Password longer than 6.\n"
												+ "Check your Email is correct.";
									alert.setContentText(s);
									alert.showAndWait();
									if(!alert.isShowing()){
										popUpSignUI();
										stage.hide();
									}
									System.out.println("FAIL");
								}
								if (integerProperty.getValue() == 2) {
									Alert alert = new Alert(AlertType.NONE);
									alert.setTitle("Register Success");
									alert.setHeaderText("Register Success");
									String s = "Register Success!!!";
									alert.setContentText(s);
									alert.showAndWait();
									if(!alert.isShowing()){
										popUpSignUI();
										stage.hide();
									}
									System.out.println("Register SUCCESS");
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

							}
												
							Platform.runLater(registerProcess);
						}						
					}
				});
				thread.setDaemon(true);
				thread.start();
			}
		});

		// Cancel Button
		cancelButton = new Button("Cancel");
		cancelButton.setPrefHeight(30);
		cancelButton.setPrefWidth(150);
		// action for cancel button
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				popUpSignUI();
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

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				popUpSignUI();
			}
		});

	}

	private void popUpSignUI() {
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
