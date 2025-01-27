package view;

import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Customer;
import model.Server;
import static model.Server.showMessage;

public class CustomerClientGUI extends Application {

    String customerID = "";
    Date d = new Date();
    SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public void start(Stage primaryStage) {
        // Login Screen
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setStyle("-fx-background-color: #f4f4f4; -fx-alignment: center;");

        Label loginLabel = new Label("Customer Login");
        loginLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-border-color: #0078d7; -fx-border-radius: 5; -fx-padding: 5;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-border-color: #0078d7; -fx-border-radius: 5; -fx-padding: 5;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-weight: bold;");

        loginLayout.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton);

        Scene loginScene = new Scene(loginLayout, 400, 300);

        // Main Screen
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #ffffff;");

        // Top Pane
        Label dashboardTitle = new Label("Customer Dashboard");
        dashboardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0078d7; -fx-padding: 10;");
        mainLayout.setTop(dashboardTitle);

        // Left Pane
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #cccccc;");

//        Button bookRoomButton = new Button("Book Room");
        Button cancelReservationButton = new Button("Cancel Reservation");
        Button logoutButton = new Button("Logout");

//        bookRoomButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelReservationButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");

        menu.getChildren().addAll(cancelReservationButton, logoutButton);
        mainLayout.setLeft(menu);

        // Center Pane
        TableView<ObservableList<String>> reservationTable = new TableView<>();
        reservationTable.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");

        mainLayout.setCenter(reservationTable);
        // Right Pane
        VBox reservationForm = new VBox(10);
        reservationForm.setPadding(new Insets(10));
        reservationForm.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        ComboBox<String> roomTypeComboBox = new ComboBox<>();
        roomTypeComboBox.getItems().addAll("Single", "Double", "Suite");

        DatePicker checkInDatePicker = new DatePicker();
        DatePicker checkOutDatePicker = new DatePicker();
        TextField guestNameField = new TextField();
        guestNameField.setPromptText("Name of guest");
        TextField numberOfGuestsField = new TextField();
        numberOfGuestsField.setPromptText("Number of Guests");

        Button submitReservationButton = new Button("Book Room");
        submitReservationButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-weight: bold;");

        reservationForm.getChildren().addAll(
                new Label("Room Type:"), roomTypeComboBox,
                new Label("Check-In Date:"), checkInDatePicker,
                new Label("Check-Out Date:"), checkOutDatePicker,
                new Label("Name of Guest:"), guestNameField,
                new Label("Number of Guests:"), numberOfGuestsField,
                submitReservationButton
        );
        mainLayout.setRight(reservationForm);

        Scene mainScene = new Scene(mainLayout, 800, 600);

        // Room Type
        roomTypeComboBox.valueProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (Customer.send_mess_to_server_help("SELECT,SELECT * FROM rooms  WHERE room_type = '" + newValue + "'  AND status = 'Available' LIMIT 1").equals("Approved")) {
                        Customer.getRoomData(reservationTable, "SELECT * FROM rooms  WHERE room_type = '" + newValue + "'  AND status = 'Available' LIMIT 1");
                    } else {
                        reservationTable.getColumns().clear();
                        showMessage("There is no data");
                    }
                }
        );
        submitReservationButton.setOnAction(e -> {
            if (Customer.send_mess_to_server_help("INSERT,").equals("Approved")) {
                Customer.book(reservationTable, customerID, String.valueOf(checkInDatePicker.getValue()), String.valueOf(checkOutDatePicker.getValue()), guestNameField.getText(), numberOfGuestsField.getText());
            } else {
                showMessage("error while booking");
            }
            //----------------------------------------------------
            String RID = Server.getR_ID(reservationTable, String.valueOf(checkInDatePicker.getValue()), String.valueOf(checkOutDatePicker.getValue()), guestNameField.getText(), numberOfGuestsField.getText());
            Server.track_transaction(RID, String.valueOf(checkInDatePicker.getValue()), String.valueOf(checkOutDatePicker.getValue()), reservationTable.getItems().get(0).get(1), "Pending");
        });

        // Switch Scenes
        loginButton.setOnAction(e -> {
            if (Customer.send_mess_to_server_help("LOGIN," + usernameField.getText() + "," + passwordField.getText() + ",Customer").equals("Approved")) {
                primaryStage.setScene(mainScene);
                customerID = Customer.getID(usernameField.getText());
                Customer.getRoomData(reservationTable, "SELECT * FROM reservation where C_ID = '" + customerID + "'");
                Server.track_transaction(customerID, sd.format(d), sd.format(d), "0", "login");
            }
//            if (Customer.login(usernameField.getText(), passwordField.getText())) {
//
//            }
        });

        cancelReservationButton.setOnAction(e -> {

            if (Customer.send_mess_to_server_help("CANCEL,").equals("Approved")) {
                Server.track_transaction(reservationTable, "canceled from Customer");
                Customer.cancel_reservations(reservationTable, usernameField.getText());
            } else {
                showMessage("error while cancel");
            }
        }
        );
        logoutButton.setOnAction(e -> {
            Server.track_transaction(customerID, sd.format(d), sd.format(d), "0", "logout");
            primaryStage.setScene(loginScene);
        });

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Customer Client");
        primaryStage.show();

        Thread th = new Thread() {
            @Override
            public void run() {
                Customer r = new Customer();
            }

        };
        th.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
