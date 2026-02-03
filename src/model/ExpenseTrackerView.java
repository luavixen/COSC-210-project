package model;

import java.time.LocalDate;
import java.util.List;

public final class ExpenseTrackerView {

  // MODIFIES: NOTHING
  // EFFECTS: **copies** the given list of expenses and creates an shallow-immutable view over them
  // REQUIRES: that expenses be sorted in ascending order by date
  public ExpenseTrackerView(List<Expense> expenses) {
    throw new AssertionError();
  }

  // MODIFIES: NOTHING
  // EFFECTS: returns a shallow-immutable view of the expenses, filtered by the given category (only expenses with matching category)
  public ExpenseTrackerView filterByCategory(Category category) {
    throw new AssertionError();
  }

  // MODIFIES: NOTHING
  // REQUIRES: that startDate is equal to or before endDate
  // EFFECTS: returns a shallow-immutable view of the expenses, filtered by the given date range (only expenses with dates in the range, inclusive)
  public ExpenseTrackerView filterByDateRange(LocalDate startDate, LocalDate endDate) {
    throw new AssertionError();
  }

  // MODIFIES: NOTHING
  // REQUIRES: that limit >= 0
  // EFFECTS: returns a shallow-immutable view of the expenses, limited to the given amount (only the first N expenses, in ascending order by date)
  public ExpenseTrackerView limitToAmount(int limit) {
    throw new AssertionError();
  }

  // MODIFIES: NOTHING
  // EFFECTS: returns a new **copied** list of the expenses in this (possibly filtered) view in ascending order by date
  public List<Expense> toList() {
    throw new AssertionError();
  }

}
