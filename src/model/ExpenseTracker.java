package model;

public final class ExpenseTracker {

  public ExpenseTracker() {
    throw new AssertionError();
  }

  // MODIFIES: NOTHING
  // EFFECTS: creates a **copy** of the expense tracker's expense list and returns a view into that state
  public ExpenseTrackerView getExpenses() {
    throw new AssertionError();
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: tries to add an expense to the expense tracker, returns true on success, false on duplicate
  public boolean addExpense(Expense expense) {
    throw new AssertionError();
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: tries to remove an expense from the expense tracker, returns true on success, false on not present
  public boolean deleteExpense(Expense expense) {
    throw new AssertionError();
  }

}
