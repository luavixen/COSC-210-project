package ui;

import model.Category;
import model.Expense;
import model.ExpenseTracker;
import model.ExpenseTrackerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Provides the console UI for this expense tracking desktop application
 */
public final class Main {

  private static final ExpenseTracker TRACKER = new ExpenseTracker();
  private static final Scanner SCANNER = new Scanner(System.in);

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  public static void main(String[] args) {
    try {
      printWelcome();

      boolean running = true;
      while (running) {
        printMenu();
        String choice = SCANNER.nextLine().trim();

        switch (choice) {
          case "1" -> addExpense();
          case "2" -> viewAllExpenses();
          case "3" -> editExpense();
          case "4" -> deleteExpense();
          case "5" -> viewByCategory();
          case "6" -> viewByDateRange();
          case "7" -> {
            printGoodbye();
            running = false;
          }
          default -> System.out.println("Invalid choice. Please try again.\n");
        }
      }
    } finally {
      SCANNER.close();
    }
  }

  private static void printWelcome() {
    System.out.println("╔══════════════════════════════════════╗");
    System.out.println("║ COSC 210 - Lua's Expense Tracker :3c ║");
    System.out.println("╚══════════════════════════════════════╝");
    System.out.println();
  }

  private static void printMenu() {
    System.out.println("------------------------------------------------------------");
    System.out.println("1. Add Expense");
    System.out.println("2. View All Expenses");
    System.out.println("3. Edit Expense");
    System.out.println("4. Delete Expense");
    System.out.println("5. View by Category");
    System.out.println("6. View by Date Range");
    System.out.println("7. Exit");
    System.out.println();
    System.out.print("Enter your choice: ");
  }

  private static void printGoodbye() {
    System.out.println();
    System.out.println("Goodbye, see you next time!");
  }

  private static void addExpense() {
    System.out.println("\n=== Add New Expense ===");

    LocalDate date = promptForDate("Enter date (YYYY-MM-DD): ");
    Category category = promptForCategory();
    BigDecimal amount = promptForAmount("Enter amount: $");
    System.out.print("Enter description: ");
    String description = SCANNER.nextLine().trim();

    Expense expense = new Expense(date, category, amount, description);
    if (TRACKER.addExpense(expense)) {
      System.out.println("Expense added successfully.\n");
    } else {
      throw new IllegalStateException("Failed to add duplicate expense in console UI, should be impossible");
    }
  }

  private static void viewAllExpenses() {
    System.out.println("\n=== All Expenses ===");
    ExpenseTrackerView view = TRACKER.getExpenses();
    displayExpenses(view.toList());
  }

  private static void editExpense() {
    System.out.println("\n=== Edit Expense ===");
    List<Expense> expenses = TRACKER.getExpenses().toList();

    if (expenses.isEmpty()) {
      System.out.println("No expenses to edit.\n");
      return;
    }

    displayExpenses(expenses);
    int index = promptForExpenseNumber("Enter expense number to edit: ", expenses.size());
    if (index == -1) return;

    Expense expense = expenses.get(index);

    System.out.println("\nWhat would you like to edit?");
    System.out.println("1. Date");
    System.out.println("2. Category");
    System.out.println("3. Amount");
    System.out.println("4. Description");
    System.out.print("Enter choice: ");
    String choice = SCANNER.nextLine().trim();

    switch (choice) {
      case "1" -> {
        LocalDate newDate = promptForDate("Enter new date (YYYY-MM-DD): ");
        expense.setDate(newDate);
        System.out.println("Date updated successfully.\n");
      }
      case "2" -> {
        Category newCategory = promptForCategory();
        expense.setCategory(newCategory);
        System.out.println("Category updated successfully.\n");
      }
      case "3" -> {
        BigDecimal newAmount = promptForAmount("Enter new amount: $");
        expense.setAmount(newAmount);
        System.out.println("Amount updated successfully.\n");
      }
      case "4" -> {
        System.out.print("Enter new description: ");
        String newDescription = SCANNER.nextLine().trim();
        expense.setDescription(newDescription);
        System.out.println("Description updated successfully.\n");
      }
      default -> System.out.println("Invalid choice.\n");
    }
  }

