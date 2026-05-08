package com.pluralsight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Starter code for the Online Store workshop.
 * Students will complete the TODO sections to make the program work.
 */
public class Store {

    // Create lists for inventory and the shopping cart
    private static Map<String, Product> inventory = new HashMap<>();
    private static Map<Product, Integer> cart = new HashMap<>();
    private static final String FILE_NAME = "products.csv";

    public static void main(String[] args) {


        // Load inventory from the data file (pipe-delimited: id|name|price)
        loadInventory("products.csv");

        // Main menu loop
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to the Online Store!");
            System.out.println("1. Products");
            System.out.println("2. Cart");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> productsScreen(inventory, cart, scanner);
                case 2 -> cartScreen(cart, scanner);
                case 3 -> System.out.println("Thank you for shopping with us!");
                default -> System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    /**
     * Reads product data from a file and populates the inventory list.
     * File format (pipe-delimited):
     * id|name|price
     * <p>
     * Example line:
     * A17|Wireless Mouse|19.99
     */
    public static void loadInventory(String fileName) {

        File file = new File(fileName);

        if (!file.exists()) {
            try {
                // Creates new "products.csv" and lets user know.
                if (file.createNewFile()) {
                    System.out.println("New \"products.csv\" file created");
                }
            } catch (IOException e) {
                // Catches any errors when creating file.
                System.out.println("Error creating file: " + e.getMessage());
            }
            // Prevents reading file if file was just created.
            return;
        }
        // Uses try-with-resources to auto-close BufferReader
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Catches bad lines and skips them to prevent exception.
                try {

                    String[] values = line.split("\\|");

                    if (values.length != 3) {
                        throw new Exception("Missing field");
                    }

                    String productID = values[0];
                    String name = values[1];
                    double transactionAmount = parseDouble(values[2]);

                    inventory.put(productID, new Product(productID, name, transactionAmount));
                }
                // Catches all general errors within each transaction.
                catch (Exception e) {
                    System.out.println("Skipping bad line: " + line + "(" + e.getMessage() + ")");
                }
            }
            // catches file not being found or can't read, etc....
        } catch (IOException e) {
            System.out.println("Error reading file:  " + e.getMessage());
        }
    }

    /**
     * Displays all products or by product ID and lets the user add one to the cart.
     * Typing X returns to the main menu.
     */
    public static void productsScreen(Map<String, Product> inventory,
                                      Map<Product, Integer> cart,
                                      Scanner scanner) {
        while (true) {
            System.out.println("Welcome to the Product Screen!");
            System.out.println("1. View All Products");
            System.out.println("2. Search by Product ID");
            System.out.println("3. Back to Home Screen");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.println("All Products");
                    for (Map.Entry<String, Product> entry : inventory.entrySet()) {
                        System.out.println(entry.getValue());
                    }
                    String userChoice;
                    while (true) {
                        System.out.print("Would you like to add a product to the cart? (Y/N)");
                        userChoice = scanner.nextLine().trim();
                        if (userChoice.equalsIgnoreCase("y") || userChoice.equalsIgnoreCase("n")) {
                            break;
                        }
                        System.out.println("Invalid input. Enter Y or N");
                    }
                    if (userChoice.equalsIgnoreCase("y")) {
                        addProduct(scanner);
                        return;
                    }


                }
                case 2 -> {
                    System.out.println("Search by Product ID");
                    System.out.print("Enter Product ID (or X to cancel): ");
                    String productID = scanner.nextLine().trim();
                    if (productID.equalsIgnoreCase("X")) {
                        return;
                    }
                    Product product = inventory.get(productID);

                    if (product == null) {
                        System.out.println("Product ID does not exist!");
                        continue;
                    }
                    System.out.println(product);
                    String userChoice;
                    while (true) {
                        System.out.print("Would you like to add a product to the cart? (Y/N)");
                        userChoice = scanner.nextLine().trim();
                        if (userChoice.equalsIgnoreCase("y") || userChoice.equalsIgnoreCase("n")) {
                            break;
                        }
                        System.out.println("Invalid input. Enter Y or N");
                    }
                    if (userChoice.equalsIgnoreCase("y")) {
                        addProduct(scanner);
                        return;
                    }
                }
                case 3 -> {
                    System.out.println("Back to Home Screen");
                    return;
                }
                default -> System.out.println("Invalid choice!");

            }
        }
    }

    /**
     * Shows the contents of the cart, calculates the total,
     * and offers the option to check out.
     */
    public static void cartScreen(Map<Product, Integer> cart, Scanner scanner) {
        // TODO:
        //   • list each product in the cart
        //   • compute the total cost
        //   • ask the user whether to check out (C) or return (X)
        //   • if C, call checkOut(cart, totalAmount, scanner)

        while (true) {
            System.out.println("Welcome to the Product Screen!");
            System.out.println("1. View Cart");
            System.out.println("2. Edit product quantity by Product ID");
            System.out.println("3. Checkout");
            System.out.println("4. Back to Home Screen");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    if (inventory.isEmpty()) return;

                    for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                        System.out.println(entry.getValue());
                    }
                    String userChoice;
                    while (true) {
                        System.out.print("Would you like to edit the quantity of the products? (Y/N)");
                        userChoice = scanner.nextLine().trim();
                        if (userChoice.equalsIgnoreCase("y") || userChoice.equalsIgnoreCase("n")) {
                            break;
                        }
                        System.out.println("Invalid input. Enter Y or N");
                    }
                    if (userChoice.equalsIgnoreCase("y")) {
                        return;
                    }
                }
                case 2 -> {
                    System.out.println("Edit product quantity");

                }
            }
        }
    }

    /**
     * Handles the checkout process:
     * 1. Confirm that the user wants to buy.
     * 2. Accept payment and calculate change.
     * 3. Display a simple receipt.
     * 4. Clear the cart.
     */
    public static void checkOut(ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {
        // TODO: implement steps listed above
    }


    public static void editQuantity(Scanner scanner) {

    }

    /**
     * Allows user to add products to cart
     *
     * @param scanner
     */
    public static void addProduct(Scanner scanner) {
        while (true) {
            System.out.print("Enter Product ID (or X to cancel): ");
            String productID = scanner.nextLine().trim();
            if (productID.equalsIgnoreCase("X")) {
                return;
            }
            Product product = inventory.get(productID);

            if (product == null) {
                System.out.println("Product ID does not exist!");
                continue;
            }
            cart.put(product, cart.getOrDefault(product, 0) + 1);

            System.out.println("Product " + product.getName() + " has been added!");

            System.out.println("Would you like to add another product to the cart? (Y/N)");
            String userChoice = scanner.nextLine();
            if (userChoice.equalsIgnoreCase("n")) break;
        }
    }

    /**
     * Parse double string
     * if s is empty throws Exception with Custom message (skipped by user)
     * Uses try/catch to check if user input valid.
     * if s is not a number or in a Double format throws Exception
     */
    private static Double parseDouble(String s) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Input can't be empty");
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + s);
        }
    }
}

