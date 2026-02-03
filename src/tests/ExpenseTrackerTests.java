package tests;

import model.Category;
import model.Expense;
import model.ExpenseTracker;
import model.ExpenseTrackerView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class ExpenseTrackerTests {

  private ExpenseTracker expenseTracker;

  @BeforeEach
  void setUp() {
    expenseTracker = new ExpenseTracker();

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-01-05"),
      Category.GROCERIES,
      BigDecimal.valueOf(87_50, 2),
      "Safeway - weekly groceries"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-01-10"),
      Category.TRANSPORTATION,
      BigDecimal.valueOf(100_00, 2),
      "U-Pass monthly"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-01-12"),
      Category.DINING,
      BigDecimal.valueOf(15_75, 2),
      "Lunch at campus cafeteria"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-01-15"),
      Category.EDUCATION,
      BigDecimal.valueOf(45_00, 2),
      "COSC 210 textbook"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-01-18"),
      Category.ENTERTAINMENT,
      BigDecimal.valueOf(22_00, 2),
      "Movie tickets with friends"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-01-20"),
      Category.GROCERIES,
      BigDecimal.valueOf(62_30, 2),
      "Save-On Foods"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-01-22"),
      Category.UTILITIES,
      BigDecimal.valueOf(75_00, 2),
      "Internet bill"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-01-25"),
      Category.HEALTH,
      BigDecimal.valueOf(30_00, 2),
      "Pharmacy - cold medicine"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-02-01"),
      Category.RENT,
      BigDecimal.valueOf(1200_00, 2),
      "February rent"
    ));

    expenseTracker.addExpense(new Expense(
      LocalDate.parse("2025-02-03"),
      Category.DINING,
      BigDecimal.valueOf(18_50, 2),
      "Starbucks - study session"
    ));
  }

  @Test
  void testGetExpenses_basic() {
    ExpenseTrackerView expenses = expenseTracker.getExpenses();
    assertEquals(10, expenses.toList().size());
  }

  @Test
  void testGetExpenses_empty() {
    ExpenseTracker emptyTracker = new ExpenseTracker();
    ExpenseTrackerView expenses = emptyTracker.getExpenses();
    assertEquals(0, expenses.toList().size());
  }

  @Test
  void testGetExpenses_viewIsImmutableCopy() {
    ExpenseTrackerView expenses1 = expenseTracker.getExpenses();

    expenseTracker.deleteExpense(expenses1.toList().getFirst());

    ExpenseTrackerView expenses2 = expenseTracker.getExpenses();

    assertEquals(10, expenses1.toList().size());
    assertEquals(9, expenses2.toList().size());
  }

  @Test
  void testGetExpenses_immutableViewCopyIsShallow() {
    ExpenseTrackerView expenses1 = expenseTracker.getExpenses();
    ExpenseTrackerView expenses2 = expenseTracker.getExpenses();

    assertEquals(expenses1.toList(), expenses2.toList());

    expenses1.toList().getFirst().setAmount(BigDecimal.valueOf(1234_56, 2));

    assertEquals(BigDecimal.valueOf(1234_56, 2), expenses2.toList().getFirst().getAmount());

    assertEquals(expenses1.toList().getFirst(), expenses2.toList().getFirst());
  }

  @Test
  void testAddExpense_basic() {
    ExpenseTracker tracker = new ExpenseTracker();
    Expense newExpense = new Expense(
      LocalDate.parse("2025-03-01"),
      Category.OTHER,
      BigDecimal.valueOf(25_00, 2),
      "Test expense"
    );

    assertTrue(tracker.addExpense(newExpense));
    assertEquals(1, tracker.getExpenses().toList().size());
  }

  @Test
  void testAddExpense_multiple() {
    ExpenseTracker tracker = new ExpenseTracker();
    Expense expense1 = new Expense(
      LocalDate.parse("2025-03-01"),
      Category.GROCERIES,
      BigDecimal.valueOf(50_00, 2),
      "Groceries"
    );
    Expense expense2 = new Expense(
      LocalDate.parse("2025-03-02"),
      Category.DINING,
      BigDecimal.valueOf(15_00, 2),
      "Lunch"
    );

    assertTrue(tracker.addExpense(expense1));
    assertTrue(tracker.addExpense(expense2));
    assertEquals(2, tracker.getExpenses().toList().size());
  }

  @Test
  void testAddExpense_duplicate() {
    ExpenseTracker tracker = new ExpenseTracker();
    Expense expense = new Expense(
      LocalDate.parse("2025-03-01"),
      Category.OTHER,
      BigDecimal.valueOf(100_00, 2),
      "Test"
    );

    assertTrue(tracker.addExpense(expense));
    assertFalse(tracker.addExpense(expense));
    assertEquals(1, tracker.getExpenses().toList().size());
  }

  @Test
  void testAddExpense_duplicateButNotEqual() {
    ExpenseTracker tracker = new ExpenseTracker();
    Expense expense1 = new Expense(
      LocalDate.parse("2025-03-01"),
      Category.OTHER,
      BigDecimal.valueOf(100_00, 2),
      "Test"
    );
    Expense expense2 = new Expense(
      LocalDate.parse("2025-03-01"),
      Category.OTHER,
      BigDecimal.valueOf(100_00, 2),
      "Test"
    );

    assertTrue(tracker.addExpense(expense1));
    assertTrue(tracker.addExpense(expense2));
    assertEquals(2, tracker.getExpenses().toList().size());
  }

  @Test
  void testAddExpense_addedOutOfOrder() {
    ExpenseTracker tracker = new ExpenseTracker();
    Expense expense1 = new Expense(
      LocalDate.parse("2025-03-01"),
      Category.GROCERIES,
      BigDecimal.valueOf(50_00, 2),
      "Groceries"
    );
    Expense expense2 = new Expense(
      LocalDate.parse("2025-03-02"),
      Category.DINING,
      BigDecimal.valueOf(15_00, 2),
      "Lunch"
    );

    assertTrue(tracker.addExpense(expense2)); // 2nd expense first !!
    assertTrue(tracker.addExpense(expense1)); // 1st expense second !!

    List<Expense> expenses = tracker.getExpenses().toList(); // should NOT fail

    assertEquals(expense1, expenses.get(0));
    assertEquals(expense2, expenses.get(1));
  }

  @Test
  void testDeleteExpense_basic() {
    List<Expense> expenses = expenseTracker.getExpenses().toList();
    Expense expenseToDelete = expenses.getFirst();

    assertTrue(expenseTracker.deleteExpense(expenseToDelete));
    assertEquals(9, expenseTracker.getExpenses().toList().size());
  }

  @Test
  void testDeleteExpense_multiple() {
    List<Expense> expenses = expenseTracker.getExpenses().toList();
    Expense firstExpense = expenses.get(0);
    Expense secondExpense = expenses.get(1);

    assertTrue(expenseTracker.deleteExpense(firstExpense));
    assertTrue(expenseTracker.deleteExpense(secondExpense));
    assertEquals(8, expenseTracker.getExpenses().toList().size());
  }

  @Test
  void testDeleteExpense_notPresent() {
    Expense nonExistentExpense = new Expense(
      LocalDate.parse("2099-12-31"),
      Category.OTHER,
      BigDecimal.valueOf(999_99, 2),
      "Non-existent expense"
    );

    assertFalse(expenseTracker.deleteExpense(nonExistentExpense));
    assertEquals(10, expenseTracker.getExpenses().toList().size());
  }

  @Test
  void testDeleteExpense_alreadyDeleted() {
    List<Expense> expenses = expenseTracker.getExpenses().toList();
    Expense expense = expenses.getFirst();

    assertTrue(expenseTracker.deleteExpense(expense));
    assertFalse(expenseTracker.deleteExpense(expense));
    assertEquals(9, expenseTracker.getExpenses().toList().size());
  }

  @Test
  void testDeleteExpense_duplicateButNotEqual() {
    ExpenseTracker tracker = new ExpenseTracker();

    Expense expense1 = new Expense(
      LocalDate.parse("2025-03-01"),
      Category.OTHER,
      BigDecimal.valueOf(100_00, 2),
      "Test"
    );

    assertTrue(tracker.addExpense(expense1));

    Expense expense2 = new Expense(
      LocalDate.parse("2025-03-01"),
      Category.OTHER,
      BigDecimal.valueOf(100_00, 2),
      "Test"
    );

    assertFalse(tracker.deleteExpense(expense2));

    assertEquals(1, tracker.getExpenses().toList().size());
  }

}
