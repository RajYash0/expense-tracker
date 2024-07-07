package com.example;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.java.com.example.model.Expense;
import main.java.com.example.model.User;
import main.java.com.example.util.FileUtil;

public class ExpenseTrackerApp {
    private static final String USER_FILE = "users.dat";
    private static final String EXPENSE_FILE = "expenses.dat";
    private static List<User> users = new ArrayList<>();
    private static List<Expense> expenses = new ArrayList<>();
    private static User currentUser;

    public static void main(String[] args) {
        try {
            users = FileUtil.loadUsers(USER_FILE);
            expenses = FileUtil.loadExpenses(EXPENSE_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous data found.");
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Add Expense");
            System.out.println("4. View Expenses");
            System.out.println("5. View Category-wise Summation");
            System.out.println("6. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    register(scanner);
                    break;
                case 2:
                    login(scanner);
                    break;
                case 3:
                    addExpense(scanner);
                    break;
                case 4:
                    viewExpenses();
                    break;
                case 5:
                    viewCategoryWiseSummation();
                    break;
                case 6:
                    running = false;
                    saveData();
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        users.add(new User(username, password));
        System.out.println("User registered successfully.");
    }

    private static void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                System.out.println("Login successful.");
                return;
            }
        }
        System.out.println("Invalid username or password.");
    }

    private static void addExpense(Scanner scanner) {
        if (currentUser == null) {
            System.out.println("Please login first.");
            return;
        }

        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            expenses.add(new Expense(date, category, amount));
            System.out.println("Expense added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
        }
    }

    private static void viewExpenses() {
        if (currentUser == null) {
            System.out.println("Please login first.");
            return;
        }

        for (Expense expense : expenses) {
            System.out.println("Date: " + expense.getDate() + ", Category: " + expense.getCategory() + ", Amount: "
                    + expense.getAmount());
        }
    }

    private static void viewCategoryWiseSummation() {
        if (currentUser == null) {
            System.out.println("Please login first.");
            return;
        }

        Map<String, Double> categorySum = new HashMap<>();
        for (Expense expense : expenses) {
            categorySum.put(expense.getCategory(),
                    categorySum.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        for (Map.Entry<String, Double> entry : categorySum.entrySet()) {
            System.out.println("Category: " + entry.getKey() + ", Total Amount: " + entry.getValue());
        }
    }

    private static void saveData() {
        try {
            FileUtil.saveUsers(users, USER_FILE);
            FileUtil.saveExpenses(expenses, EXPENSE_FILE);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}
