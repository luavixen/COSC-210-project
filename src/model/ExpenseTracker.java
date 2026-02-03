package model;

import java.util.ArrayList;
import java.util.Collections;

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

}
