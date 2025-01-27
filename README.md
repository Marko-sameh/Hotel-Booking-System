# Hotel-Booking-System
main features:

1. TCP Protocol Usage: The code uses the TCP protocol for reliable client-server communication

2. Connection-Oriented: TCP ensures a connection-oriented approach, allowing persistent and reliable communication between clients (customers/receptionists) and the server.

3. Data Integrity: TCP guarantees the delivery of data packets in the correct order, which is critical for sending and receiving booking details, login credentials, and reservation updates without loss or corruption.

-------------------------------------------------------------------------------------------------------------------------------------------------------
other features:

1. Customer Features:
 - Login: Customers can log in with a username and password.
 - Room Booking: Customers can view available rooms based on room type (single, double, suite) and book them by entering check-in/check-out dates and guest information.
 - Cancel Reservation: Customers can cancel existing reservations.
 - Reservation Display: A table displays the customer's existing reservations with details like room number, check-in/out dates, and status.

3. Receptionist Features:

 - Login: Receptionists log in using their credentials.
 - View Pending Reservations: Receptionists can view all pending reservations, with actions to confirm or cancel them.
 - Reservation Confirmation: Receptionists can confirm a reservation and update room status (mark room as "Unavailable" when confirmed).
 - Reservation Cancellation: Receptionists can cancel a reservation and update room status (mark room as "Available" when cancelled).
 - Active Reservation Management: Receptionists can interact with reservation data, update statuses, and manage rooms directly through the system.

3. Server Features:

 - Client Communication: The server accepts connections from both customer and receptionist clients, handles login requests, room booking, and reservation management.
 - Reservation Data Management: It handles database queries for fetching active reservations, room statuses, and transaction logs.
 - Transaction Tracking: The server logs transactions for actions like booking, cancellation, login, logout, and other reservation updates.
 - Dynamic Data Handling: The server dynamically responds to client requests like checking room availability, inserting new reservations, and updating room statuses.

4. Database Operations:

 - SQL Queries: The system uses SQL queries to interact with the database to fetch room data, customer reservations, update reservation statuses, and log transactions.
 - Real-Time Updates: The system provides real-time updates for room availability and reservation statuses.

5. Graphical User Interface (GUI):

 - Customer GUI: A customer can interact with a user-friendly interface to log in, book rooms, and manage their reservations.
 - Receptionist GUI: The receptionist has access to a dashboard for managing reservations, confirming, and cancelling them, as well as viewing transaction logs.
 - Server GUI: A server-side interface is provided for administrators to manage and monitor reservations, room statuses, and transaction logs, with options to reload data.

The system is designed for both customer and staff interactions and includes backend communication with a MySQL database to manage hotel room availability, reservations, and transaction logs.
