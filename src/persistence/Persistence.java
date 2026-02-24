package persistence;

import model.Expense;
import model.ExpenseTracker;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Persistence utilities - provides routines for encoding/decoding expenses
 * and expense trackers as JSON, and saving them to the disk
 */
public final class Persistence {

  private Persistence() { throw new AssertionError(); }

  // MODIFIES: NOTHING
  // EFFECTS: encodes an expense into a JSON object
  public static JSONObject encodeExpense(Expense expense) {
    // TODO
    throw new AssertionError();
  }

  // MODIFIES: NOTHING
  // EFFECTS: decodes an expense from a JSON object, creating a new Expense object
  public static Expense decodeExpense(JSONObject jsonExpense) {
    // TODO
    throw new AssertionError();
  }

  // MODIFIES: NOTHING
  // EFFECTS: encodes an expense tracker into a JSON array
  public static JSONArray encodeExpenseTracker(ExpenseTracker tracker) {
    // TODO
    throw new AssertionError();
  }

  // MODIFIES: tracker
  // EFFECTS: restores an expense tracker from a JSON array, decoding each expense into an Expense object:
  //          1. deletes all expenses in the tracker
  //          2. adds all expenses from the JSON array to the tracker
  public static void restoreExpenseTracker(ExpenseTracker tracker, JSONArray jsonExpenseTracker) {
    // TODO
    throw new AssertionError();
  }

  // MODIFIES: NOTHING
  // EFFECTS: saves an expense tracker to a file at the specified path
  public static void saveExpenseTrackerToFile(ExpenseTracker tracker, Path path) throws IOException {
    // TODO
    throw new AssertionError();
  }

  // MODIFIES: tracker
  // EFFECTS: restores an expense tracker from a file at the specified path:
  //          1. reads the file as a JSON array, decoding each expense into an Expense object
  //          2. deletes all expenses in the tracker
  //          3. adds all expenses from the JSON array to the tracker
  public static void restoreExpenseTrackerFromFile(ExpenseTracker tracker, Path path) throws IOException {
    // TODO
    throw new AssertionError();
  }

}
