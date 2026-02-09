package tests;

import model.Category;
import model.Expense;
import model.ExpenseTrackerView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ExpenseTrackerView class
 */
public final class ExpenseTrackerViewTests {

  @Test
  void testCtor_null() {
    assertThrows(NullPointerException.class, () -> new ExpenseTrackerView(null));
  }

  @Test
  void testCtor_empty() {
    ExpenseTrackerView view = new ExpenseTrackerView(List.of());
    assertEquals(0, view.toList().size());
  }

  @Test
  void testCtor_mutableListIsCopied() {
    ArrayList<Expense> mutableList = new ArrayList<>();

    mutableList.add(new Expense(
      LocalDate.parse("2024-05-05"),
      Category.TRAVEL,
      BigDecimal.valueOf(380_23, 2),
      "Air Canada"
    ));

    ExpenseTrackerView view = new ExpenseTrackerView(mutableList);

    assertNotSame(mutableList, view.toList()); // reference equality
    assertEquals(mutableList, view.toList()); // .equals() method

    assertEquals(1, mutableList.size());
    assertEquals(1, view.toList().size());

    mutableList.removeFirst();

    assertEquals(0, mutableList.size());
    assertEquals(1, view.toList().size());
  }

  @Test
  void testCtor_throwsOnUnsortedList() {
    List<Expense> unsortedList = List.of(
      new Expense(LocalDate.parse("2024-09-01"), Category.RENT, BigDecimal.valueOf(1200_00, 2), "All Start Ct"),
      new Expense(LocalDate.parse("2024-08-02"), Category.GROCERIES, BigDecimal.valueOf(180_00, 2), "Loblaws")
    );
    assertThrows(IllegalArgumentException.class, () -> new ExpenseTrackerView(unsortedList));
  }

  private ExpenseTrackerView expenseTrackerView;

  @BeforeEach
  void setUp() {
    Expense groceryExpense = new Expense(
      LocalDate.parse("2024-08-02"),
      Category.GROCERIES,
      BigDecimal.valueOf(180_00, 2),
      "Safeway"
    );
    Expense rentExpense = new Expense(
      LocalDate.parse("2024-09-01"),
      Category.RENT,
      BigDecimal.valueOf(2300_00, 2),
      "Andrew Apartments"
    );
    Expense firstFoodExpense = new Expense(
      LocalDate.parse("2024-08-11"),
      Category.DINING,
      BigDecimal.valueOf(12_72, 2),
      "McDonald's"
    );
    Expense secondFoodExpense = new Expense(
      LocalDate.parse("2024-08-24"),
      Category.DINING,
      BigDecimal.valueOf(16_02, 2),
      "Subway"
    );
    Expense paymentExpense = new Expense(
      LocalDate.parse("2024-08-28"),
      Category.PAYMENT,
      BigDecimal.valueOf(200_00, 2),
      "Payment to Credit Card"
    );

    // NOTE: expenseList **MUST** be ordered by date
    // NOTE: expenseList is expected to be immutable
    List<Expense> expenseList = List.of(
      groceryExpense,
      firstFoodExpense,
      secondFoodExpense,
      paymentExpense,
      rentExpense
    );

    expenseTrackerView = new ExpenseTrackerView(expenseList);
  }

  @Test
  void testToList_size() {
    List<Expense> expenses = expenseTrackerView.toList();
    assertEquals(5, expenses.size());
  }

  @Test
  void testToList_firstAndLastDateOrder() {
    List<Expense> expenses = expenseTrackerView.toList();
    assertEquals(LocalDate.parse("2024-08-02"), expenses.getFirst().getDate());
    assertEquals(LocalDate.parse("2024-09-01"), expenses.getLast().getDate());
  }

  @Test
  void testToList_cannotMutateView() {
    Expense newExpense = new Expense(
      LocalDate.parse("2026-02-02"),
      Category.OTHER,
      BigDecimal.valueOf(20),
      "Gave my friend $20"
    );

    try {
      expenseTrackerView.toList().add(newExpense);
    } catch (UnsupportedOperationException ignored) {}

    assertFalse(expenseTrackerView.toList().contains(newExpense));

    try {
      expenseTrackerView.toList().removeLast();
    } catch (UnsupportedOperationException ignored) {}

    assertEquals(5, expenseTrackerView.toList().size());
  }

  @Test
  void testToList_ableToMutateContainedExpenses() {
    Expense expense = expenseTrackerView.toList().getFirst();
    expense.setAmount(BigDecimal.valueOf(1234_56, 2));
    assertEquals(BigDecimal.valueOf(1234_56, 2), expenseTrackerView.toList().getFirst().getAmount());
  }

  @Test
  void testLimitToAmount_basic() {
    ExpenseTrackerView firstThreeExpenses = expenseTrackerView.limitToAmount(3);
    assertEquals(3, firstThreeExpenses.toList().size());
  }

  @Test
  void testLimitToAmount_none() {
    ExpenseTrackerView noExpenses = expenseTrackerView.limitToAmount(0);
    assertEquals(0, noExpenses.toList().size());
  }

  @Test
  void testLimitToAmount_all() {
    ExpenseTrackerView allExpenses = expenseTrackerView.limitToAmount(100);
    assertEquals(5, allExpenses.toList().size());
  }

  @Test
  void testLimitToAmount_negative() {
    assertThrows(IllegalArgumentException.class, () -> expenseTrackerView.limitToAmount(-1));
  }

