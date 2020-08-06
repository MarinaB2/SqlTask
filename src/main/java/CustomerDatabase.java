

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class CustomerDatabase {

    private static String URL = "jdbc:sqlite::resource:Chinook_Sqlite.sqlite";
    private static Connection conn = null;
    public static ArrayList<Customer> customers = new ArrayList<Customer>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a customer Id: ");
        int userInput;
        Customer customer;

        if (scanner.hasNextInt()) {//Checking if the input is an integer
            userInput = scanner.nextInt();
            customer = getCustomerName(userInput);//Get den customer who has the id that the user wrote

        } else {// If not int, get a random customer
            System.out.println("Getting a random customer ..");
            customer = getRandomCustomerName();
            userInput = Integer.parseInt(customer.getCustomerId());

        }
        System.out.println("The full name of the customer is: " + customer.getFirstName() + " " + customer.getLastName());


        //Get All the Genre that the customer likes
        ArrayList<String> getGeneres = getGenreName(userInput);
        System.out.println("This all the generes that the customers like, in order " + getGeneres);


        //Get the customers most popular genre
        ArrayList<String> generes = getMostPopularGenre(userInput);
        System.out.println("Customers most popular genre is : " + generes.get(generes.size() - 1));

        // In case of Ties print the other one
        for (String genre : generes) {
            if (genre.length() == generes.size() - 1) {
                System.out.println("Customers have another most popular genre is : " + generes.get(generes.size() - 2));
            }
        }

    }

    public static Customer getCustomerName(int customerId) {
        try {
            conn = DriverManager.getConnection(URL);

            // Prepare Statement
            PreparedStatement preparedStatement =
                    conn.prepareStatement("SELECT CustomerId,FirstName,LastName FROM Customer WHERE CustomerId =?");
            preparedStatement.setInt(1, customerId);

            // Execute Statement
            ResultSet resultSet = preparedStatement.executeQuery();

            return new Customer(
                    resultSet.getString("customerId"),
                    resultSet.getString("firstName"),
                    resultSet.getString("lastName")
            );

        } catch (Exception ex) {
            System.out.println("Something went wrong(CustomerName)...");
            System.out.println(ex.toString());
        } finally {
            try {
                // Close Connection
                conn.close();
            } catch (Exception ex) {
                System.out.println("Something went wrong(CustomerName) while closing connection.");
                System.out.println(ex.toString());
            }
        }
        return null;
    }

    public static Customer getRandomCustomerName() {
        try {
            conn = DriverManager.getConnection(URL);

            // Prepare Statement
            PreparedStatement preparedStatement =
                    conn.prepareStatement("SELECT * FROM Customer");

            // Execute Statement
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process Results
            while (resultSet.next()) {
                customers.add(
                        new Customer(
                                resultSet.getString("customerId"),
                                resultSet.getString("firstName"),
                                resultSet.getString("lastName")
                        ));
            }
            int random = new Random().nextInt(customers.size());
            return customers.get(random);

        } catch (Exception ex) {
            System.out.println("Something went wrong(CustomerName)...");
            System.out.println(ex.toString());
        } finally {
            try {
                // Close Connection
                conn.close();
            } catch (Exception ex) {
                System.out.println("Something went wrong(CustomerName) while closing connection.");
                System.out.println(ex.toString());
            }
        }
        return null;
    }

    public static ArrayList<String> getGenreName(int customerId) {
        try {
            // Open Connection
            conn = DriverManager.getConnection(URL);

            // Prepare Statement

            // A quary that gets all the customers genre
            PreparedStatement preparedStatement =
                    conn.prepareStatement("SELECT Genre.Name FROM Genre JOIN Track ON Genre.GenreId = Track.GenreId "
                            + "JOIN InvoiceLine ON InvoiceLine.TrackId = Track.TrackId "
                            + "JOIN Invoice ON InvoiceLine.InvoiceId = Invoice.InvoiceId "
                            + "WHERE CustomerId =? " + "GROUP BY Genre.Name " + "ORDER BY COUNT(Invoice.InvoiceId) DESC");
            preparedStatement.setInt(1, customerId);

            // Execute Statement
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<String> geners = new ArrayList<>();
            // Process Results
            while (resultSet.next()) {
                geners.add(resultSet.getString("Name"));

            }
            return geners;
        } catch (Exception ex) {
            System.out.println("Something went wrong(getGenreName)...");
            System.out.println(ex.toString());
        } finally {
            try {
                // Close Connection
                conn.close();
            } catch (Exception ex) {
                System.out.println("Something went wrong(getGenreName) while closing connection.");
                System.out.println(ex.toString());
            }
        }
        return null;
    }

    public static ArrayList<String> getMostPopularGenre(int customerId) {
        try {
            // Open Connection
            conn = DriverManager.getConnection(URL);
            // Prepare Statement

            // Get the most popular genre by counting Invoice
            PreparedStatement preparedStatement =
                    conn.prepareStatement(" SELECT Genre.Name, COUNT(Invoice.InvoiceId), Customer.FirstName, Genre.GenreId FROM Invoice "
                            + "JOIN Customer ON Invoice.CustomerId = Customer.CustomerId "
                            + "JOIN InvoiceLine ON InvoiceLine.InvoiceId = Invoice.InvoiceId "
                            + "JOIN Track ON Track.TrackId = InvoiceLine.TrackId "
                            + "JOIN Genre ON Track.GenreId = Genre.GenreId "
                            + "WHERE Customer.CustomerId =? " + "GROUP BY Genre.Name " + "ORDER BY COUNt(Invoice.InvoiceId)");
            preparedStatement.setInt(1, customerId);

            // Execute Statement
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<String> geners = new ArrayList<>();
            // Process Results
            while (resultSet.next()) {
                geners.add(resultSet.getString("Name"));

            }
            return geners;

        } catch (Exception ex) {
            System.out.println("Something went wrong(MostPopularGenre)...");
            System.out.println(ex.toString());

        } finally {
            try {
                // Close Connection
                conn.close();
            } catch (Exception ex) {
                System.out.println("Something went wrong(MostPopularGenre) while closing connection.");
                System.out.println(ex.toString());
            }
        }
        return null;
    }

}


