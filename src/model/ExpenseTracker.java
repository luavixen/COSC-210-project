package model;

import java.util.LinkedHashSet;
import java.util.List;

public final class ExpenseTracker {

  private final LinkedHashSet<Expense> expenses = new LinkedHashSet<>();

  public ExpenseTracker() {
    // empty constructor
  }

  // MODIFIES: NOTHING
  // EFFECTS: creates a **copy** of the expense tracker's expense list and returns a view into that state
  public ExpenseTrackerView getExpenses() {
    return new ExpenseTrackerView(List.copyOf(expenses));
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: tries to add an expense to the expense tracker, returns true on success, false on duplicate
  public boolean addExpense(Expense expense) {
    return expenses.add(expense);
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: tries to remove an expense from the expense tracker, returns true on success, false on not present
  public boolean deleteExpense(Expense expense) {
    return expenses.remove(expense);
  }

}
