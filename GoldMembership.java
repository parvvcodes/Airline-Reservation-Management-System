import java.sql.*;
import java.util.*;
import java.util.Date;

class GoldMembership extends Membership
{

    GoldMembership(Connection con)
    {
        super(con);
    }

    @Override
    void showBenefits()
    {
        System.out.println(YELLOW + "Benefits of Gold Membership:" + RESET);
        System.out.println(BLUE + "- 25% discount on every booking" + RESET);
        System.out.println(BLUE + "- 2000 miles credited per booking" + RESET);
        System.out.println(BLUE + "- Priority boarding & lounge access" + RESET);
    }

    @Override
    void enrollMembership() throws Exception
    {
        showBenefits();

        System.out.print("Enter Phone Number (10 digits starting from 9,8,7) : ");
        String phone = sc.nextLine();
        if (!validatePhoneNumber(phone))
        {
            System.out.println(RED + "Invalid phone number , it should be 10 digits staring from 9,8,7" + RESET);
            return;
        }

        System.out.print("Enter password: ");
        String passCheck = sc.nextLine();

        String sqlCheck = "SELECT customer_id FROM customers WHERE phone=? AND password=?";
        PreparedStatement pstCheck = con.prepareStatement(sqlCheck);
        pstCheck.setString(1, phone);
        pstCheck.setString(2, passCheck);
        ResultSet rs = pstCheck.executeQuery();

        if (!rs.next())
        {
            System.out.println(RED + " Wrong phone/password or not registered customer try again" + RESET);
            return;
        }

        customerId = rs.getInt(1);
        password = passCheck;

        double fee = 2500.0;
        System.out.println(YELLOW + "Gold Membership Fee: ₹" + fee + RESET);

        if (!processPayment(fee))
        {
            System.out.println(RED + " Payment failed, enrollment cancelled!" + RESET);
            return;
        }

        membershipId = generateDigits(4);
        membershipStatus = "gold";

        String sqlInsert = "INSERT INTO membership(membership_id, customer_id, start_date, miles, membership_status) VALUES(?, ?, NOW(), 0, ?)";
        PreparedStatement pstIns = con.prepareStatement(sqlInsert);
        pstIns.setString(1, membershipId);
        pstIns.setInt(2, customerId);
        pstIns.setString(3, membershipStatus);
        pstIns.executeUpdate();

        isGoldMember=true;

        miles = 0;
        startDate = new java.util.Date();
        System.out.println(GREEN + " Gold Membership created with ID: " + membershipId + RESET);
    }

    private boolean processPayment(double fee)
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
                return true;
            }

        }
        else if (method == 2)
        {
            methodStr = "CARD";
            System.out.print("Enter Card Number: ");
            String card = sc.nextLine();
            System.out.print("Enter Name on Card: ");
            String name = sc.nextLine();
            System.out.print("Enter Expiry (MM/YY): ");
            String exp = sc.nextLine();
            System.out.print("Enter CVV: ");
            String cvv = sc.nextLine();

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
                return true;
            }
        }
        else if (method == 3)
        {
            methodStr = "NET BANKING";
            System.out.print("Bank Name: ");
            String bank = sc.nextLine();
            System.out.print("Account/User ID: ");
            String acc = sc.nextLine();

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
                return true;
            }
        }
        else
        {
            System.out.println(RED + "Invalid payment method." + RESET);
            return false;
        }
    }
}