  private static void deleteExpense() {
    System.out.println("\n=== Delete Expense ===");
    List<Expense> expenses = TRACKER.getExpenses().toList();

    if (expenses.isEmpty()) {
      System.out.println("No expenses to delete.\n");
      return;
    }

    displayExpenses(expenses);
    int index = promptForExpenseNumber("Enter expense number to delete: ", expenses.size());
    if (index == -1) return;

    Expense expense = expenses.get(index);
    System.out.print("Are you sure? (y/n): ");
    String confirm = SCANNER.nextLine().trim().toLowerCase();

    if (confirm.equals("y") || confirm.equals("yes")) {
      if (TRACKER.deleteExpense(expense)) {
        System.out.println("Expense deleted successfully.\n");
      } else {
        throw new IllegalStateException("Failed to delete expense in console UI, should be impossible");
      }
    } else {
      System.out.println("Deletion cancelled.\n");
    }
  }

  private static void viewByCategory() {
    System.out.println("\n=== View by Category ===");
    Category category = promptForCategory();

    ExpenseTrackerView view = TRACKER.getExpenses().filterByCategory(category);
    System.out.println("\n---- Expenses in " + category.getDisplayName() + " --------------------");
    displayExpenses(view.toList());
  }

  private static void viewByDateRange() {
    System.out.println("\n=== View by Date Range ===");
    LocalDate startDate = promptForDate("Enter start date (YYYY-MM-DD): ");
    LocalDate endDate = promptForDate("Enter end date (YYYY-MM-DD): ");

    if (startDate.isAfter(endDate)) {
      System.out.println("Start date must be before or equal to end date.\n");
      return;
    }

    ExpenseTrackerView view = TRACKER.getExpenses().filterByDateRange(startDate, endDate);
    System.out.println("\n---- Expenses from " + startDate + " to " + endDate + " --------------------");
    displayExpenses(view.toList());
  }

  private static void displayExpenses(List<Expense> expenses) {
    if (expenses.isEmpty()) {
      System.out.println("No expenses found.\n");
      return;
    }

    System.out.println();
    BigDecimal total = BigDecimal.ZERO;

    for (int i = 0; i < expenses.size(); i++) {
      Expense expense = expenses.get(i);
      System.out.println((i + 1) + "\t| " + expense.toString());
      total = total.add(expense.getAmount());
    }

    System.out.println("--------------------------------------------------");
    System.out.println("Total: " + NumberFormat.getCurrencyInstance(Locale.CANADA).format(total));
    System.out.println("Count: " + expenses.size() + " expense" + (expenses.size() != 1 ? "s" : ""));
    System.out.println();
  }

  private static LocalDate promptForDate(String prompt) {
    while (true) {
      System.out.print(prompt);
      String input = SCANNER.nextLine().trim();
      try {
        return LocalDate.parse(input, DATE_FORMATTER);
      } catch (DateTimeParseException e) {
        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
      }
    }
  }

  private static Category promptForCategory() {
    Category[] categories = Category.values();
    System.out.println("\nCategories:");
    for (int i = 0; i < categories.length; i++) {
      System.out.println((i + 1) + ". " + categories[i].getDisplayName());
    }

    while (true) {
      System.out.print("Select category (1-" + categories.length + "): ");
      String input = SCANNER.nextLine().trim();
      try {
        int choice = Integer.parseInt(input);
        if (choice >= 1 && choice <= categories.length) {
          return categories[choice - 1];
        }
        System.out.println("Invalid choice. Please try again.");
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a number.");
      }
    }
  }

  private static BigDecimal promptForAmount(String prompt) {
    while (true) {
      System.out.print(prompt);
      String input = SCANNER.nextLine().trim();
      try {
        BigDecimal amount = new BigDecimal(input);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
          System.out.println("Amount must be non-negative.");
          continue;
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
      } catch (NumberFormatException e) {
        System.out.println("Invalid amount. Please enter a valid number.");
      }
    }
  }

  private static int promptForExpenseNumber(String prompt, int maxNumber) {
    while (true) {
      System.out.print(prompt);
      String input = SCANNER.nextLine().trim();

      if (input.equalsIgnoreCase("cancel")) {
        System.out.println("Operation cancelled.\n");
        return -1;
      }

      try {
        int number = Integer.parseInt(input);
        if (number >= 1 && number <= maxNumber) {
          return number - 1;
        }
        System.out.println("Invalid number. Please enter a number between 1 and " + maxNumber + ".");
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a number or 'cancel'.");
      }
    }
  }

}
