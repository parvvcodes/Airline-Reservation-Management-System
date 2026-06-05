
⸻

✈️ Airline Reservation and Management System (ARMS)

A Java + Data Structures + DBMS console-based system that simulates a real-world airline booking platform.
This project demonstrates integration of Java backend logic with MySQL database (via XAMPP).

⸻

🚀 Features
	•	👤 Customer Registration & Login
	•	🛫 Flight Search & Booking
	•	🎟️ PNR Generation & Ticket Booking
	•	💺 Seat Selection
	•	🍽️ Extra Services (Meal, Assistance, etc.)
	•	💳 Payment Simulation
	•	🏆 Membership System (Miles-based rewards)
	•	🛠️ Admin Login & Flight Management

⸻

🛠️ Tech Stack
	•	Language: Java
	•	Concepts Used: OOP, Data Structures
	•	Database: MySQL
	•	Server: XAMPP (Apache + MySQL)
	•	Connectivity: JDBC

⸻

📂 Project Structure

├── Main.java
├── Admin.java
├── Customer.java
├── Membership.java
├── GoldMembership.java
└── (Database handled via MySQL)

⸻

⚙️ Setup Instructions

1️⃣ Install Requirements
	•	Install XAMPP
	•	Install Java (JDK 8 or above)
	•	Install any IDE (IntelliJ / Eclipse / VS Code)

⸻

2️⃣ Start Database Server
	1.	Open XAMPP Control Panel
	2.	Start:
	•	✅ Apache
	•	✅ MySQL

⸻

3️⃣ Create Database

Open phpMyAdmin → Create a database:

CREATE DATABASE airline_db;
USE airline_db;

⸻

4️⃣ Run SQL Queries (IMPORTANT)

Copy and run the following queries in order inside phpMyAdmin SQL tab:

⸻

🔹 Admin Table

CREATE TABLE adminlog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

INSERT INTO adminlog (username, password) VALUES
('admin_1', 'admin@123'),
('supervisor_32', 'super_21');

⸻

🔹 Flights Table

CREATE TABLE flights (
 flight_id INT AUTO_INCREMENT PRIMARY KEY,
 flight_number VARCHAR(100) UNIQUE NOT NULL,
 flight_name VARCHAR(100) NOT NULL,
 departure_city VARCHAR(50) NOT NULL,
 arrival_city VARCHAR(50) NOT NULL,
 number_of_seats INT NOT NULL,
 duration TIME NOT NULL,
 departure_time TIME NOT NULL,
 arrival_time TIME NOT NULL,
 aircraft_type VARCHAR(50) NOT NULL,
 economy DECIMAL(10,2) NOT NULL CHECK (economy BETWEEN 5000 AND 10000),
 premium_economy DECIMAL(10,2) NOT NULL,
 CHECK (premium_economy = economy * 1.3)
);


⸻

🔹 Insert Flight Data

INSERT INTO flights (
    flight_number, flight_name, departure_city, arrival_city,
    number_of_seats, duration, departure_time, arrival_time,
    aircraft_type, economy, premium_economy
) VALUES
('AI101', 'Air India', 'Delhi', 'Mumbai', 180, '02:15:00', '10:00:00', '12:15:00', 'Airbus A320', 8000.00, 10400.00),
('6E202', 'Indigo', 'Bangalore', 'Chennai', 149, '01:00:00', '09:00:00', '10:00:00', 'ATR 72', 6000.00, 7800.00),
('QP303', 'Akasa', 'Hyderabad', 'Pune', 160, '01:30:00', '11:30:00', '13:00:00', 'Boeing 737', 7000.00, 9100.00),
('AI202', 'Air India', 'Kolkata', 'Delhi', 200, '02:30:00', '08:00:00', '10:30:00', 'Boeing 787', 9000.00, 11700.00),
('AI303', 'Air India', 'Mumbai', 'Chennai', 170, '01:45:00', '14:00:00', '15:45:00', 'Airbus A321', 7500.00, 9750.00),
('6E404', 'Indigo', 'Delhi', 'Goa', 180, '02:30:00', '06:30:00', '09:00:00', 'Airbus A320', 8500.00, 11050.00),
('6E505', 'Indigo', 'Ahmedabad', 'Pune', 160, '01:15:00', '17:00:00', '18:15:00', 'ATR 72', 6500.00, 8450.00),
('QP606', 'Akasa', 'Delhi', 'Lucknow', 140, '01:00:00', '07:00:00', '08:00:00', 'Boeing 737', 5500.00, 7150.00),
('QP707', 'Akasa', 'Bangalore', 'Kolkata', 190, '02:45:00', '20:00:00', '22:45:00', 'Boeing 737 MAX', 9500.00, 12350.00),
('6E808', 'Indigo', 'Chennai', 'Hyderabad', 155, '01:20:00', '13:00:00', '14:20:00', 'Airbus A320neo', 7300.00, 9490.00);

⸻

🔹 Customers Table

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    fname VARCHAR(50) NOT NULL,
    lname VARCHAR(50) NOT NULL,
    age INT CHECK (age > 0),
    phone VARCHAR(15) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    govt_id LONGBLOB NOT NULL
);

⸻

🔹 Bookings Table

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    pnr VARCHAR(10) NOT NULL UNIQUE,
    seat_no VARCHAR(10),
    services VARCHAR(100),
    assistance BOOLEAN DEFAULT FALSE,
    check_in_status VARCHAR(20) DEFAULT 'NOT CHECKED IN',
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    amount DECIMAL(10,2) NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE
);

⸻

🔹 Membership Table

CREATE TABLE membership (
    membership_id VARCHAR(10) PRIMARY KEY,
    customer_id INT NOT NULL,
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    miles INT DEFAULT 0,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);
⸻
🔹 Payments Table

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(20),
    status VARCHAR(20),
    tx_ref VARCHAR(20) UNIQUE,
    payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);
⸻

▶️ How to Run the Project
	1.	Setup database (above steps)
	2.	Open project in IDE
	3.	Update DB credentials in Java code:

String url = "jdbc:mysql://localhost:3306/airline_db";
String user = "root";
String password = "";

	4.	Run:

Main.java


⸻

🔐 Default Admin Login

Username: admin_1
Password: admin@123

⸻

📌 Important Notes
	•	Ensure MySQL is running before starting the project
	•	Tables must be created in correct order (due to foreign keys)
	•	If XAMPP crashes, just re-run the SQL queries
	•	Passwords are stored in plain text (can be improved using hashing)

⸻

📈 Future Enhancements
	•	GUI (JavaFX / Swing)
	•	Online payment gateway integration
	•	Ticket PDF generation
	•	Real-time seat tracking
	•	REST API integration

⸻

📜 License

This project is created for educational purposes only.
Not intended for commercial use.

⸻
