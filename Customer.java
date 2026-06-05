import java.io.*;
import java.sql.*;
import java.util.*;

class Customer
{

    static final String RESET  = "\u001B[0m";
    static final String RED    = "\u001B[31m";
    static final String GREEN  = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String ORANGE = "\u001B[38;5;208m";
    static final String BLUE   = "\u001B[34m";

    Connection con;
    Scanner sc = new Scanner(System.in);

    int customerId = 0;
    boolean isNormalMember = false;
    boolean isGoldMember=false;
    String password;

    Customer(Connection con)
    {
        this.con = con;
    }

    void customerMenu() throws Exception
    {
        while (true)
        {
            System.out.println(ORANGE + "======================================================================================================================================" + RESET);
            System.out.println(YELLOW + "                                                                CUSTOMER MENU                                                          " + RESET);
            System.out.println(ORANGE + "======================================================================================================================================" + RESET);
            System.out.println("1. Register");
            System.out.println("2. Enroll Membership");
            System.out.println("3. View Flights");
            System.out.println("4. Book Flight");
            System.out.println("5. View My Bookings");
            System.out.println("6. Cancel Booking (Refund)");
            System.out.println("7. Update Profile");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch)
            {
                case 1:
                    registerCustomer();
                    break;

                case 2:
                    System.out.println(YELLOW + "Choose Membership Type:" + RESET);
                    System.out.println("1. Normal Membership");
                    System.out.println("2. Gold Membership");
                    System.out.print("Enter choice: ");
                    int mChoice = sc.nextInt();
                    sc.nextLine();

                    if (mChoice == 1)
                    {
                        Membership normal = new Membership(con);
                        normal.enrollMembership();
                    }
                    else if (mChoice == 2)
                    {
                        GoldMembership gold = new GoldMembership(con);
                        gold.enrollMembership();
                    }
                    else
                    {
                        System.out.println(RED + "Invalid choice." + RESET);
                    }
                    break;

                case 3:
                    viewFlights();
                    break;
                case 4:
                    bookFlight();
                    break;
                case 5:
                    viewMyBookings();
                    break;
                case 6:
                    cancelBooking();
                    break;
                case 7:
                    updateProfile();
                    break;
                case 8:
                    System.out.println(ORANGE + "Thank you for visiting!" + RESET);
                    return;
                default:
                    System.out.println(RED + "Invalid option." + RESET);
            }
        }
    }

    void registerCustomer() throws Exception
    {
        System.out.print("Enter First Name: ");
        String fname = sc.nextLine();
        System.out.print("Enter Last Name: ");
        String lname = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(sc.nextLine().trim());
        boolean cage=validateAge(age);
        if(cage==false)
        {
            System.out.println(RED+"Invalid Age Entered"+RESET);
            return;
        }
        System.out.print("Enter Phone Number (10 digits starting from 9,8,7) : ");
        String phone = sc.nextLine();
        boolean vph=validatePhoneNumber(phone);
        if(vph==false)
        {
            System.out.println(RED+"Invalid phone number , it should be 10 digits staring from 9,8,7"+RESET);
            return;
        }

        System.out.print("Set Password: ");
        password = sc.nextLine();

        System.out.print("Enter path of Govt ID (image/pdf): ");
        String filePath = sc.nextLine();
        File file = new File(filePath);
        if (!file.exists())
        {
            System.out.println(RED + "File not found." + RESET);
            return;
        }
        FileInputStream fis = new FileInputStream(file);

        String sql = "INSERT INTO customers(fname, lname, age, phone, password, govt_id) VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, fname);
        pst.setString(2, lname);
        pst.setInt(3, age);
        pst.setString(4, phone);
        pst.setString(5, password);
        pst.setBinaryStream(6, fis, (int) file.length());
        pst.executeUpdate();

        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next())
        {
            customerId = rs.getInt(1);
        }
        System.out.println(GREEN + " Customer registered with ID: " + customerId + RESET);
    }

    void viewFlights() throws Exception
    {
        String q = "SELECT flight_id, flight_number, flight_name, departure_city, arrival_city, departure_time, arrival_time, duration, economy, premium_economy FROM flights";
        PreparedStatement pst = con.prepareStatement(q);
        ResultSet rs = pst.executeQuery();

        System.out.println(BLUE + "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" + RESET);
        System.out.printf("%-8s %-12s %-20s %-15s %-15s %-15s %-15s %-12s %-12s %-18s\n",
                "ID", "Number", "Name", "From", "To", "Departure", "Arrival", "Duration", "Economy", "Premium Economy");
        System.out.println(BLUE + "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" + RESET);

        while (rs.next())
        {
            System.out.printf("%-8d %-12s %-20s %-15s %-15s %-15s %-15s %-12s %-12.2f %-18.2f\n",
                    rs.getInt("flight_id"),
                    rs.getString("flight_number"),
                    rs.getString("flight_name"),
                    rs.getString("departure_city"),
                    rs.getString("arrival_city"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getString("duration"),
                    rs.getDouble("economy"),
                    rs.getDouble("premium_economy"));
        }
    }

    void bookFlight() throws Exception
    {
        System.out.println("Enter your customer id else enter -1");
        int custID=sc.nextInt();
        int checkcust=0;
        String checksql="select customer_id from customers where customer_id=?";
        PreparedStatement checkpst=con.prepareStatement(checksql);
        checkpst.setInt(1,custID);
        ResultSet checkrs=checkpst.executeQuery();

        while(checkrs.next())
        {
            checkcust=checkrs.getInt("customer_id");
        }

        if(custID==checkcust)
        {
            customerId = checkcust;
            viewFlights();
            System.out.print("Enter Flight ID to book: ");
            int flightId = sc.nextInt();
            sc.nextLine();

            String pq = "SELECT flight_number, flight_name, economy, premium_economy FROM flights WHERE flight_id=?";
            PreparedStatement pp = con.prepareStatement(pq);
            pp.setInt(1, flightId);
            ResultSet pr = pp.executeQuery();

            if (!pr.next())
            {
                System.out.println(RED + "Flight not found." + RESET);
                return;
            }

            String flightNumber = pr.getString("flight_number");
            String flightName = pr.getString("flight_name");
            double eco = pr.getDouble("economy");
            double prem = pr.getDouble("premium_economy");

            System.out.print("Choose class (economy/premium): ");
            String seatClass = sc.nextLine().trim();
            boolean isEconomyClass = seatClass.equalsIgnoreCase("economy");
            double basePrice = isEconomyClass ? eco : prem;
            System.out.println(GREEN + "Price for selected class: ₹" + String.format("%.2f", basePrice) + RESET);

            System.out.print("Enter number of passengers: ");
            int n = Integer.parseInt(sc.nextLine().trim());

            String seatCheckSQL = "SELECT number_of_seats FROM flights WHERE flight_id=?";
            PreparedStatement seatCheckPst = con.prepareStatement(seatCheckSQL);
            seatCheckPst.setInt(1, flightId);
            ResultSet seatRs = seatCheckPst.executeQuery();
            if (seatRs.next())
            {
                int availableSeats = seatRs.getInt("number_of_seats");
                if (n > availableSeats)
                {
                    System.out.println(RED + "❌ NOT AVAILABLE. Only " + availableSeats + " seats left." + RESET);
                    return;
                }
            }

            double totalAmount = 0.0;
            StringBuffer seatNos = new StringBuffer();
            StringBuffer assistance = new StringBuffer();
            int nop = 0;
            String meal = null;

            Set<String> chosenSeats = new HashSet<>();

            for (int i = 1; i <= n; i++)
            {
                System.out.println(YELLOW + "Passenger " + i + " details:" + RESET);

                System.out.print("Name: ");
                String pname = sc.nextLine();
                System.out.print("Age: ");
                int page = Integer.parseInt(sc.nextLine().trim());
                boolean pcage = validateAge(page);
                if (pcage == false)
                {
                    return;
                }
                System.out.print("Category (normal/student/military): ");
                String category = sc.nextLine();
                System.out.print("Seat No: ");
                String seatNo = sc.nextLine();

                if (chosenSeats.contains(seatNo))
                {
                    System.out.println(RED + "❌ Seat " + seatNo + " already chosen in this booking. Pick another." + RESET);
                    i--;
                    continue;
                }

                String checkSeatSQL = "SELECT COUNT(*) FROM bookings WHERE flight_id=? AND FIND_IN_SET(?, seat_no) > 0 AND status='CONFIRMED'";
                PreparedStatement checkSeatPst = con.prepareStatement(checkSeatSQL);
                checkSeatPst.setInt(1, flightId);
                checkSeatPst.setString(2, seatNo);
                ResultSet seatTaken = checkSeatPst.executeQuery();

                if (seatTaken.next() && seatTaken.getInt(1) > 0)
                {
                    System.out.println(RED + " Seat " + seatNo + " is already booked. Choose another seat." + RESET);
                    i--; // redo this passenger’s input
                    continue;
                }

                chosenSeats.add(seatNo);

                System.out.print("Any medical conditions (yes/no): ");
                String medical = sc.nextLine();

                if (i > 1) {
                    seatNos.append(",");
                }
                seatNos.append(seatNo);

                double price = basePrice;

                boolean needsAssist = false;
                if (medical.equalsIgnoreCase("yes") || page < 18 || page > 60) {
                    price += price * 0.10;
                    needsAssist = true;
                }
                if (category.equalsIgnoreCase("student") || category.equalsIgnoreCase("military"))
                {
                    price -= price * 0.10;
                }

                if (isNormalMember == true)
                {
                    price -= price * 0.15;
                }
                else if(isGoldMember==true)
                {
                    price -= price * 0.25;
                }

                System.out.print("Need special assistance? (y/n): ");
                String ans = sc.nextLine().trim();
                String assistDetail;

                if (ans.equalsIgnoreCase("y"))
                {
                    System.out.print("Enter assistance details: ");
                    assistDetail = sc.nextLine().trim();
                    if (assistDetail.isEmpty()) {
                        assistDetail = "General Assistance";
                    }
                } else {
                    assistDetail = "N/A";
                }

                if (i > 1) {
                    assistance.append(",");
                }
                assistance.append("P").append(i).append(":").append(assistDetail);

                meal = selectMeal(isEconomyClass ? "ECONOMY" : "PREMIUM");

                totalAmount += price;
                nop = i;
            }

            String updateSeatsSQL = "UPDATE flights SET number_of_seats = number_of_seats - ? WHERE flight_id=?";
            PreparedStatement updateSeatsPst = con.prepareStatement(updateSeatsSQL);
            updateSeatsPst.setInt(1, n);       // reduce by number of passengers booked
            updateSeatsPst.setInt(2, flightId);
            updateSeatsPst.executeUpdate();

            if (isNormalMember == true)
            {
                PreparedStatement addMiles = con.prepareStatement("UPDATE membership SET miles = miles + 1000 WHERE customer_id=?");
                addMiles.setInt(1, customerId);
                addMiles.executeUpdate();
            }
            else if(isGoldMember==true)
            {
                PreparedStatement addGMiles = con.prepareStatement("UPDATE membership SET miles = miles + 2000 WHERE customer_id=?");
                addGMiles.setInt(1, customerId);
                addGMiles.executeUpdate();
            }

            String pnr = generateDigits(6);

            System.out.println(BLUE + "Total amount to pay: ₹" + String.format("%.2f", totalAmount) + RESET);
            System.out.print("Confirm booking? (yes/no): ");
            String conf = sc.nextLine();
            if (!conf.equalsIgnoreCase("yes"))
            {
                System.out.println(RED + "Booking cancelled by user." + RESET);
                return;
            }

            String insertBooking = "INSERT INTO bookings(customer_id, flight_id, pnr, seat_no, services, assistance,meals, check_in_status, status, amount, booking_time) VALUES(?, ?, ?, ?, ?, ?,?, 'NOT_CHECKED_IN', 'PENDING', ?, NOW())";
            PreparedStatement pst = con.prepareStatement(insertBooking);
            pst.setInt(1, customerId);
            pst.setInt(2, flightId);
            pst.setString(3, pnr);
            pst.setString(4, seatNos.toString());
            pst.setString(5, isEconomyClass ? "ECONOMY" : "PREMIUM");
            pst.setString(6, assistance.toString());
            pst.setString(7, meal);
            pst.setDouble(8, totalAmount);
            pst.executeUpdate();
            int bookingId = -1;
            String sql1 = "select booking_id from bookings where customer_id=?";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setInt(1, customerId);
            ResultSet gk = pst1.executeQuery();
            if (gk.next())
            {
                bookingId = gk.getInt(1);
            }

            boolean paid = makePayment(bookingId, totalAmount, pnr);
            if (paid)
            {
                PreparedStatement updSeats = con.prepareStatement("UPDATE flights SET number_of_seats = number_of_seats - ? WHERE flight_id=?");
                updSeats.setInt(1, n);
                updSeats.setInt(2, flightId);
                updSeats.executeUpdate();

                PreparedStatement upd = con.prepareStatement("UPDATE bookings SET status='CONFIRMED' WHERE booking_id=?");
                upd.setInt(1, bookingId);
                upd.executeUpdate();
                System.out.println(GREEN + " Booking confirmed. PNR: " + pnr + RESET);
                exportTicket(pnr, bookingId, flightNumber, flightName, seatNos.toString(), totalAmount, isEconomyClass ? "ECONOMY" : "PREMIUM", meal, nop);
            }
            else
            {
                PreparedStatement upd = con.prepareStatement("UPDATE bookings SET status='FAILED' WHERE booking_id=?");
                upd.setInt(1, bookingId);
                upd.executeUpdate();
                System.out.println(RED + " Payment failed. Booking marked as FAILED." + RESET);
            }
        }
        else if (custID == -1)
        {
            System.out.println(RED + "Please register/login first." + RESET);
            return;
        }
        else
        {
            System.out.println(RED + "Please register/login first." + RESET);
            return;
        }
    }

    boolean makePayment(int bookingId, double totalAmount, String pnr) throws Exception
    {
        System.out.println(YELLOW + "Select Payment Method:" + RESET);
        System.out.println("1. UPI");
        System.out.println("2. Card");
        System.out.println("3. Net Banking");
        System.out.print("Enter choice: ");
        int method = Integer.parseInt(sc.nextLine().trim());

        String methodStr;
        if (method == 1)
        {
            methodStr = "UPI";
            System.out.print("Enter UPI ID (e.g., name@bank): ");
            String upiId = sc.nextLine();
        }
        else if (method == 2)
        {
            methodStr = "CARD";
            System.out.print("Enter Card Number (16 digits) : ");
            String card = sc.nextLine();
            if (card.length() != 16)
            {
                System.out.println(RED + "Invalid Card Number. Must be exactly 16 digits." + RESET);
                return false;
            }
            for (char c : card.toCharArray())
            {
                if (!Character.isDigit(c))
                {
                    System.out.println(RED + "Card Number must contain only digits." + RESET);
                    return false;
                }
            }

            System.out.print("Enter Name on Card: ");
            String name = sc.nextLine();

            System.out.print("Enter Expiry (MM/YY foramt only): ");
            String exp = sc.nextLine();
            if (exp.length() != 5 || exp.charAt(2) != '/')
            {
                System.out.println(RED + "Invalid Expiry format. Use MM/YY." + RESET);
                return false;
            }

            System.out.print("Enter CVV (3 digits): ");
            String cvv = sc.nextLine();
            if (cvv.length() != 3)
            {
                System.out.println(RED + "Invalid CVV. Must be 3 digits." + RESET);
                return false;
            }
            for (char c : cvv.toCharArray())
            {
                if (!Character.isDigit(c))
                {
                    System.out.println(RED + "CVV must contain only digits." + RESET);
                    return false;
                }
            }
        }
        else if (method == 3)
        {
            methodStr = "NET BANKING";

            System.out.print("Bank Name: ");
            String bank = sc.nextLine();

            System.out.print("Account/User ID (10 digits): ");
            String acc = sc.nextLine();
            if (acc.length() != 10)
            {
                System.out.println(RED + "Invalid Account/User ID. Must be exactly 10 digits." + RESET);
                return false;
            }
            for (char c : acc.toCharArray())
            {
                if (!Character.isDigit(c))
                {
                    System.out.println(RED + "Account/User ID must contain only digits." + RESET);
                    return false;
                }
            }
        }
        else
        {
            System.out.println(RED + "Invalid payment method." + RESET);
            return false;
        }

        String otp = generateDigits(6);
        System.out.println(BLUE + "Generated OTP: " + otp + RESET);
        System.out.print("Enter OTP: ");
        String entered = sc.nextLine().trim();

        if (!otp.equals(entered))
        {
            System.out.println(RED + "OTP Incorrect!" + RESET);
            return false;
        }
        else
        {
            String txRef = "TXN" + pnr + generateDigits(3);
            PreparedStatement pstPay = con.prepareStatement("INSERT INTO payments(booking_id, amount, method, status, tx_ref, payment_time) VALUES(?, ?, ?, ?, ?, NOW())");
            pstPay.setInt(1, bookingId);
            pstPay.setDouble(2, totalAmount);
            pstPay.setString(3, methodStr);
            pstPay.setString(4, "SUCCESS");
            pstPay.setString(5, txRef);
            pstPay.executeUpdate();

            System.out.println(GREEN + " Payment Successful! TxRef: " + txRef + RESET);
            return true;
        }
    }

    void viewMyBookings() throws Exception
    {
        if (customerId == -1)
        {
            System.out.println(RED + "You must register/login first!" + RESET);
            return;
        }

        String sql = "SELECT b.booking_id, b.pnr, b.flight_id, b.services AS seat_class, b.seat_no, " +
                "b.assistance, b.status, b.amount, b.booking_time, " +
                "f.departure_time, f.arrival_time, f.duration, f.departure_city, f.arrival_city " +
                "FROM bookings b " +
                "JOIN flights f ON b.flight_id = f.flight_id " +
                "WHERE b.customer_id=? " +
                "ORDER BY b.booking_time DESC";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, customerId);
        ResultSet rs = pst.executeQuery();

        System.out.println(BLUE + "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" + RESET);
        System.out.printf("%-6s %-10s %-9s %-10s %-18s %-12s %-12s %-10s %-20s %-12s %-12s %-10s %-15s %-15s\n",
                "ID","PNR","FlightID","Class","Seats","Assist","Status","Amount","BookedAt",
                "Departure","Arrival","Duration","From","To");
        System.out.println(BLUE + "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" + RESET);

        while (rs.next())
        {
            System.out.printf("%-6d %-10s %-9d %-10s %-18s %-12s %-12s %-10.2f %-20s %-12s %-12s %-10s %-15s %-15s\n",
                    rs.getInt("booking_id"),
                    rs.getString("pnr"),
                    rs.getInt("flight_id"),
                    rs.getString("seat_class"),
                    rs.getString("seat_no"),
                    rs.getString("assistance"),
                    rs.getString("status"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("booking_time"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getString("duration"),
                    rs.getString("departure_city"),
                    rs.getString("arrival_city"));
        }
    }

    void cancelBooking() throws Exception
    {
        if (customerId == -1)
        {
            System.out.println(RED + "You must register/login first!" + RESET);
            return;
        }

        System.out.print("Enter your PNR to cancel booking: ");
        String pnr = sc.nextLine();

        String sqlFind = "SELECT booking_id, amount, booking_time, status FROM bookings WHERE pnr=? AND customer_id=?";
        PreparedStatement pstFind = con.prepareStatement(sqlFind);
        pstFind.setString(1, pnr);
        pstFind.setInt(2, customerId);
        ResultSet rs = pstFind.executeQuery();

        if (!rs.next())
        {
            System.out.println(RED + " No booking found with this PNR!" + RESET);
            return;
        }

        int bookingId = rs.getInt("booking_id");
        double amount = rs.getDouble("amount");
        Timestamp bookingTime = rs.getTimestamp("booking_time");
        String status = rs.getString("status");

        if (!"CONFIRMED".equalsIgnoreCase(status))
        {
            System.out.println(RED + "⚠️ Booking is not CONFIRMED (current: " + status + "). Cannot cancel." + RESET);
            return;
        }

        long millis = System.currentTimeMillis() - bookingTime.getTime();
        long hours = millis / (1000 * 60 * 60);
        double refundAmount;
        if (hours <= 24)
        {
            refundAmount = amount;
        }
        else
        {
            refundAmount = amount * 0.80;
        }

        PreparedStatement pstUpdate = con.prepareStatement("UPDATE bookings SET status='CANCELLED' WHERE booking_id=?");
        pstUpdate.setInt(1, bookingId);
        pstUpdate.executeUpdate();

        PreparedStatement pstRefund = con.prepareStatement("INSERT INTO payments(booking_id, amount, method, status, tx_ref, payment_time) VALUES(?, ?, 'REFUND', 'SUCCESS', ?, NOW())");
        pstRefund.setInt(1, bookingId);
        pstRefund.setDouble(2, -refundAmount);
        pstRefund.setString(3, "REFUND" + pnr + generateDigits(2));
        pstRefund.executeUpdate();

        System.out.println(GREEN + " Booking Cancelled! Refund of ₹" + String.format("%.2f", refundAmount) + " processed." + RESET);

        System.out.print("Do you want a cancellation receipt (txt)? (yes/no): ");
        String opt = sc.nextLine();
        if (opt.equalsIgnoreCase("yes"))
        {
            String filename = pnr + "_CANCEL.txt";
            FileWriter fw = new FileWriter(filename);
            fw.write("PNR: " + pnr + "\n");
            fw.write("Booking ID: " + bookingId + "\n");
            fw.write("Refund Amount: " + String.format("%.2f", refundAmount) + "\n");
            fw.write("Refund Status: SUCCESS\n");
            fw.flush();
            fw.close();
            System.out.println(" Cancellation receipt saved as " + filename);
        }
    }

    void updateProfile() throws Exception
    {
        if (customerId == -1)
        {
            System.out.println(RED + "You must register/login first!" + RESET);
            return;
        }
        System.out.print("Enter new phone number (10 digits stating 9,8,7) : ");
        String newPhone = sc.nextLine();
        boolean vph=validatePhoneNumber(newPhone);
        if(vph==false)
        {
            System.out.println(RED+"Invalid phone number , it should be 10 digits staring from 9,8,7"+RESET);
            return;
        }
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();
        String sql = "UPDATE customers SET phone=?, password=? WHERE customer_id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, newPhone);
        pst.setString(2, newPass);
        pst.setInt(3, customerId);
        pst.executeUpdate();
        System.out.println(GREEN + " Profile updated." + RESET);
    }

    String generateDigits(int n)
    {
        StringBuffer digits = new StringBuffer();
        for (int i = 0; i < n; i++)
        {
            digits.append((int)(Math.random() * 10));
        }
        return digits.toString();
    }

    void exportTicket(String pnr, int bookingId, String flightNumber, String flightName, String seats, double amount, String seatClass,String mealchoice,int nop) throws Exception
    {
        System.out.print("Download ticket as txt? (yes/no) : ");
        String opt = sc.nextLine();
        if (opt.equalsIgnoreCase("yes"))
        {
            String sql = "SELECT departure_city, arrival_city, duration, departure_time, arrival_time " +
                    "FROM flights WHERE flight_number = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, flightNumber);
            ResultSet rs = pst.executeQuery();

            String departureCity = "", arrivalCity = "", duration = "", departureTime = "", arrivalTime = "";
            if (rs.next())
            {
                departureCity = rs.getString("departure_city");
                arrivalCity = rs.getString("arrival_city");
                duration = rs.getString("duration");
                departureTime = rs.getString("departure_time");
                arrivalTime = rs.getString("arrival_time");
            }

            String filename = pnr + ".txt";
            FileWriter fw = new FileWriter(filename);
            fw.write("============== E-TICKET ==============\n");
            fw.write("PNR              : " + pnr + "\n");
            fw.write("No.of Passengers : " + nop + "\n");
            fw.write("Booking ID       : " + bookingId + "\n");
            fw.write("Customer ID      : " + customerId + "\n");
            fw.write("Flight           : " + flightNumber + " - " + flightName + "\n");
            fw.write("From             : " + departureCity + "\n");
            fw.write("To               : " + arrivalCity + "\n");
            fw.write("Departure Time   : " + departureTime + "\n");
            fw.write("Arrival Time     : " + arrivalTime + "\n");
            fw.write("Duration         : " + duration + "\n");
            fw.write("Class            : " + seatClass + "\n");
            fw.write("Seats            : " + seats + "\n");
            fw.write("Meals            : " + mealchoice + "\n");
            fw.write("Amount Paid      : ₹" + String.format("%.2f", amount) + "\n");
            fw.write("Status           : CONFIRMED\n");
            fw.write("=====================================\n");
            fw.flush();
            fw.close();
            System.out.println(" Ticket saved as " + filename);
        }
        else
        {
            return;
        }
    }

    String selectMeal(String seatClass) {
        System.out.print("Are you lactose intolerant? (yes/no): ");
        String lactose = sc.nextLine().trim();
        if (lactose.equalsIgnoreCase("yes")) {
            System.out.println(GREEN + "Assigned meal: NLML (Non-Lactose Meal)" + RESET);
            return "NLML";
        }

        System.out.print("Do you have any food allergies? (yes/no): ");
        String allergy = sc.nextLine().trim();
        if (allergy.equalsIgnoreCase("yes")) {
            System.out.print("Enter allergen (e.g., Nuts, Gluten, Seafood): ");
            String allergen = sc.nextLine().trim();
            String warning = "ALLERGY:" + allergen.toUpperCase();
            System.out.println(RED + " Warning: Passenger allergic to " + allergen + RESET);
            return warning;
        }

        String mealCode = "";
        if (seatClass.equalsIgnoreCase("ECONOMY")) {
            System.out.println(YELLOW + "Select Meal Option (Economy):" + RESET);
            System.out.println("1. Vegetarian Meal (VML)");
            System.out.println("2. Non-Vegetarian Meal (NVML)");
            System.out.println("3. Vegan Meal (VGML)");
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(sc.nextLine().trim());
            switch (choice) {
                case 1: mealCode = "VML"; break;
                case 2: mealCode = "NVML"; break;
                case 3: mealCode = "VGML"; break;
                default:
                    System.out.println(RED + "Invalid choice. Defaulting to VML." + RESET);
                    mealCode = "VML";
            }
        } else if (seatClass.equalsIgnoreCase("PREMIUM")) {
            System.out.println(YELLOW + "Select Meal Option (Premium Economy):" + RESET);
            System.out.println("1. Asian Vegetarian Meal (AVML)");
            System.out.println("2. Hindu Vegetarian Meal (HVML)");
            System.out.println("3. Asian Non-Vegetarian Meal (ANML)");
            System.out.println("4. Hindu Non-Vegetarian Meal (HNML)");
            System.out.println("5. Vegan Meal (VGML)");
            System.out.println("6. Diabetic Meal (DBML)");
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(sc.nextLine().trim());
            switch (choice) {
                case 1: mealCode = "AVML"; break;
                case 2: mealCode = "HVML"; break;
                case 3: mealCode = "ANML"; break;
                case 4: mealCode = "HNML"; break;
                case 5: mealCode = "VGML"; break;
                case 6: mealCode = "DBML"; break;
                default:
                    System.out.println(RED + "Invalid choice. Defaulting to AVML." + RESET);
                    mealCode = "AVML";
            }
        }
        System.out.println(GREEN + "Assigned meal: " + mealCode + RESET);
        return mealCode;
    }

    boolean validatePhoneNumber(String phone)
    {
        if (phone.length() != 10)
        {
            return false;
        }

        char first = phone.charAt(0);
        if (first != '9' && first != '8' && first != '7')
        {
            return false;
        }

        for (int i = 0; i < phone.length(); i++)
        {
            char c = phone.charAt(i);
            if (c < '0' || c > '9')
            {
                return false;
            }
        }
        return true;
    }

    boolean validateAge(int age)
    {
        if(age<=0 || age>100)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}