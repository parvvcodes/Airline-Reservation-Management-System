import java.util.*;
import java.sql.*;
import java.time.*;

class FlightNode
{
    String flightNumber;
    String flightName;
    int flightId;
    FlightNode left, right;

    public FlightNode(int flightId, String flightNumber, String flightName)
    {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.flightName = flightName;
        this.left = this.right = null;
    }
}

class FlightBST
{
    FlightNode root;

    FlightNode insert(FlightNode root, int flightId, String flightNumber, String flightName)
    {
        if (root == null)
        {
            return new FlightNode(flightId, flightNumber, flightName);
        }

        if (flightNumber.compareTo(root.flightNumber) < 0)
        {
            root.left = insert(root.left, flightId, flightNumber, flightName);
        }
        else if (flightNumber.compareTo(root.flightNumber) > 0)
        {
            root.right = insert(root.right, flightId, flightNumber, flightName);
        }
        return root;
    }

    FlightNode search(FlightNode root, String flightNumber)
    {
        if (root == null || root.flightNumber.equals(flightNumber))
        {
            return root;
        }
        if (flightNumber.compareTo(root.flightNumber) < 0)
        {
            return search(root.left, flightNumber);
        }
        return search(root.right, flightNumber);
    }

    void printInOrder(FlightNode root)
    {
        if (root != null)
        {
            printInOrder(root.left);
            System.out.println("[" + root.flightNumber + "] " + root.flightName + " (ID: " + root.flightId + ")");
            printInOrder(root.right);
        }
    }

    FlightNode delete(FlightNode root, int flightId)
    {
        if (root == null)
        {
            return null;
        }

        if (flightId < root.flightId)
        {
            root.left = delete(root.left, flightId);
        }
        else if (flightId > root.flightId)
        {
            root.right = delete(root.right, flightId);
        }
        else
        {
            if (root.left == null)
            {
                return root.right;
            }
            else if (root.right == null)
            {
                return root.left;
            }

            FlightNode successor = minValueNode(root.right);
            root.flightId = successor.flightId;
            root.flightNumber = successor.flightNumber;
            root.flightName = successor.flightName;

            root.right = delete(root.right, successor.flightId);
        }
        return root;
    }

    FlightNode minValueNode(FlightNode node)
    {
        FlightNode current = node;
        while (current.left != null)
        {
            current = current.left;
        }
        return current;
    }
}

class Admin
{
    static FlightBST flightTree = new FlightBST();
    public static final String RESET  = "\u001B[0m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String ORANGE = "\u001B[38;5;208m";
    public static final String BLUE   = "\u001B[34m";
    static String user=null;
    static String pass=null;
    static String cpass=null;
    static String upass=null;
    void login()
    {
        Scanner sc = new Scanner(System.in);
        String dburl = "jdbc:mysql://localhost:3306/arms_trial";
        String dbuser = "root";
        String dbpass = "";
        System.out.println(ORANGE + "======================================================================================================================================" + RESET);
        System.out.println(YELLOW + "                                                             ADMIN Logins                                                                    " + RESET);
        System.out.println(ORANGE+"======================================================================================================================================" +RESET);
        int attempts = 0;
        int c=2;
        boolean success = false;

        while (attempts < 3)
        {
            if (attempts > 0)
            {
                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    System.out.println(" Sleep interrupted.");
                }
            }

            System.out.print("Enter your Username: ");
            user = sc.nextLine();
            System.out.print("Enter Password: ");
            pass = sc.nextLine();
            try
            {
                Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
                String query = "SELECT * FROM adminlog WHERE username = ? AND password = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, user);
                stmt.setString(2, pass);
                ResultSet rs = stmt.executeQuery();

                if (rs.next())
                {
                    cpass=pass;
                    System.out.println(GREEN + " Welcome, " + user + ". You have successfully logged in." + RESET);
                    success = true;
                    con.close();
                    break;
                }
                else
                {
                    System.out.println("\u001B[34m Hold on... preparing the next login attempt "+(c)+" Remaining \u001B[0m");
                    attempts++;
                    c--;
                    con.close();
                }
            }
            catch (SQLException e)
            {
                System.out.println(RED + " Database error: " + e.getMessage() + RESET);
                System.exit(0);
            }
        }
        if (!success)
        {
            System.out.println(RED + " Too many failed attempts. Access denied." + RESET);
            System.exit(0);
        }
        try
        {
            Thread.sleep(2500);
            System.out.println();
            System.out.println();
        }
        catch (InterruptedException e)
        {
            System.out.println(" Sleep interrupted.");
        }
    }

