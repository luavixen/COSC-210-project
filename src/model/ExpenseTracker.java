package model;

import persistence.Persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Expense tracker - represents a mutable list of unique expenses
 */
public final class ExpenseTracker {

  private final ArrayList<Expense> expenses = new ArrayList<>();

  public ExpenseTracker() {
    // empty constructor
  }

  // MODIFIES: NOTHING
  // EFFECTS: creates a **copy** of the expense tracker's expense list and returns a view into that state
  public ExpenseTrackerView getExpenses() {
    return new ExpenseTrackerView(expenses);
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: tries to add an expense to the expense tracker, returns true on success, false on duplicate
  public boolean addExpense(Expense expense) {
    if (expenses.contains(expense)) {
      return false;
    } else {
      expenses.add(expense);
      // NOTE: always sort expenses after mutation
      Collections.sort(expenses);
      return true;
    }
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: tries to remove an expense from the expense tracker, returns true on success, false on not present
  public boolean deleteExpense(Expense expense) {
    return expenses.remove(expense);
  }

  // MODIFIES: NOTHING
  // EFFECTS: saves the expense tracker's expense list to a file with the provided path
  public void save(Path path) throws IOException {
    Persistence.saveExpenseTrackerToFile(this, path);
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: reads the file at the provided path as an expense list,
  //          empties this expense tracker,
  //          and calls addExpense for each expense in the file
  public void load(Path path) throws IOException {
    Persistence.restoreExpenseTrackerFromFile(this, path);
  }

}
