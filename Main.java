import java.util.*;
import java.sql.*;
import java.io.*;

class Main
{
    public static final String RESET  = "\u001B[0m";
    public static final String RED    = "\u001B[31m";
    public static final String BLUE   = "\u001B[34m";
    public static Connection con;

    static void connect()
    {
        String dburl = "jdbc:mysql://localhost:3306/arms_trial";
        String dbuser = "root";
        String dbpass = "";

        try
        {
            con = DriverManager.getConnection(dburl, dbuser, dbpass);
            if (con != null)
            {
                System.out.println(BLUE + " All systems are operational and the connection is live." + RESET);
            }
        }
        catch (SQLException e)
        {
            System.out.println(RED + " ERROR: Unable to connect to the database. Check configuration and network status." + RESET);
            System.out.println(RED + "Cause: " + e.getMessage() + RESET);
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception
    {
        connect();
        System.out.println();
        System.out.println(BLUE + "=====================================================================================================================================" + RESET);
        System.out.println(RED + "                                                 Welcome to Airline Reservation System                             " + RESET);
        System.out.println(BLUE + "=====================================================================================================================================" + RESET);

        Scanner sc = new Scanner(System.in);

        System.out.println(" Press 1. for Customer ");
        System.out.println(" Press 2. for Admin/Employee ");
        System.out.println(" Press 3. for exit ");
        System.out.print("Enter choice : ");
        int choice = sc.nextInt();
        sc.nextLine();
        switch (choice)
        {
            case 1:
                Customer c = new Customer(con);
                c.customerMenu();
                break;
            case 2:
                Admin a = new Admin();
                a.login();
                a.Booking();
                break;

            case 3:
                System.out.println(BLUE+" Thank you for visiting our system "+RESET);
                break;

            default:
                System.out.println(RED+"Invalid choice"+RESET);
                con.close();
                break;
        }
    }
}