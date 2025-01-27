
package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;


public class Server {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;

    public Server() {
    }

    public Server(int port) {

        create_server_connection(5000);

    }


    // function to prompt message the user
    public static void showMessage(String mess) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(null, mess);
            }
        };

        worker.execute();
    }

    // function to get reservation data from database
    public static void get_reservation_list(TableView<ObservableList<String>> table) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try ( Connection con = db.mycon();  Statement s = con.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT * FROM reservation");

            // إعداد الأعمدة ديناميكيًا بناءً على عدد الأعمدة في الجدول
            table.getColumns().clear(); // مسح الأعمدة القديمة
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
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

    // function to get Transactions data from database
   public static void get_Transaction_Log(TableView<ObservableList<String>> table) {
//        Platform.runLater(() -> {

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try ( Connection con = db.mycon();  Statement s = con.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT R_ID,check_in_dates,check_out_date,room_num,transaction_statu FROM transactions ");

            // إعداد الأعمدة ديناميكيًا بناءً على عدد الأعمدة في الجدول
            table.getColumns().clear(); // مسح الأعمدة القديمة
            int columnCount = rs.getMetaData().getColumnCount();
            // ----------------------
            TableColumn<ObservableList<String>, String> column = new TableColumn<>("Reservation Number");
            column.setCellValueFactory(param -> {
                if (param.getValue().size() > 0) {
                    return new javafx.beans.property.SimpleStringProperty(param.getValue().get(0));
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            column.setPrefWidth(120);
            table.getColumns().add(column);
//            ----------------------------
            TableColumn<ObservableList<String>, String> column2 = new TableColumn<>("Check-in Date");
            column2.setCellValueFactory(param -> {
                if (param.getValue().size() > 1) {
                    return new javafx.beans.property.SimpleStringProperty(param.getValue().get(1));
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            column2.setPrefWidth(120);
            table.getColumns().add(column2);
            //            ----------------------------
            TableColumn<ObservableList<String>, String> column3 = new TableColumn<>("Check-out Date");
            column3.setCellValueFactory(param -> {
                if (param.getValue().size() > 2) {
                    return new javafx.beans.property.SimpleStringProperty(param.getValue().get(2));
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            column3.setPrefWidth(120);
            table.getColumns().add(column3);

            //            ----------------------------
            TableColumn<ObservableList<String>, String> column4 = new TableColumn<>("room num");
            column4.setCellValueFactory(param -> {
                if (param.getValue().size() > 3) {
                    return new javafx.beans.property.SimpleStringProperty(param.getValue().get(3));
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            column4.setPrefWidth(120);
            table.getColumns().add(column4);
//            ----------------------------
            TableColumn<ObservableList<String>, String> column5 = new TableColumn<>("Status");
            column5.setCellValueFactory(param -> {
                if (param.getValue().size() > 4) {
                    return new javafx.beans.property.SimpleStringProperty(param.getValue().get(4));
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            column5.setPrefWidth(120);
            table.getColumns().add(column5);

            // add data
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

        table.setItems(data);

//        });
    }

    
    
    public static void track_transaction(String R_ID, String check_in_dates, String check_out_date, String room_num, String transaction_statu) {

        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {
            s.executeUpdate(" INSERT INTO `transactions`(`R_ID`, `check_in_dates`, `check_out_date`, `room_num`, "
                    + "`transaction_statu`) VALUES ( '" + R_ID + "' ,'" + check_in_dates + "','" + check_out_date + "','" + room_num + "','" + transaction_statu + "') ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void track_transaction(TableView<ObservableList<String>> table, String transaction_statu) {
        String R_ID = "";
        String check_in_dates = "";
        String check_out_date = "";
        String room_num = "";
        int selectedRowIndex = table.getSelectionModel().getSelectedIndex();
        if (selectedRowIndex != -1) {
            ObservableList<String> selectedRow = table.getSelectionModel().getSelectedItem();

            R_ID = selectedRow.get(2);
            check_in_dates = selectedRow.get(6);
            check_out_date = selectedRow.get(7);
            room_num = selectedRow.get(3);
        } else {
            showMessage("No row is selected.");
        }

        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {
            s.executeUpdate(" INSERT INTO `transactions`(`R_ID`, `check_in_dates`, `check_out_date`, `room_num`, "
                    + "`transaction_statu`) VALUES ( '" + R_ID + "' ,'" + check_in_dates + "','" + check_out_date + "','" + room_num + "','" + transaction_statu + "' ) ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getR_ID(TableView<ObservableList<String>> table, String checkIn, String checkOut, String guestName, String guestNumber) {
        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {

            ResultSet rs = s.executeQuery("SELECT R_ID FROM reservation WHERE room_num='" + table.getItems().get(0).get(1) + "' "
                    + "AND guest_name = '" + guestName + "' AND "
                    + "check_in_dates = '" + checkIn + "' AND check_out_date = '" + checkOut + "' AND num_of_guests = '" + guestNumber + "' ");

            if (rs.next()) {
                return rs.getString("R_ID");
            }
        } catch (SQLException e) {
            showMessage("" + e);
        }
        return "";
    }

    // function to get rooms status data from database
    public static void get_room_status(TableView<ObservableList<String>> table) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try ( Connection con = db.mycon();  Statement s = con.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT room_num, status FROM rooms");

            table.getColumns().clear();

            // -----------------------
            TableColumn<ObservableList<String>, String> column = new TableColumn<>("Room Number");
            column.setCellValueFactory(param -> {
                if (param.getValue().size() > 0) {
                    return new javafx.beans.property.SimpleStringProperty(param.getValue().get(0));
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            column.setPrefWidth(120); // ضبط العرض
            table.getColumns().add(column);

            // ----------------------
            TableColumn<ObservableList<String>, String> column2 = new TableColumn<>("Status");
            column2.setCellValueFactory(param -> {
                if (param.getValue().size() > 1) {
                    return new javafx.beans.property.SimpleStringProperty(param.getValue().get(1));
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
            column2.setPrefWidth(120); // ضبط العرض
            table.getColumns().add(column2);

            // add data
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i)); // إضافة البيانات للصف
                }
                data.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        table.setItems(data);
    }

    public boolean login(String user, String password, String type) {
        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {

            ResultSet rs = s.executeQuery("SELECT * FROM login WHERE L_user_name = '" + user + "'  AND L_password = '" + password + "' "
                    + " AND L_registration_type='" + type + "' ");

            if (rs.next()) {
//                id = getID(user);
                return true;

            } else {
                showMessage("" + "Wrong user name or password");
            }
        } catch (SQLException e) {
            showMessage("" + e);
        }
        return false;
    }

    public void create_server_connection(int port) {
        try ( ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                // قبول اتصال العميل
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // تشغيل خيط جديد لكل عميل
//                ClientHandler clientHandler = new ClientHandler(clientSocket);
                help(clientSocket);
//                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public static boolean database_checker(String query) {
        try ( java.sql.Connection conn = db.mycon();  Statement s = conn.createStatement();) {
            ResultSet rs = s.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }


    public void help(Socket clientSocket) {
        Thread th = new Thread() {
            @Override
            public void run() {
                try ( DataInputStream in = new DataInputStream(clientSocket.getInputStream());  DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
                    String message;
                    while (true) {
                        // استقبال الرسالة من العميل
                        message = in.readUTF();
                        System.out.println("Message from client: " + message);
                        String arr[] = message.split(",");

                        if (arr[0].equals("LOGIN")) {
                            if (login(arr[1], arr[2], arr[3])) {
                                out.writeUTF("Approved");
                            } else {
                                out.writeUTF("Not Approved");
                            }
                        } else if (arr[0].equals("SELECT")) {
                            if (database_checker(arr[1])) {
                                out.writeUTF("Approved");
                            } else {
                                out.writeUTF("Not Approved");
                            }
                        } else if (arr[0].equals("INSERT")) {
                            out.writeUTF("Approved");
                        } else if (arr[0].equals("CANCEL")) {
                            out.writeUTF("Approved");
                        } else if (arr[0].equals("UPDATE")) {
                            out.writeUTF("Approved");
                        } 

                        // إرسال رد للعميل
//                        out.writeUTF("Server received: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                }
            }

        };
        th.start();
    }

}
