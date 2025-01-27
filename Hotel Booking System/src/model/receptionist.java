package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import static model.Server.showMessage;


public class receptionist {

    static boolean login;
    public static Server s;

    private Socket socket = null;
    private static DataInputStream in = null;
    private static DataOutputStream out = null;
    private int port = 5000;
    private String address = "localhost";

    public receptionist() {
        create_client_con();
    }

   
    public void create_client_con() {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected to the server!");
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    public static String send_mess_to_server_help(String message) {
        String response = "";
        try {
            out.writeUTF(message); // إرسال الرسالة
            response = in.readUTF(); // استقبال الرد
//            System.out.println("Server response: " + response);
        } catch (IOException e) {
            System.out.println("Communication error: " + e.getMessage());
        }
        return response;
    }

    // function to get reservation data from database
    public static void get_active_reservations(TableView<ObservableList<String>> table, String query) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try ( Connection con = db.mycon();  Statement s = con.createStatement()) {
            ResultSet rs = s.executeQuery(query);

            table.getColumns().clear();
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 3; i <= columnCount; i++) {
                final int colIndex = i - 1; // فهرس العمود
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(rs.getMetaData().getColumnName(i));
                column.setCellValueFactory(param -> {
                    if (param.getValue().size() > colIndex) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(colIndex));
                    } else {
                        return new javafx.beans.property.SimpleStringProperty("");
                    }
                });
                column.setPrefWidth(120); // قيمة افتراضية
                table.getColumns().add(column);
            }

            // تعبئة البيانات في الجدول
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        // تعيين البيانات إلى الجدول
        table.setItems(data);
    }

    public static void handel_pending_reservations(TableView<ObservableList<String>> table, String commande) {
        String reservation_id = "";
        String room_num = "";
        int selectedRowIndex = table.getSelectionModel().getSelectedIndex();
        if (selectedRowIndex != -1) {
            ObservableList<String> selectedRow = table.getSelectionModel().getSelectedItem();

            reservation_id = selectedRow.get(2);
            room_num = selectedRow.get(3);

        } else {
            showMessage("No row is selected.");
        }

//        if (reservation_status_var.equals("Pending")) {
        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {
            s.executeUpdate(" UPDATE reservation SET reservation_status ='" + commande + "' WHERE R_ID = '" + reservation_id + "' ");
            showMessage("reservation is " + commande + " successfully ");
        } catch (SQLException e) {
            System.out.println(e);
        }
        System.out.println(room_num);

        if (commande.equals("confirmed")) {
            try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {
                s.executeUpdate(" UPDATE rooms SET status ='Unavailable' WHERE room_num = '" + room_num + "' ");
                showMessage("reservation is " + commande + " successfully ");
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {
                s.executeUpdate(" UPDATE rooms SET status ='available' WHERE room_num = '" + room_num + "' ");
                showMessage("reservation is " + commande + " successfully ");
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
        receptionist.get_active_reservations(table, "SELECT * FROM reservation WHERE reservation_status ='Pending' ");

//        } else {
//            showMessage("this row is not in the Pending list.");
//        }
    }

}


