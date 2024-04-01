import java.sql.*;
import java.util.Scanner;

class Menu{
    static String url = "jdbc:mysql://localhost:3306/library_db";
    static String user = "root";
    static String password = "Ganeshaa2005@";

    static Scanner sc = new Scanner(System.in);

//    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    void showBook(){
        // connect with mysql
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "SELECT title, author, quantity, serial_number FROM books";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            System.out.println("Books in the Library:");
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String quantity = resultSet.getString("quantity");
                String serial_number = resultSet.getString("serial_number");
                System.out.println("Title: " + title + " | Author: " + author + " | Quantity: "+ quantity + " | Serial number: " + serial_number);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void issueBook() {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
//            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(url, user, password);

            // Identify the book to be issued by its author
            showBook();
            System.out.println("\nEnter book author name that you want to issue");
            String authorName = sc.nextLine(); // user enter the author name

            // Check if the book is available in the library
            String checkAvailabilityQuery = "SELECT * FROM books WHERE author = ? AND quantity > 0";
            stmt = conn.prepareStatement(checkAvailabilityQuery);
            stmt.setString(1, authorName);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String serialNumber = resultSet.getString("serial_number");
                int quantity = resultSet.getInt("quantity");

                if (quantity > 0) {
                    // Associate the book with the student who is checking it out
                    System.out.println("Enter your enrollment number");
                    int userEnrollment = sc.nextInt();
                    String student_enroll = String.valueOf(userEnrollment);
                    // Update the book quantity to reflect the checkout
                    String updateQuantityQuery = "UPDATE books SET quantity = ? WHERE serial_number = ?";
                    stmt = conn.prepareStatement(updateQuantityQuery);
                    stmt.setInt(1, quantity - 1);
                    stmt.setString(2, serialNumber);
                    stmt.executeUpdate();

                    // Keep track of the books checked out by each student
                    String issueBookQuery = "INSERT INTO issued_books (serial_number, student_enroll) VALUES (?, ?)";
                    stmt = conn.prepareStatement(issueBookQuery);
                    stmt.setString(1, serialNumber);
                    stmt.setString(2, student_enroll);
                    stmt.executeUpdate();
                    System.out.println("Book by author " + authorName + " with serial number " + serialNumber + " is successfully issued to student with enrollment number: "+student_enroll);


                }
                else {
                    System.out.println("No available books by author " + authorName + " for issue.");
                }

            }
            else {
                System.out.println("Book not found in the library.");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
public class Library {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        Menu menu = new Menu();
        System.out.print("1.Show Book Name\n2.Issue Book.\n3.Remove book\n4.Returning book\nChoose your option: ");
        int userChose = sc.nextInt();
        if (userChose == 1){
            menu.showBook();
        }
        else if (userChose == 2){
            menu.issueBook();
        }



    }
}