    void Booking() throws Exception
    {

        while(true)
        {
            Scanner sc=new Scanner(System.in);
            System.out.println(ORANGE + "=======================================================================================================================================" + RESET);
            System.out.println(YELLOW + "                                                                ADMIN MENU                                                      " + RESET);
            System.out.println(ORANGE + "=======================================================================================================================================" + RESET);

            System.out.println("Press 1 to ADD flight.");
            System.out.println("Press 2 to DELETE flight.");
            System.out.println("Press 3 to UPDATE flight.");
            System.out.println("Press 4 to SEARCH flights.");
            System.out.println("Press 5 to VIEW ALL flights.");
            System.out.println("Press 6 to VIEW ALL Customers");
            System.out.println("Press 7 to VIEW ALL Members");
            System.out.println("Press 8 to VIEW ALL bookings");
            System.out.println("Press 9 to VIEW ALL payments");
            System.out.println("Press 10 to exit");
            System.out.print("Enter your Choice : ");
            int ch=sc.nextInt();

            sc.nextLine();

            Connection con=null;
            try
            {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/arms_trial","root","");

                if (flightTree.root == null)
                {
                    String loadSql = "SELECT flight_id, flight_number, flight_name FROM flights";
                    PreparedStatement loadStmt = con.prepareStatement(loadSql);
                    ResultSet loadRs = loadStmt.executeQuery();
                    while (loadRs.next())
                    {
                        int id = loadRs.getInt("flight_id");
                        String num = loadRs.getString("flight_number");
                        String name = loadRs.getString("flight_name");
                        flightTree.root = flightTree.insert(flightTree.root, id, num, name);
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println(RED + " ERROR: Unable to connect to the database. Check configuration and network status." + RESET);
                System.exit(0);
            }

            con.setAutoCommit(false);
            switch (ch)
            {
                case 1:
                    System.out.println(ORANGE + "=====================================================================================================================================" + RESET);
                    System.out.println(YELLOW + "                                                                ADD FLIGHT                                                      " + RESET);
                    System.out.println(ORANGE + "=====================================================================================================================================" + RESET);

                    System.out.print("Enter flight_number : ");
                    String f_num = sc.nextLine();

                    System.out.print("Enter flight_name : ");
                    String f_name = sc.nextLine();

                    System.out.print("Enter departure_city : ");
                    String d_city = sc.nextLine();

                    System.out.print("Enter arrival_city : ");
                    String a_city = sc.nextLine();

                    System.out.print("Enter number_of_seats : ");
                    int num_of_seats = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter duration (HH:MM:SS) : ");
                    String duration = sc.nextLine();

                    System.out.print("Enter departure_time (HH:MM:SS) : ");
                    String d_time = sc.nextLine();

                    java.time.LocalTime departureTime = java.time.LocalTime.parse(d_time);
                    java.time.Duration dur = java.time.Duration.between(java.time.LocalTime.MIN, java.time.LocalTime.parse(duration));
                    java.time.LocalTime arrivalTime = departureTime.plus(dur);
                    String a_time = arrivalTime.toString();

                    System.out.print("Enter aircraft_type : ");
                    String aircraft_type = sc.nextLine();

                    System.out.print("Enter Economy Price : ");
                    double eprice = sc.nextDouble();
                    sc.nextLine();

                    System.out.println("Calculating Premium Economy Price (30%) greater...");
                    double peprice = eprice + (eprice * 0.3);

                    String sql1 = "INSERT INTO flights (flight_number, flight_name, departure_city, arrival_city, number_of_seats, duration, departure_time, arrival_time, aircraft_type, economy, premium_economy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pst1 = con.prepareStatement(sql1);

                    pst1.setString(1, f_num);
                    pst1.setString(2, f_name);
                    pst1.setString(3, d_city);
                    pst1.setString(4, a_city);
                    pst1.setInt(5, num_of_seats);
                    pst1.setString(6, duration);
                    pst1.setString(7, d_time);
                    pst1.setString(8, a_time);
                    pst1.setString(9, aircraft_type);
                    pst1.setDouble(10, eprice);
                    pst1.setDouble(11, peprice);

                    int r = pst1.executeUpdate();
                    System.out.println("Enter your password");
                    upass = sc.next();
                    if (cpass.equals(upass))
                    {
                        con.commit();
                        if (r > 0)
                        {
                            String idQuery = "SELECT flight_id FROM flights WHERE flight_number = ?";
                            PreparedStatement idStmt = con.prepareStatement(idQuery);
                            idStmt.setString(1, f_num);
                            ResultSet rsId = idStmt.executeQuery();
                            if (rsId.next()) {
                                int fid = rsId.getInt("flight_id");
                                flightTree.root = flightTree.insert(flightTree.root, fid, f_num, f_name);
                            }
                            System.out.println(GREEN + " Flight details successfully added to the system." + RESET);
                        }
                    }
                    else
                    {
                        con.rollback();
                        System.out.println(RED + " Failed to insert flight details. Please verify the password." + RESET);
                        System.out.println();
                    }
                    break;

                case 2:
                    System.out.println(ORANGE + "=====================================================================================================================================" + RESET);
                    System.out.println(YELLOW + "                                                           DELETE FLIGHT                                                  " + RESET);
                    System.out.println(ORANGE + "=====================================================================================================================================" + RESET);

                    System.out.print("Enter Id to be Deleted : ");
                    int id = sc.nextInt();
                    String sql2 = "Delete from flights where flight_id = ?";
                    PreparedStatement pst2 = con.prepareStatement(sql2);
                    pst2.setInt(1, id);
                    int r1 = pst2.executeUpdate();
                    System.out.println("Enter your password");
                    upass = sc.next();
                    if (cpass.equals(upass))
                    {
                        con.commit();
                        if (r1 > 0)
                        {

                            flightTree.root = flightTree.delete(flightTree.root, id);
                            System.out.println(GREEN + " Flight with ID " + id + " has been successfully deleted from the system." + RESET);
                        }
                        else
                        {
                            System.out.println(RED + " No flight found with ID " + id + ". Deletion failed or ID does not exist." + RESET);
                        }
                    }
                    else
                    {
                        con.rollback();
                        System.out.println(RED + " No flight found with ID " + id + ". Deletion failed or ID does not exist." + RESET);
                    }
                    System.out.println();
                    break;

                case 3:
                    System.out.println(ORANGE + "=====================================================================================================================================" + RESET);
                    System.out.println(YELLOW + "                                                            UPDATE FLIGHT                                                        " + RESET);
                    System.out.println(ORANGE + "=====================================================================================================================================" + RESET);

                    System.out.println("Press 1 to update Aircraft Type ");
                    System.out.println("Press 2 to update Departure & Arrival Timings");
                    System.out.println("Press 3 to update prices");
                    System.out.print("Enter your choice: ");
                    int uch = sc.nextInt();

                    switch (uch)
                    {
                        case 1:
                            System.out.print("Enter Flight ID to update: ");
                            int updateId = sc.nextInt();
                            sc.nextLine();
                            System.out.print("Enter new Aircraft Type: ");
                            String newAircraftType = sc.nextLine();

                            System.out.print("Enter new Number of Seats: ");
                            int newSeats = sc.nextInt();

                            String updateAircraftSQL = "UPDATE flights SET aircraft_type = ?, number_of_seats = ? WHERE flight_id = ?";
                            PreparedStatement pst = con.prepareStatement(updateAircraftSQL);
                            pst.setString(1, newAircraftType);
                            pst.setInt(2, newSeats);
                            pst.setInt(3, updateId);

                            int rowsAffected = pst.executeUpdate();
                            System.out.println("Enter your password");
                            upass = sc.next();
                            if (cpass.equals(upass))
                            {
                                con.commit();
                                if (rowsAffected > 0)
                                {
                                    System.out.println(GREEN + " Aircraft type and seats updated successfully for Flight ID " + updateId + RESET);
                                    System.out.println();
                                    System.out.println("=========================================================== Updated Details ===========================================================");
                                    System.out.println();
                                    PreparedStatement pst6 = con.prepareStatement("select * from flights where flight_id = ?");
                                    pst6.setInt(1, updateId);
                                    ResultSet urs = pst6.executeQuery();
                                    System.out.println(BLUE + "============================================================================================================================================================================================" + RESET);
                                    System.out.printf("%-10s %-15s %-20s %-20s %-20s %-10s %-20s %-25s %-25s %-20s%n",
                                            "Flight ID", "Flight No", "Flight Name", "Departure City", "Arrival City",
                                            "Seats", "Total Duration", "Departure Time", "Arrival Time", "Aircraft Type");
                                    System.out.println(BLUE + "============================================================================================================================================================================================" + RESET);
                                    while (urs.next())
                                    {
                                        System.out.printf("%-10d %-15s %-20s %-20s %-20s %-10d %-20s %-25s %-25s %-20s%n",
                                                urs.getInt(1),
                                                urs.getString(2),
                                                urs.getString(3),
                                                urs.getString(4),
                                                urs.getString(5),
                                                urs.getInt(6),
                                                urs.getString(7),
                                                urs.getString(8),
                                                urs.getString(9),
                                                urs.getString(10)
                                        );
                                    }
                                    System.out.println(BLUE + "============================================================================================================================================================================================" + RESET);
                                    System.out.println();
                                }
                            }
                            else
                            {
                                con.rollback();
                                System.out.println(RED + " No flight found with ID " + updateId + ". Update failed." + RESET);
                            }
                            System.out.println();
                            break;

                        case 2:
                            System.out.print("Enter Flight ID to update timings: ");
                            int timeUpdateId = sc.nextInt();
                            sc.nextLine();

                            System.out.print("Enter new Departure Time (HH:MM:SS): ");
                            String newDeparture = sc.nextLine();

                            System.out.println("Enter new Duration (HH:mm:ss):");
                            String newduration = sc.next();
                            java.time.LocalTime newdepartureTime = java.time.LocalTime.parse(newDeparture);
                            java.time.LocalTime durationTime = java.time.LocalTime.parse(newduration);
                            long seconds = durationTime.toSecondOfDay();
                            java.time.Duration ndur = java.time.Duration.ofSeconds(seconds);
                            java.time.LocalTime newarrivalTime = newdepartureTime.plus(ndur);
                            String newa_time = newarrivalTime.toString();

                            String updateTimeSQL = "UPDATE flights SET departure_time = ?, arrival_time = ?,duration=? WHERE flight_id = ?";
                            PreparedStatement upst = con.prepareStatement(updateTimeSQL);
                            upst.setString(1, newDeparture);
                            upst.setString(2, newa_time);
                            upst.setString(3,newduration);
                            upst.setInt(4, timeUpdateId);

                            int rowsaffected = upst.executeUpdate();
                            System.out.println("Enter your password");
                            upass = sc.next();
                            if (cpass.equals(upass))
                            {
                                con.commit();
                                if (rowsaffected > 0)
                                {
                                    System.out.println(GREEN + " Timings updated successfully for Flight ID " + timeUpdateId + RESET);
                                    PreparedStatement pst6 = con.prepareStatement("select * from flights where flight_id = ?");
                                    pst6.setInt(1, timeUpdateId);
                                    ResultSet urs = pst6.executeQuery();
                                    System.out.println();
                                    System.out.println("=========================================================== Updated Details ===========================================================");
                                    System.out.println();
                                    System.out.println(BLUE + "============================================================================================================================================================================================" + RESET);
                                    System.out.printf("%-10s %-15s %-20s %-20s %-20s %-10s %-20s %-25s %-25s %-20s%n",
                                            "Flight ID", "Flight No", "Flight Name", "Departure City", "Arrival City",
                                            "Seats", "Total Duration", "Departure Time", "Arrival Time", "Aircraft Type");
                                    System.out.println(BLUE + "============================================================================================================================================================================================" + RESET);
                                    while (urs.next())
                                    {
                                        System.out.printf("%-10d %-15s %-20s %-20s %-20s %-10d %-20s %-25s %-25s %-20s%n",
                                                urs.getInt(1),
                                                urs.getString(2),
                                                urs.getString(3),
                                                urs.getString(4),
                                                urs.getString(5),
                                                urs.getInt(6),
                                                urs.getString(7),
                                                urs.getString(8),
                                                urs.getString(9),
                                                urs.getString(10)
                                        );
                                    }
                                    System.out.println(BLUE + "============================================================================================================================================================================================" + RESET);
                                    System.out.println();
                                }
                            }
                            else
                            {
                                con.rollback();
                                System.out.println(RED + " No flight found with ID " + timeUpdateId + ". Update failed." + RESET);
                            }
                            break;

                        case 3:
                            System.out.print("Enter Flight ID to update: ");
                            int updatePriceId = sc.nextInt();
                            System.out.print("Enter Economy Price : ");
                            double ueprice = sc.nextDouble();
                            double upeprice = ueprice+(ueprice*0.3);
                            sc.nextLine();
                            String sql6 = "update flights set economy = ?,premium_economy=? where flight_id=?";
                            PreparedStatement pst6 = con.prepareStatement(sql6);
                            pst6.setDouble(1,ueprice);
                            pst6.setDouble(2,upeprice);
                            pst6.setInt(3,updatePriceId);
                            int r6 = pst6.executeUpdate();

                            System.out.println("Enter your password");
                            upass = sc.next();
                            if (cpass.equals(upass))
                            {
                                con.commit();
                                if (r6 > 0)
                                {
                                    System.out.println(GREEN + " Flight details successfully added to the system." + RESET);
                                }
                            }
                            else
                            {
                                con.rollback();
                                System.out.println(RED + " Failed to insert flight details. Please verify the password." + RESET);
                            }
                            break;

                        default:
                            System.out.println(RED + "Invalid update choice." + RESET);
                            break;
                    }
                    System.out.println();
                    break;

                case 4:
                    boolean found = false;
                    System.out.println(ORANGE + "======================================================================================================================================" + RESET);
                    System.out.println(YELLOW + "                                                 Search Flight (Using BST with Flight Number)                                      " + RESET);
                    System.out.println(ORANGE + "======================================================================================================================================" + RESET);

                    System.out.print("Enter the Flight Number you want to search : ");
                    String searchNumber = sc.nextLine();

                    FlightNode result = flightTree.search(flightTree.root, searchNumber);

                    if (result != null)
                    {
                        System.out.println(GREEN + " Found in BST → " + result.flightNumber + " (" + result.flightName + ")" + RESET);

                        String sql3 = "SELECT * FROM flights WHERE flight_number = ?";
                        PreparedStatement pst4 = con.prepareStatement(sql3);
                        pst4.setString(1, searchNumber);
                        ResultSet rs = pst4.executeQuery();

                        System.out.println("Enter your password");
                        upass = sc.next();
                        if (cpass.equals(upass))
                        {
                            con.commit();
                            if (rs.next())
                            {
                                found = true;
                                System.out.println();
                                System.out.println("Flight ID                   : " + rs.getInt("flight_id"));
                                System.out.println("Flight Number               : " + rs.getString("flight_number"));
                                System.out.println("Flight Name                 : " + rs.getString("flight_name"));
                                System.out.println("Departure City              : " + rs.getString("departure_city"));
                                System.out.println("Arrival City                : " + rs.getString("arrival_city"));
                                System.out.println("Total Seats                 : " + rs.getInt("number_of_seats"));
                                System.out.println("Total Duration              : " + rs.getString("duration"));
                                System.out.println("Departure Time              : " + rs.getString("departure_time"));
                                System.out.println("Arrival Time                : " + rs.getString("arrival_time"));
                                System.out.println("Aircraft Type               : " + rs.getString("aircraft_type"));
                                System.out.println("Economy Price               : " + rs.getDouble("economy"));
                                System.out.println("Premium Economy Price       : " + rs.getDouble("premium_economy"));
                                System.out.println();
                            }
                        }
                        else
                        {
                            con.rollback();
                            System.out.println(RED + "️ Wrong password entered , searching cancelled " + RESET);
                        }
                    }
                    else
                    {
                        System.out.println(RED + " Flight not found in BST, checking Database..." + RESET);

                        String sql3 = "SELECT * FROM flights WHERE flight_number = ?";
                        PreparedStatement pst4 = con.prepareStatement(sql3);
                        pst4.setString(1, searchNumber);
                        ResultSet rs = pst4.executeQuery();

                        System.out.println("Enter your password");
                        upass = sc.next();
                        if (cpass.equals(upass))
                        {
                            con.commit();
                            if (rs.next())
                            {
                                found = true;
                                System.out.println();
                                System.out.println("Flight ID                   : " + rs.getInt("flight_id"));
                                System.out.println("Flight Number               : " + rs.getString("flight_number"));
                                System.out.println("Flight Name                 : " + rs.getString("flight_name"));
                                System.out.println("Departure City              : " + rs.getString("departure_city"));
                                System.out.println("Arrival City                : " + rs.getString("arrival_city"));
                                System.out.println("Total Seats                 : " + rs.getInt("number_of_seats"));
                                System.out.println("Total Duration              : " + rs.getString("duration"));
                                System.out.println("Departure Time              : " + rs.getString("departure_time"));
                                System.out.println("Arrival Time                : " + rs.getString("arrival_time"));
                                System.out.println("Aircraft Type               : " + rs.getString("aircraft_type"));
                                System.out.println("Economy Price               : " + rs.getDouble("economy"));
                                System.out.println("Premium Economy Price       : " + rs.getDouble("premium_economy"));
                                System.out.println();
                            }
                        }
                        else
                        {
                            con.rollback();
                            System.out.println(RED + "️ Wrong password entered , searching cancelled " + RESET);
                        }
                    }

                    if (!found)
                    {
                        System.out.println(RED + "️ No flight found with Number " + searchNumber + ". Search failed." + RESET);
                    }
                    break;

                case 5:
                    System.out.println(ORANGE + "======================================================================================================================================" + RESET);
                    System.out.println(YELLOW + "                                                      All The Flight Details                                                      " + RESET);
                    System.out.println(ORANGE + "======================================================================================================================================" + RESET);

                    String sql4 = "SELECT * FROM flights";
                    PreparedStatement pst5 = con.prepareStatement(sql4);
                    ResultSet urs = pst5.executeQuery();

                    System.out.println();
                    System.out.println("Enter your password");
                    upass = sc.next();
                    if (cpass.equals(upass))
                    {
                        System.out.println(BLUE + "===============================================================================================================================================================================================================================" + RESET);
                        System.out.printf(
                                "%-10s %-15s %-20s %-20s %-20s %-10s %-20s %-20s %-20s %-20s %-15s %-15s\n",
                                "Flight ID", "Flight No", "Flight Name", "Departure City", "Arrival City",
                                "Seats", "Total Duration", "Departure Time", "Arrival Time",
                                "Aircraft Type", "Economy", "Premium Economy"
                        );
                        System.out.println(BLUE + "===============================================================================================================================================================================================================================" + RESET);

                        con.commit();
                        while (urs.next())
                        {
                            System.out.printf(
                                    "%-10d %-15s %-20s %-20s %-20s %-10d %-20s %-20s %-20s %-20s %-15.2f %-15.2f\n",
                                    urs.getInt("flight_id"),
                                    urs.getString("flight_number"),
                                    urs.getString("flight_name"),
                                    urs.getString("departure_city"),
                                    urs.getString("arrival_city"),
                                    urs.getInt("number_of_seats"),
                                    urs.getString("duration"),
                                    urs.getString("departure_time"),
                                    urs.getString("arrival_time"),
                                    urs.getString("aircraft_type"),
                                    urs.getDouble("economy"),
                                    urs.getDouble("premium_economy")
                            );
                        }
                    }
                    else
                    {
                        con.rollback();
                        System.out.println(RED + "️ Wrong password entered , no flights displayed" + RESET);
                    }
                    System.out.println(BLUE + "===============================================================================================================================================================================================================================" + RESET);
                    System.out.println();
                    break;

                case 6:
                    String sql = "SELECT * FROM customers";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    System.out.println("Enter your password");
                    upass = sc.next();
                    if (cpass.equals(upass))
                    {
                        con.commit();
                        System.out.println(ORANGE+"\n--- CUSTOMERS ---"+RESET);
                        while (rs.next())
                        {
                            System.out.println("Customer ID: " + rs.getInt("customer_id") +
                                " | Name: " + rs.getString("fname") + "" + rs.getString("lname") +
                                " | Age: " + rs.getString("age") +
                                " | Phone: " + rs.getString("phone"));
                        }
                    }
                    else
                    {
                        con.rollback();
                        System.out.println(RED+"Wrong password entered, try again "+RESET);
                    }
                    break;

                case 7:
                    String msql = "SELECT * FROM membership";
                    Statement mst = con.createStatement();
                    ResultSet mrs = mst.executeQuery(msql);

                    System.out.println("Enter your password");
                    upass = sc.next();
                    if (cpass.equals(upass))
                    {
                        con.commit();
                        System.out.println("---------------------------------------------------------------");
                        System.out.printf("%-15s %-15s %-25s %-10s%n", "Membership ID", "Customer ID", "Start Date", "Miles");
                        System.out.println("---------------------------------------------------------------");

                        while (mrs.next())
                        {
                            String membershipId = mrs.getString("membership_id");
                            int customerId = mrs.getInt("customer_id");
                            String startDate = mrs.getString("start_date");
                            int miles = mrs.getInt("miles");

                            System.out.printf("%-15s %-15d %-25s %-10d%n", membershipId, customerId, startDate, miles);
                        }
                    }
                    else
                    {
                        con.rollback();
                        System.out.println(RED+"Wrong password entered, try again later "+RESET);
                    }
                    break;
                case 8:
                    String bsql = "SELECT * FROM bookings";
                    Statement st1 = con.createStatement();
                    ResultSet rs1 = st1.executeQuery(bsql);

                    System.out.println("Enter your password");
                    upass = sc.next();
                    if (cpass.equals(upass))
                    {
                        con.commit();
                        System.out.println("\n============================================================================================================================================================");
                        System.out.printf("%-10s %-12s %-10s %-12s %-8s %-15s %-15s %-15s %-12s %-10s %-20s%n",
                                "BookingID", "CustomerID", "FlightID", "PNR", "SeatNo", "Services", "Assistance",
                                "CheckInStatus", "Status", "Amount", "BookingTime");
                        System.out.println("============================================================================================================================================================");

                        while (rs1.next())
                        {
                            int booking_id = rs1.getInt("booking_id");
                            int customer_id = rs1.getInt("customer_id");
                            int flight_id = rs1.getInt("flight_id");
                            String pnr = rs1.getString("pnr");
                            String seat_no = rs1.getString("seat_no");
                            String services = rs1.getString("services");
                            String assistance = rs1.getString("assistance");
                            String check_in_status = rs1.getString("check_in_status");
                            String status = rs1.getString("status");
                            double amount = rs1.getDouble("amount");
                            Timestamp booking_time = rs1.getTimestamp("booking_time");

                            System.out.printf("%-10d %-12d %-10d %-12s %-8s %-15s %-15s %-15s %-12s %-10.2f %-20s%n",
                                    booking_id, customer_id, flight_id, pnr, seat_no, services, assistance,
                                    check_in_status, status, amount, booking_time.toString());
                        }
                        System.out.println("============================================================================================================================================================");
                    }
                    else
                    {
                        con.rollback();
                        System.out.println(RED+"Wrong password entered, try again later "+RESET);
                    }
                    break;

                case 9:
                    String psql = "SELECT * FROM payments";
                    Statement st2 = con.createStatement();
                    ResultSet rs2 = st2.executeQuery(psql);

                    System.out.println("Enter your password");
                    upass = sc.next();
                    if (cpass.equals(upass))
                    {
                        con.commit();
                        System.out.println("\n=======================================================================================================================");
                        System.out.printf("%-10s %-12s %-10s %-15s %-15s %-15s %-20s%n",
                                "PaymentID", "BookingID", "Amount", "Method", "Status", "TxRef", "PaymentTime");
                        System.out.println("=======================================================================================================================");

                        while (rs2.next())
                        {
                            int payment_id = rs2.getInt("payment_id");
                            int booking_id = rs2.getInt("booking_id");
                            double amount = rs2.getDouble("amount");
                            String method = rs2.getString("method");
                            String status = rs2.getString("status");
                            String tx_ref = rs2.getString("tx_ref");
                            Timestamp payment_time = rs2.getTimestamp("payment_time");

                            System.out.printf("%-10d %-12d %-10.2f %-15s %-15s %-15s %-20s%n",
                                    payment_id, booking_id, amount, method, status, tx_ref, payment_time);
                        }
                        System.out.println("=======================================================================================================================");
                    }
                    else
                    {
                        con.rollback();
                        System.out.println(RED+"Wrong password entered, try again later "+RESET);
                    }
                    break;

                case 10:
                    System.out.println(ORANGE+"Thank you "+user+" for using the system."+RESET);
                    System.out.println(YELLOW+"Logging out for "+user+""+RESET);
                    System.exit(0);
                    break;

                default:
                    System.out.println(RED+"Invalid choice , try again later"+RESET);
                    System.out.println(RED+"Logging out for "+user+""+RESET);
                    System.exit(0);
                    break;
            }
        }
    }
}