package persistence;

import model.Category;
import model.Expense;
import model.ExpenseTracker;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Persistence utilities - provides routines for encoding/decoding expenses
 * and expense trackers as JSON, and saving them to the disk
 */
public final class Persistence {

  private Persistence() { throw new AssertionError(); }

  // MODIFIES: NOTHING
  // EFFECTS: encodes an expense into a JSON object
  public static JSONObject encodeExpense(Expense expense) {
    JSONObject jsonExpense = new JSONObject();
    jsonExpense.put("date", expense.getDate().toString());
    jsonExpense.put("category", expense.getCategory().name());
    jsonExpense.put("amount", expense.getAmount());
    jsonExpense.put("description", expense.getDescription());
    return jsonExpense;
  }

  // MODIFIES: NOTHING
  // EFFECTS: decodes an expense from a JSON object, creating a new Expense object
  public static Expense decodeExpense(JSONObject jsonExpense) {
    return new Expense(
      LocalDate.parse(jsonExpense.getString("date")),
      Category.valueOf(jsonExpense.getString("category")),
      jsonExpense.getBigDecimal("amount"),
      jsonExpense.getString("description")
    );
  }

  // MODIFIES: NOTHING
  // EFFECTS: encodes an expense tracker into a JSON array
  public static JSONArray encodeExpenseTracker(ExpenseTracker tracker) {
    JSONArray jsonExpenseTracker = new JSONArray();
    for (Expense expense : tracker.getExpenses().toList()) {
      jsonExpenseTracker.put(encodeExpense(expense));
    }
    return jsonExpenseTracker;
  }

  // MODIFIES: tracker
  // EFFECTS: restores an expense tracker from a JSON array, decoding each expense into an Expense object:
  //          1. deletes all expenses in the tracker
  //          2. adds all expenses from the JSON array to the tracker
  public static void restoreExpenseTracker(ExpenseTracker tracker, JSONArray jsonExpenseTracker) {
    List<Expense> newExpenseList = jsonExpenseTracker
      .toList()
      .stream()
      .map(jsonExpense -> decodeExpense(new JSONObject((Map<?, ?>) jsonExpense)))
      .toList();

    for (Expense oldExpense : tracker.getExpenses().toList()) tracker.deleteExpense(oldExpense);
    for (Expense newExpense : newExpenseList) tracker.addExpense(newExpense);
  }

  // MODIFIES: NOTHING
  // EFFECTS: saves an expense tracker to a file at the specified path
  public static void saveExpenseTrackerToFile(ExpenseTracker tracker, Path path) throws IOException {
    String json = encodeExpenseTracker(tracker).toString();
    Files.writeString(path, json);
  }

  // MODIFIES: tracker
  // EFFECTS: restores an expense tracker from a file at the specified path:
  //          1. reads the file as a JSON array, decoding each expense into an Expense object
  //          2. deletes all expenses in the tracker
  //          3. adds all expenses from the JSON array to the tracker
  public static void restoreExpenseTrackerFromFile(ExpenseTracker tracker, Path path) throws IOException {
    String json = Files.readString(path);
    restoreExpenseTracker(tracker, new JSONArray(json));
  }

}
