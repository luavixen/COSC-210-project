package model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Expense tracker view - represents a selection of expenses that were pulled from an expense tracker
 */
public final class ExpenseTrackerView {

  private final List<Expense> expenses;

  // MODIFIES: NOTHING
  // REQUIRES: that expenses be sorted in ascending order by date
  // EFFECTS: **copies** the given list of expenses and creates an shallow-immutable view over them
  public ExpenseTrackerView(List<Expense> expenses) {
    Objects.requireNonNull(expenses);

    this.expenses = List.copyOf(expenses);

    LocalDate previousDate = null;
    for (Expense expense : this.expenses) {
      if (
        previousDate != null &&
        previousDate.isAfter(expense.getDate())
      ) {
        throw new IllegalStateException("ExpenseTrackerView expenses must be sorted in ascending order by date");
      }
      previousDate = expense.getDate();
    }
  }

  // MODIFIES: NOTHING
  // REQUIRES: that category is not null
  // EFFECTS:
  //   returns a shallow-immutable view of the expenses, filtered by the given category (only expenses with matching category)
  //   throws if no expenses match the given category
  public ExpenseTrackerView filterByCategory(Category category) throws FilterException {
    if (category == null) {
      throw new InvalidArgumentFilterException("Category is null");
    }

    List<Expense> filteredExpenses = expenses
      .stream()
      .filter(expense -> expense.getCategory() == category)
      .toList();

    if (filteredExpenses.isEmpty()) {
      throw new NoResultsFilterException("No expenses found for category: " + category);
    }

    return new ExpenseTrackerView(filteredExpenses);
  }

  // MODIFIES: NOTHING
  // REQUIRES: that startDate and endDate are not null, and startDate is before or equal to endDate
  // EFFECTS:
  //   returns a shallow-immutable view of the expenses, filtered by the given date range (only expenses with dates in the range, inclusive)
  //   throws if no expenses match the given date range, or if startDate is after endDate (invalid)
  public ExpenseTrackerView filterByDateRange(LocalDate startDate, LocalDate endDate) throws FilterException {
    if (startDate == null) {
      throw new InvalidArgumentFilterException("Start date is null");
    }
    if (endDate == null) {
      throw new InvalidArgumentFilterException("End date is null");
    }
    if (!startDate.isBefore(endDate) && !startDate.isEqual(endDate)) {
      throw new InvalidDateRangeFilterException("Start date must be before or equal to end date");
    }

    List<Expense> filteredExpenses = expenses
      .stream()
      .filter(expense ->
        (
          expense.getDate().isAfter(startDate) &&
          expense.getDate().isBefore(endDate)
        )
          || expense.getDate().isEqual(startDate)
          || expense.getDate().isEqual(endDate)
      )
      .toList();

    if (filteredExpenses.isEmpty()) {
      throw new NoResultsFilterException("No expenses found in date range: " + startDate + " to " + endDate);
    }

    return new ExpenseTrackerView(filteredExpenses);
  }

  // MODIFIES: NOTHING
  // REQUIRES: that limit is greater than zero
  // EFFECTS:
  //   returns a shallow-immutable view of the expenses, limited to the given amount (only the first N expenses, in ascending order by date)
  //   throws if no expenses match the given limit, or if limit <= 0 (invalid)
  public ExpenseTrackerView limitToAmount(int limit) throws FilterException {
    if (limit <= 0) {
      throw new InvalidArgumentFilterException("Limit must be greater than zero");
    }

    List<Expense> filteredExpenses = expenses.subList(0, Math.min(limit, expenses.size()));

    if (filteredExpenses.isEmpty()) {
      throw new NoResultsFilterException("No expenses found");
    }

    return new ExpenseTrackerView(filteredExpenses);
  }

  // MODIFIES: NOTHING
  // EFFECTS: returns a new **copied** list of the expenses in this (possibly filtered) view in ascending order by date
  public List<Expense> toList() {
    return List.copyOf(expenses);
  }

}