  @Test
  void testFilterByCategory_basic() {
    ExpenseTrackerView diningExpenses = expenseTrackerView.filterByCategory(Category.DINING);
    assertEquals(2, diningExpenses.toList().size());

    for (Expense expense : diningExpenses.toList()) {
      assertEquals(Category.DINING, expense.getCategory());
    }
  }

  @Test
  void testFilterByCategory_singleResult() {
    ExpenseTrackerView rentExpenses = expenseTrackerView.filterByCategory(Category.RENT);
    assertEquals(1, rentExpenses.toList().size());
    assertEquals(Category.RENT, rentExpenses.toList().getFirst().getCategory());
  }

  @Test
  void testFilterByCategory_noResults() {
    ExpenseTrackerView healthExpenses = expenseTrackerView.filterByCategory(Category.HEALTH);
    assertEquals(0, healthExpenses.toList().size());
  }

  @Test
  void testFilterByCategory_chainedFilteringSame() {
    ExpenseTrackerView diningExpenses = expenseTrackerView
      .filterByCategory(Category.DINING)
      .filterByCategory(Category.DINING);
    assertEquals(2, diningExpenses.toList().size());
  }

  @Test
  void testFilterByCategory_chainedFilteringToNothing() {
    ExpenseTrackerView diningExpenses = expenseTrackerView
      .filterByCategory(Category.DINING)
      .filterByCategory(Category.TRAVEL);
    assertEquals(0, diningExpenses.toList().size());
  }

  @Test
  void testFilterByDateRange_basic() {
    ExpenseTrackerView augustExpenses = expenseTrackerView.filterByDateRange(
      LocalDate.parse("2024-08-01"),
      LocalDate.parse("2024-08-31")
    );

    assertEquals(4, augustExpenses.toList().size());

    for (Expense expense : augustExpenses.toList()) {
      assertTrue(expense.getDate().isAfter(LocalDate.parse("2024-07-31")));
      assertTrue(expense.getDate().isBefore(LocalDate.parse("2024-09-01")));
    }
  }

  @Test
  void testFilterByDateRange_noResults() {
    ExpenseTrackerView noExpenses = expenseTrackerView.filterByDateRange(
      LocalDate.parse("2024-10-01"),
      LocalDate.parse("2024-10-31")
    );
    assertEquals(0, noExpenses.toList().size());
  }

  @Test
  void testFilterByDateRange_all() {
    ExpenseTrackerView allExpenses = expenseTrackerView.filterByDateRange(
      LocalDate.parse("2024-01-01"),
      LocalDate.parse("2024-12-31")
    );
    assertEquals(5, allExpenses.toList().size());
  }

  @Test
  void testFilterByDateRange_startEqualsEnd() {
    ExpenseTrackerView singleDayExpenses = expenseTrackerView.filterByDateRange(
      LocalDate.parse("2024-08-11"),
      LocalDate.parse("2024-08-11")
    );
    assertEquals(1, singleDayExpenses.toList().size());
    assertEquals(LocalDate.parse("2024-08-11"), singleDayExpenses.toList().getFirst().getDate());
  }

  @Test
  void testFilterByDateRange_endBeforeStart() {
    assertThrows(IllegalArgumentException.class, () -> expenseTrackerView.filterByDateRange(
      LocalDate.parse("2024-08-20"),
      LocalDate.parse("2024-08-10")
    ));
  }

  @Test
  void testChainedFilters_categoryThenDate() {
    ExpenseTrackerView filteredExpenses = expenseTrackerView
      .filterByCategory(Category.DINING)
      .filterByDateRange(LocalDate.parse("2024-08-01"), LocalDate.parse("2024-08-20"));

    assertEquals(1, filteredExpenses.toList().size());
    assertEquals(Category.DINING, filteredExpenses.toList().getFirst().getCategory());
    assertEquals(LocalDate.parse("2024-08-11"), filteredExpenses.toList().getFirst().getDate());
  }

  @Test
  void testChainedFilters_dateThenCategory() {
    ExpenseTrackerView filteredExpenses = expenseTrackerView
      .filterByDateRange(LocalDate.parse("2024-08-01"), LocalDate.parse("2024-08-20"))
      .filterByCategory(Category.DINING);

    assertEquals(1, filteredExpenses.toList().size());
    assertEquals(Category.DINING, filteredExpenses.toList().getFirst().getCategory());
    assertEquals(LocalDate.parse("2024-08-11"), filteredExpenses.toList().getFirst().getDate());
  }

  @Test
  void testChainedFilters_withLimit() {
    ExpenseTrackerView filteredExpenses = expenseTrackerView
      .filterByDateRange(LocalDate.parse("2024-08-01"), LocalDate.parse("2024-08-31"))
      .limitToAmount(2);

    assertEquals(2, filteredExpenses.toList().size());
    assertEquals(LocalDate.parse("2024-08-02"), filteredExpenses.toList().getFirst().getDate());
    assertEquals(LocalDate.parse("2024-08-11"), filteredExpenses.toList().getLast().getDate());
  }

  @Test
  void testChainedFilters_complex() {
    ExpenseTrackerView filteredExpenses = expenseTrackerView
      .filterByCategory(Category.DINING)
      .filterByDateRange(LocalDate.parse("2024-08-01"), LocalDate.parse("2024-08-31"))
      .limitToAmount(1);

    assertEquals(1, filteredExpenses.toList().size());
    assertEquals(Category.DINING, filteredExpenses.toList().getFirst().getCategory());
    assertEquals("McDonald's", filteredExpenses.toList().getFirst().getDescription());
  }

}
