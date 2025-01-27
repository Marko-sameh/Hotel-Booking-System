package view;

import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.receptionist;
import model.Server;
import static model.Server.showMessage;

public class ReceptionistClientGUI extends Application {

    Date d = new Date();
    SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public void start(Stage primaryStage) {
        // Login Screen
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setStyle("-fx-background-color: #f4f4f4; -fx-alignment: center;");
        Label loginLabel = new Label("Receptionist Login");
        loginLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-border-color: #0078d7; -fx-border-radius: 5; -fx-padding: 5;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-border-color: #0078d7; -fx-border-radius: 5; -fx-padding: 5;");
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-weight: bold;");
        loginLayout.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton);

        Scene loginScene = new Scene(loginLayout, 400, 300);

        // Main Screen
        BorderPane mainLayout = new BorderPane();

        // Top Pane
        Label dashboardTitle = new Label("Receptionist Dashboard");
        dashboardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0078d7; -fx-padding: 10;");
        mainLayout.setTop(dashboardTitle);

        // Left Pane
        VBox menu = new VBox(10);
        Button viewPendingReservationsButton = new Button("View Pending Reservations");
        Button confirmReservationButton = new Button("Confirm Reservation");
        Button cancelReservationButton = new Button("Cancel Reservation");
        Button logoutButton = new Button("Logout");
        menu.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #cccccc;");
        menu.getChildren().addAll(viewPendingReservationsButton, confirmReservationButton, cancelReservationButton, logoutButton);
        mainLayout.setLeft(menu);
        cancelReservationButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");

        // Center Pane
//        TableView<String> reservationTable = new TableView<>();
        TableView<ObservableList<String>> reservationTable = new TableView<>();
//        receptionist.send_mess_to_server_help("get_active_reservations");
        // Add columns to the table
        mainLayout.setCenter(reservationTable);

        Scene mainScene = new Scene(mainLayout, 1000, 600);

        // Switch Scenes
        loginButton.setOnAction(e -> {

            if (receptionist.send_mess_to_server_help("LOGIN," + usernameField.getText() + "," + passwordField.getText() + ",Receptionist").equals("Approved")) {
                primaryStage.setScene(mainScene);

                if (receptionist.send_mess_to_server_help("SELECT,SELECT * FROM reservation").equals("Approved")) {
                    receptionist.get_active_reservations(reservationTable, "SELECT * FROM reservation");
                } else {
                    showMessage("There is no data");
                }
                Server.track_transaction("0", sd.format(d), sd.format(d), "0", "login");

            }
        }
        );
        //--------------
        viewPendingReservationsButton.setOnAction(e -> {
            if (receptionist.send_mess_to_server_help("SELECT,SELECT * FROM reservation WHERE reservation_status ='Pending' ").equals("Approved")) {
                receptionist.get_active_reservations(reservationTable, "SELECT * FROM reservation WHERE reservation_status ='Pending' ");
            } else {
                showMessage("There is no data");
            }

        });
        //--------------
        confirmReservationButton.setOnAction(e -> {
            if (receptionist.send_mess_to_server_help("UPDATE,").equals("Approved")) {
                Server.track_transaction(reservationTable, "confirmed");
                receptionist.handel_pending_reservations(reservationTable, "confirmed");
            } else {
                showMessage("Server : Confirmed Not Approved");
            }

        });
        //--------------

        cancelReservationButton.setOnAction(e -> {
            if (receptionist.send_mess_to_server_help("UPDATE,").equals("Approved")) {
                Server.track_transaction(reservationTable, "cancelled");
                receptionist.handel_pending_reservations(reservationTable, "cancelled");
            } else {
                showMessage("Server : Cancelled Not Approved");
            }

        });
        //--------------
        logoutButton.setOnAction(e -> {
            Server.track_transaction("0", sd.format(d), sd.format(d), "0", "logout");
            primaryStage.setScene(loginScene);
        });
        //--------------

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Receptionist Client");
        primaryStage.show();
        Thread th = new Thread() {
            @Override
            public void run() {
                receptionist r = new receptionist();

            }

        };
        th.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
