package model;

import java.time.LocalDate;
import java.util.List;

public final class ExpenseTrackerView {

  private final List<Expense> expenses;

  // MODIFIES: NOTHING
  // EFFECTS: **copies** the given list of expenses and creates an shallow-immutable view over them
  // REQUIRES: that expenses be sorted in ascending order by date
  public ExpenseTrackerView(List<Expense> expenses) {
    this.expenses = List.copyOf(expenses);

    LocalDate previousDate = null;
    for (Expense expense : this.expenses) {
      if (
        previousDate != null &&
        previousDate.isAfter(expense.getDate())
      ) {
        throw new IllegalArgumentException("Expenses must be sorted in ascending order by date");
      }
      previousDate = expense.getDate();
    }
  }

  // MODIFIES: NOTHING
  // EFFECTS: returns a shallow-immutable view of the expenses, filtered by the given category (only expenses with matching category)
  public ExpenseTrackerView filterByCategory(Category category) {
    return new ExpenseTrackerView(
      expenses
        .stream()
        .filter(expense -> expense.getCategory() == category)
        .toList()
    );
  }

  // MODIFIES: NOTHING
  // REQUIRES: that startDate is equal to or before endDate
  // EFFECTS: returns a shallow-immutable view of the expenses, filtered by the given date range (only expenses with dates in the range, inclusive)
  public ExpenseTrackerView filterByDateRange(LocalDate startDate, LocalDate endDate) {
    if (!startDate.isBefore(endDate) && !startDate.isEqual(endDate)) {
      throw new IllegalArgumentException("Start date must be before or equal to end date");
    }

    return new ExpenseTrackerView(
      expenses
        .stream()
        .filter(expense ->
          (
            expense.getDate().isAfter(startDate) &&
            expense.getDate().isBefore(endDate)
          )
          || expense.getDate().isEqual(startDate)
          || expense.getDate().isEqual(endDate)
        )
        .toList()
    );
  }

  // MODIFIES: NOTHING
  // REQUIRES: that limit >= 0
  // EFFECTS: returns a shallow-immutable view of the expenses, limited to the given amount (only the first N expenses, in ascending order by date)
  public ExpenseTrackerView limitToAmount(int limit) {
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be non-negative");
    }

    return new ExpenseTrackerView(expenses.subList(0, Math.min(limit, expenses.size())));
  }

  // MODIFIES: NOTHING
  // EFFECTS: returns a new **copied** list of the expenses in this (possibly filtered) view in ascending order by date
  public List<Expense> toList() {
    return List.copyOf(expenses);
  }

}
