package com.pluralsight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Starter code for the Online Store workshop.
 * Students will complete the TODO sections to make the program work.
 */
public class Store {

    // Stores product ID and product
    private static Map<String, Product> inventory = new HashMap<>();
    // Stores Product ID and quantity
    private static Map<String, Integer> cart = new HashMap<>();

    // Reused code from Ledger Application
    private static final String FILE_NAME = "products.csv";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    public static void main(String[] args) {


        // Load inventory from the data file (pipe-delimited: id|name|price)
        loadInventory(FILE_NAME);

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
     * Checks if products.csv exists.
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
                                      Map<String, Integer> cart,
                                      Scanner scanner) {
        while (true) {
            System.out.println("Welcome to the Product Screen!");
            System.out.println("1. View All Products");
            System.out.println("2. Search by Product ID");
            System.out.println("3. Back to Home Screen");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                // Displays all products
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
                // Search by specific product ID
                case 2 -> {
                    System.out.println("Search by Product ID");
                    System.out.print("Enter Product ID (or X to cancel): ");
                    String productID = scanner.nextLine().trim();
                    // If cancel search then returns to product screen
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
    public static void cartScreen(Map<String, Integer> cart, Scanner scanner) {

        while (true) {
            System.out.println("Welcome to the Cart Screen!");
            System.out.println("1. View Cart");
            System.out.println("2. Edit product quantity by Product ID");
            System.out.println("3. Checkout");
            System.out.println("4. Back to Home Screen");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    if (inventory.isEmpty()) return;
                    // Loops through cart to calculate total
                    double total = 0;
                    for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                        Product product = inventory.get(entry.getKey());
                        System.out.println(product);
                        System.out.println("Quantity: " + entry.getValue());
                        int quantity = entry.getValue();
                        total += product.getPrice() * quantity;
                    }
                    System.out.println("Total: " + total);
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
                        editQuantity(scanner);
                        return;
                    }
                }
                case 2 -> {
                    System.out.println("Edit product quantity by product ID");
                    editQuantity(scanner);
                    return;
                }
                case 3 -> {
                    checkOut(scanner);
                }
                case 4 -> {
                    System.out.println("Back to Home Screen");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * Handles the checkout process:
     * Makes sure the cart isn't empty
     * Calculates total
     * Accept payment and calculate change.
     * Display a simple receipt.
     * Clear the cart.
     */
    public static void checkOut(Scanner scanner) {

        if (cart.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }
        // Loops through cart to get total.
        double total = 0;
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            Product product = inventory.get(entry.getKey());
            total += product.getPrice() * entry.getValue();
        }
        System.out.println("Total: " + total);
        System.out.print("Please enter payment amount: ");

        double payment = 0;

        /**
         * Makes sure the input is a number
         * The payment is greater than 0
         * Payment is enough to cover total
         */
        while (true) {
            if (!scanner.hasNextDouble()) {
                System.out.println("Enter a valid number!");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            payment = scanner.nextDouble();
            scanner.nextLine();
            if (payment == 0) {
                System.out.println("Please enter a amount greater than zero!");
                continue;
            }
            if (payment < total) {
                System.out.println("The payment is not enough!");
                continue;
            }
            break;

        }

        // Prints change
        double change = payment - total;
        System.out.printf("The change is %.2f\n", change);
        System.out.println("Thank you for shopping!\n\n");

        /**
         * Prints receipt for user
         * Includes the header "Receipt with current date and time formatted"
         */
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDateTime = dateTime.format(DATETIME_FMT);
        System.out.println("Receipt " + formattedDateTime);
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            Product product = inventory.get(entry.getKey());
            System.out.println(product);
        }
        System.out.printf("Total is %.2f\n", total);
        System.out.printf("Payment amount is %.2f\n", payment);
        System.out.printf("Change amount is %.2f\n", change);
        cart.clear();
    }

    /**
     * Allows user to change the quantity of the products in cart.
     *
     * @param scanner
     */
    public static void editQuantity(Scanner scanner) {
        // Loops to prompt user to enter ID or return to home screen
        while (true) {
            System.out.print("Enter Product ID (or X to cancel): ");
            String productID = scanner.nextLine().trim();
            if (productID.equalsIgnoreCase("X")) {
                return;
            }
            if (!cart.containsKey(productID)) {
                System.out.println("Product does not exist in cart!");
                System.out.println("Please enter a valid product ID!");
                continue;
            }
            Product product = inventory.get(productID);
            System.out.println(product);
            System.out.println("Quantity: " + cart.get(productID));
            int unchangedQuantity = cart.get(productID);
            int quantity = unchangedQuantity;
            // Prompts user to enter valid input
            while (true) {
                System.out.print("What would you like the quantity to be: ");
                if (!scanner.hasNextInt()) {
                    System.out.print("Please enter a number above 0");
                    scanner.nextLine();                 // discard bad input
                    continue;
                }
                break;
            }
            quantity = scanner.nextInt();
            scanner.nextLine();

            /**
             * Checks if quantity is 0 to remove the item completely from cart.
             * Prints quantity went from x to y with the product details.
             * checks if quantity even changed.
             */
            if (quantity == 0) {
                cart.remove(productID);
                System.out.println(product.getName() + " removed from cart");
            } else if (unchangedQuantity != quantity) {
                cart.put(productID, quantity);
                System.out.println(product.getName() + " quantity changed from "
                        + unchangedQuantity + " to " + quantity);
            } else {
                System.out.println("Quantity did not change!");
            }

            System.out.print("Would you like to change another product quantity? (Y/N) ");
            String userChoice = scanner.nextLine().trim();
            if (userChoice.equalsIgnoreCase("n")) break;
        }
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
            // If x go back to Product screen
            if (productID.equalsIgnoreCase("X")) {
                return;
            }
            Product product = inventory.get(productID);

            // Checks if the product created is null
            if (product == null) {
                System.out.println("Product ID does not exist!");
                continue;
            }
            // Adds product to cart product ID as key and with a quantity of 1 or current quantity + 1
            cart.put(product.getProductId(), cart.getOrDefault(product.getProductId(), 0) + 1);

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


