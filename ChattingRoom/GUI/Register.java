
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Register extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {

            // FirstName Label
            Label firstNameLabel = new Label("First Name :");
            firstNameLabel.setPrefHeight(30);
            firstNameLabel.setPrefWidth(150);
            // FirstName TextField
            TextField firstNameText = new TextField();
            firstNameText.setPrefHeight(30);
            firstNameText.setPrefWidth(200);
            // FirstNameLabel for the FirstNameText
            firstNameLabel.setLabelFor(firstNameText);  // label is for this input box

            // LastName Label
            Label lastNameLabel = new Label("Last Name :");
            lastNameLabel.setPrefHeight(30);
            lastNameLabel.setPrefWidth(150);
            // LastName TextField
            TextField lastNameText = new TextField();
            lastNameText.setPrefHeight(30);
            lastNameText.setPrefWidth(200);
            // LastNameLabel for the LastNameText
            lastNameLabel.setLabelFor(lastNameText);

            // Username Label
            Label userNameLabel = new Label("Username :");
            userNameLabel.setPrefHeight(30);
            userNameLabel.setPrefWidth(150);
            // Username TextField
            TextField userNameText = new TextField();
            userNameText.setPrefHeight(30);
            userNameText.setPrefWidth(200);
            // UserNameLabel for the UserNameText
            userNameLabel.setLabelFor(userNameText);

            // PasswordLabel
            Label passwordLabel = new Label("Password :");
            passwordLabel.setPrefHeight(30);
            passwordLabel.setPrefWidth(150);
            // Password  TextField
            TextField passwordText = new TextField();
            passwordText.setPrefHeight(30);
            passwordText.setPrefWidth(200);
            // PasswordLabel for the PasswordText
            passwordLabel.setLabelFor(passwordText);


            // E-mail Label
            Label eMailLabel = new Label("E-mail :");
            eMailLabel.setPrefHeight(30);
            eMailLabel.setPrefWidth(150);
            // E-mail TextField
            TextField eMailText = new TextField();
            eMailText.setPrefHeight(30);
            eMailText.setPrefWidth(200);
            // E-mailLabel for the E-mailText
            eMailLabel.setLabelFor(eMailText);

            VBox labelBox = new VBox();
            labelBox.getChildren().add(firstNameLabel);
            labelBox.getChildren().add(lastNameLabel);
            labelBox.getChildren().add(userNameLabel);
            labelBox.getChildren().add(passwordLabel);
            labelBox.getChildren().add(eMailLabel);
            labelBox.setAlignment(Pos.CENTER);
            labelBox.setSpacing(20);

            VBox textBox = new VBox();
            textBox.getChildren().add(firstNameText);
            textBox.getChildren().add(lastNameText);
            textBox.getChildren().add(userNameText);
            textBox.getChildren().add(passwordText);
            textBox.getChildren().add(eMailText);
            textBox.setAlignment(Pos.CENTER);
            textBox.setSpacing(20);

            HBox signUpTextBox = new HBox();
            signUpTextBox.getChildren().add(labelBox);
            signUpTextBox.getChildren().add(textBox);
            signUpTextBox.setAlignment(Pos.CENTER);
            signUpTextBox.setSpacing(-10);

            // button registerButton
            Button submitButton = new Button("Submit");
            submitButton.setPrefHeight(30);
            submitButton.setPrefWidth(150);

            // Cancel Button
            Button cancelButton = new Button("Cancel");
            cancelButton.setPrefHeight(30);
            cancelButton.setPrefWidth(150);

            HBox buttonBox = new HBox();
            buttonBox.getChildren().add(submitButton);
            buttonBox.getChildren().add(cancelButton);
            buttonBox.setPrefHeight(50);
            buttonBox.setPrefWidth(100);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setSpacing(10);

            BorderPane pane = new BorderPane();
            pane.setCenter(signUpTextBox);
            pane.setBottom(buttonBox);
            Scene scene = new Scene(pane, 600, 400);


            primaryStage.setTitle("Register");
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.DECORATED);
            primaryStage.show();

        }
        public static void main(String[] args) {
            launch(args);
        }

    }
