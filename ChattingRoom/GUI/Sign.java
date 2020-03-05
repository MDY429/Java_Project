import javafx.application.Application;
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

public class Sign extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {

        //title
        Text title = new Text("WELCOME");
        title.setFill(Color.BLACK);
        title.setStyle("-fx-font-family:Verdana;-fx-font-size: 70;");
        Text subtitle = new Text("Group Chat");
        subtitle.setFill(Color.BLACK);
        subtitle.setStyle("-fx-font-family:Verdana;-fx-font-size: 18;");

        VBox titleBox = new VBox();
        titleBox.getChildren().add(title);
        titleBox.getChildren().add(subtitle);
        titleBox.setAlignment(Pos.CENTER);

        //label&text field
        Label userNameLabel = new Label("USERNAME:");
        userNameLabel.setPrefHeight(30);
        userNameLabel.setPrefWidth(200);

        TextField userNameText = new TextField();
        userNameText.setPrefHeight(30);
        userNameText.setPrefWidth(200);

        Label passwordLabel = new Label("PASSWORD:");
        passwordLabel.setPrefHeight(30);
        passwordLabel.setPrefWidth(200);

        PasswordField passwordText = new PasswordField();
        passwordText.setPrefHeight(30);
        passwordText.setPrefWidth(200);
        passwordLabel.setLabelFor(passwordText);

        VBox labelBox = new VBox();
        labelBox.getChildren().add(userNameLabel);
        labelBox.getChildren().add(passwordLabel);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setSpacing(15);

        VBox textBox = new VBox();
        textBox.getChildren().add(userNameText);
        textBox.getChildren().add(passwordText);
        textBox.setAlignment(Pos.CENTER);
        textBox.setSpacing(15);

        HBox logInTextBox = new HBox();
        logInTextBox.getChildren().add(labelBox);
        logInTextBox.getChildren().add(textBox);
        logInTextBox.setAlignment(Pos.CENTER);
        logInTextBox.setSpacing(-40);

        //button
        Button registerButton = new Button("REGISTER");
        registerButton.setPrefHeight(30);
        registerButton.setPrefWidth(150);

        Button signInButton = new Button("Sign IN");
        signInButton.setPrefHeight(30);
        signInButton.setPrefWidth(150);

        Button AdministratorsButton = new Button("ADMINISTRATORS");
        AdministratorsButton.setPrefHeight(30);
        AdministratorsButton.setPrefWidth(150);

        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(registerButton);
        buttonBox.getChildren().add(signInButton);
        buttonBox.getChildren().add(AdministratorsButton);
        buttonBox.setPrefHeight(50);
        buttonBox.setPrefWidth(100);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        BorderPane pane = new BorderPane();
        pane.setTop(titleBox);
        pane.setCenter(logInTextBox);
        pane.setBottom(buttonBox);
        Scene scene = new Scene(pane,600,400);


        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}


