import java.sql.*;
import java.util.*;
import java.util.Date;

class Membership extends Customer
{

    protected String membershipId;
    protected int miles;
    protected Date startDate;
    protected String membershipStatus;

    Membership(Connection con)
    {
        super(con);
    }


    void enrollMembership() throws Exception
    {
        showBenefits();

        System.out.print("Enter Phone Number (10 digits starting from 9,8,7) : ");
        String phone = sc.nextLine();
        if (!validatePhoneNumber(phone))
        {
            System.out.println(RED + "Invalid phone number , it should be 10 digits starting from 9,8,7" + RESET);
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

        membershipId = generateDigits(4);   // Generate membership ID
        membershipStatus = "normal";

        String sqlInsert = "INSERT INTO membership(membership_id, customer_id, start_date, miles, membership_status) VALUES(?, ?, NOW(), 0, ?)";
        PreparedStatement pstIns = con.prepareStatement(sqlInsert);
        pstIns.setString(1, membershipId);
        pstIns.setInt(2, customerId);
        pstIns.setString(3, membershipStatus);
        pstIns.executeUpdate();

        miles = 0;
        startDate = new java.util.Date();
        isNormalMember=true;
        System.out.println(GREEN + " Normal Membership created with ID: " + membershipId + RESET);
    }

    void showBenefits()
    {
        System.out.println(YELLOW + "Benefits of Normal Membership:" + RESET);
        System.out.println(BLUE + "- 15% discount on every booking" + RESET);
        System.out.println(BLUE + "- 1000 miles credited per booking" + RESET);
    }

    public String getMembershipId() {
        return membershipId;
    }

    public int getMiles() {
        return miles;
    }

    public void addMiles(int m) {
        this.miles += m;
    }
}