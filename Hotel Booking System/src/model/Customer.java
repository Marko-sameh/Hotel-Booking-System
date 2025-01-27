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

public class Customer {

//    static String id;
    static boolean login;
    private static Socket socket;
    private static DataOutputStream out = null;
    public static Server s;
//        private Socket socket = null;
    private static DataInputStream in = null;
//    private static DataOutputStream out = null;
    private int port = 5000;
    private String address = "localhost";

    public Customer() {
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
            System.out.println(message);
            response = in.readUTF(); // استقبال الرد
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            System.out.println("Communication error: " + e.getMessage());
        }
        return response;
    }


    public static String getID(String user) {
        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {

            ResultSet rs = s.executeQuery("SELECT C_id FROM customer WHERE C_name = '" + user + "' ");

            if (rs.next()) {
                String name = rs.getString("C_id");
                return name;

            } else {
                showMessage("there is no user");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return "";
    }

    public static void getRoomData(TableView<ObservableList<String>> table, String query) {

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try ( Connection con = db.mycon();  Statement s = con.createStatement()) {
            //SELECT * FROM rooms  WHERE room_type='single' AND status='Available' LIMIT 1
            ResultSet rs = s.executeQuery(query);

            // إعداد الأعمدة ديناميكيًا بناءً على عدد الأعمدة في الجدول
            table.getColumns().clear(); // مسح الأعمدة القديمة
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                final int colIndex = i - 1; // col index
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(rs.getMetaData().getColumnName(i));
                column.setCellValueFactory(param -> {
                    if (param.getValue().size() > colIndex) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(colIndex));
                    } else {
                        return new javafx.beans.property.SimpleStringProperty("");
                    }
                });
                column.setPrefWidth(120); // virtual value
                table.getColumns().add(column);
            }

            int count = 0;
            // Putting data in table
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
                count++;
            }
            if (count == 0) {
                System.out.println("count is 0");
            } else {
                System.out.println("count is " + count);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        table.setItems(data);

    }

    public static void book(TableView<ObservableList<String>> table, String id, String checkIn, String checkOut, String guestName, String guestNumber) {
        if (checkIn.isEmpty() || checkOut.isEmpty() || table.getItems() == null) {
            showMessage("Fill all fields");
            return;
        }


        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {
            s.executeUpdate(" INSERT INTO reservation(C_ID, room_ID, room_num, room_type, guest_name, "
                    + "check_in_dates, check_out_date, num_of_guests, reservation_status) "
                    + "VALUES ('" + id + "','" + table.getItems().get(0).get(0) + "','" + table.getItems().get(0).get(1) + "','" + table.getItems().get(0).get(2)
                    + "','" + guestName + "'" + ",'" + checkIn + "','" + checkOut + "','" + guestNumber + "','Pending')");

            showMessage("Booked successfully , Waiting for receptionist accepting");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void cancel_reservations(TableView<ObservableList<String>> table, String usernameField) {
        String reservation_id = "";
        int selectedRowIndex = table.getSelectionModel().getSelectedIndex();
        if (selectedRowIndex != -1) {
            ObservableList<String> selectedRow = table.getSelectionModel().getSelectedItem();
            reservation_id = selectedRow.get(2);
        } else {
            showMessage("No row is selected.");
        }

        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {
            s.executeUpdate(" DELETE FROM reservation WHERE R_ID = '" + reservation_id + "' ");
            showMessage("reservation is Canceled successfully ");
        } catch (SQLException e) {
            System.out.println(e);
        }
        Customer.getRoomData(table, "SELECT * FROM reservation where C_ID = '" + Customer.getID(usernameField) + "'");

    }

}
