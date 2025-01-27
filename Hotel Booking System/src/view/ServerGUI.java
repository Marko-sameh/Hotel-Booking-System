package view;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;

public class ServerGUI extends Application {

    public static TableView<ObservableList<String>> reservationTable;
    public static TableView<ObservableList<String>> roomAvailabilityList;
    public static TableView<ObservableList<String>> transactionLog;

    @Override
    public void start(Stage primaryStage) {
        // Top Pane
        Label title = new Label("Hotel Booking Server");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10;");
        BorderPane root = new BorderPane();
        BorderPane.setAlignment(title, javafx.geometry.Pos.CENTER);
        root.setTop(title);

        // TabPane
        TabPane tabPane = new TabPane();

        // Active Reservations Tab
        Tab activeReservationsTab = new Tab("Active Reservations");
        activeReservationsTab.setClosable(false);
        reservationTable = new TableView<>();
        //---------
        Server.get_reservation_list(reservationTable);
        //---------

//        activeReservationsTab.setContent(new VBox(reservationTable));
        Button addButton = new Button("Reload");

        // Create a Region for spacing
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create an HBox for the button
        HBox topBar = new HBox(spacer, addButton);
        topBar.setAlignment(Pos.CENTER_RIGHT); // Align the button to the right
        topBar.setSpacing(10);

        // Create the VBox containing the topBar and the TableView
        VBox contentBox = new VBox(topBar, reservationTable);
        contentBox.setSpacing(10);

        activeReservationsTab.setContent(contentBox);

        // Room Availability Tab
        Tab roomAvailabilityTab = new Tab("Room Availability");
        roomAvailabilityTab.setClosable(false);
        roomAvailabilityList = new TableView<>();
        //---------
        Server.get_room_status(roomAvailabilityList);
        //---------

//        roomAvailabilityTab.setContent(new VBox(roomAvailabilityList));
        Button addButton2 = new Button("Reload");

        // Create a Region for spacing
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // Create an HBox for the button
        HBox topBar2 = new HBox(spacer2, addButton2);
        topBar2.setAlignment(Pos.CENTER_RIGHT); // Align the button to the right
        topBar2.setSpacing(10);

        // Create the VBox containing the topBar and the TableView
        VBox contentBox2 = new VBox(topBar2, roomAvailabilityList);
        roomAvailabilityTab.setContent(contentBox2);

        // Transaction Log Tab
        Tab transactionLogTab = new Tab("Transaction Log");
        transactionLogTab.setClosable(false);
        transactionLog = new TableView<>();
        //---------
        Server.get_Transaction_Log(transactionLog);
        //---------

        transactionLog.setEditable(false);
//        transactionLogTab.setContent(new VBox(transactionLog));
        Button addButton3 = new Button("Reload");

        // Create a Region for spacing
        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);

        // Create an HBox for the button
        HBox topBar3 = new HBox(spacer, addButton3);
        topBar.setAlignment(Pos.CENTER_RIGHT); // Align the button to the right
        topBar.setSpacing(10);

        // Create the VBox containing the topBar and the TableView
        VBox contentBox3 = new VBox(topBar3, transactionLog);
        transactionLogTab.setContent(contentBox3);

        // Add tabs to TabPane
        tabPane.getTabs().addAll(activeReservationsTab, roomAvailabilityTab, transactionLogTab);
        root.setCenter(tabPane);

        // Create Scene and Apply CSS
        Scene scene = new Scene(root, 1250, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server GUI");
        primaryStage.show();

        addButton.setOnAction(e -> Server.get_reservation_list(reservationTable));
        addButton2.setOnAction(e -> Server.get_room_status(roomAvailabilityList));
        addButton3.setOnAction(e -> Server.get_Transaction_Log(transactionLog));

        Thread th = new Thread() {
            @Override
            public void run() {
                Server s = new Server(5000);
            }
        };
        th.start();

    }

    public static void main(String[] args) {
        launch(args);

    }
}