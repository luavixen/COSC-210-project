package tests;

import model.Expense;
import model.ExpenseTracker;
import model.KnownCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.Persistence;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Persistence class
 */
public final class PersistenceTests {

  private Expense expense;
  private ExpenseTracker tracker;

  @BeforeEach
  void setUp() {
    expense = new Expense(
      LocalDate.parse("2025-01-15"),
      KnownCategory.EDUCATION,
      BigDecimal.valueOf(45_00, 2),
      "COSC 222 textbook"
    );

    tracker = new ExpenseTracker();

    tracker.addExpense(new Expense(
      LocalDate.parse("2025-01-05"),
      KnownCategory.GROCERIES,
      BigDecimal.valueOf(87_50, 2),
      "Safeway - weekly groceries"
    ));

    tracker.addExpense(new Expense(
      LocalDate.parse("2025-01-10"),
      KnownCategory.TRANSPORTATION,
      BigDecimal.valueOf(100_00, 2),
      "Bus pass"
    ));

    tracker.addExpense(new Expense(
      LocalDate.parse("2025-01-12"),
      KnownCategory.DINING,
      BigDecimal.valueOf(15_75, 2),
      "Lunch at Koi Sushi"
    ));
  }

  @Test
  void testEncodeExpense_basic() {
    JSONObject json = Persistence.encodeExpense(expense);

    assertEquals("2025-01-15", json.getString("date"));
    assertEquals("Education", json.getString("category"));
    assertEquals(BigDecimal.valueOf(45_00, 2), json.getBigDecimal("amount"));
    assertEquals("COSC 222 textbook", json.getString("description"));
  }

  @Test
  void testDecodeExpense_basic() {
    JSONObject json = new JSONObject();
    json.put("date", "2025-01-15");
    json.put("category", "Education");
    json.put("amount", BigDecimal.valueOf(45_00, 2));
    json.put("description", "COSC 222 textbook");

    Expense decoded = Persistence.decodeExpense(json);

    assertEquals(LocalDate.parse("2025-01-15"), decoded.getDate());
    assertEquals(KnownCategory.EDUCATION, decoded.getCategory());
    assertEquals(BigDecimal.valueOf(45_00, 2), decoded.getAmount());
    assertEquals("COSC 222 textbook", decoded.getDescription());
  }

  @Test
  void testEncodeAndDecodeExpense_roundTrip() {
    JSONObject json = Persistence.encodeExpense(expense);
    Expense decoded = Persistence.decodeExpense(json);

    assertEquals(expense.getDate(), decoded.getDate());
    assertEquals(expense.getCategory(), decoded.getCategory());
    assertEquals(expense.getAmount(), decoded.getAmount());
    assertEquals(expense.getDescription(), decoded.getDescription());
  }

  @Test
  void testEncodeExpenseTracker_basic() {
    JSONArray json = Persistence.encodeExpenseTracker(tracker);

    assertEquals(3, json.length());
  }

  @Test
  void testEncodeExpenseTracker_empty() {
    ExpenseTracker emptyTracker = new ExpenseTracker();
    JSONArray json = Persistence.encodeExpenseTracker(emptyTracker);

    assertEquals(0, json.length());
  }

  @Test
  void testEncodeExpenseTracker_preservesOrder() {
    JSONArray json = Persistence.encodeExpenseTracker(tracker);

    assertEquals("2025-01-05", json.getJSONObject(0).getString("date"));
    assertEquals("2025-01-10", json.getJSONObject(1).getString("date"));
    assertEquals("2025-01-12", json.getJSONObject(2).getString("date"));
  }

  @Test
  void testRestoreExpenseTracker_basic() {
    JSONArray json = Persistence.encodeExpenseTracker(tracker);

    ExpenseTracker newTracker = new ExpenseTracker();
    Persistence.restoreExpenseTracker(newTracker, json);

    assertEquals(3, newTracker.getExpenses().toList().size());
    assertEquals(
      tracker.getExpenses().toList().toString(),
      newTracker.getExpenses().toList().toString()
    );
  }

  @Test
  void testRestoreExpenseTracker_replacesExistingExpenses() {
    ExpenseTracker existingTracker = new ExpenseTracker();
    existingTracker.addExpense(new Expense(
      LocalDate.parse("2024-06-01"),
      KnownCategory.RENT,
      BigDecimal.valueOf(1200_00, 2),
      "June rent"
    ));
    existingTracker.addExpense(new Expense(
      LocalDate.parse("2024-06-15"),
      KnownCategory.UTILITIES,
      BigDecimal.valueOf(75_00, 2),
      "Internet bill"
    ));

    JSONArray json = Persistence.encodeExpenseTracker(tracker);
    Persistence.restoreExpenseTracker(existingTracker, json);

    List<Expense> expenses = existingTracker.getExpenses().toList();
    assertEquals(3, expenses.size());
    assertEquals("Safeway - weekly groceries", expenses.get(0).getDescription());
  }

  @Test
  void testRestoreExpenseTracker_empty() {
    JSONArray emptyJson = new JSONArray();
    Persistence.restoreExpenseTracker(tracker, emptyJson);

    assertEquals(0, tracker.getExpenses().toList().size());
  }

  @Test
  void testSaveAndRestoreFromFile() throws IOException {
    Path path = Path.of("./test-persistence.json");
    Files.deleteIfExists(path);

    Persistence.saveExpenseTrackerToFile(tracker, path);

    assertTrue(Files.exists(path));

    ExpenseTracker newTracker = new ExpenseTracker();
    Persistence.restoreExpenseTrackerFromFile(newTracker, path);

    assertEquals(
      tracker.getExpenses().toList().toString(),
      newTracker.getExpenses().toList().toString()
    );

    Files.deleteIfExists(path);
  }

  @Test
  void testSaveAndRestoreFromFile_empty() throws IOException {
    Path path = Path.of("./test-persistence-empty.json");
    Files.deleteIfExists(path);

    ExpenseTracker emptyTracker = new ExpenseTracker();
    Persistence.saveExpenseTrackerToFile(emptyTracker, path);

    Persistence.restoreExpenseTrackerFromFile(tracker, path);

    assertEquals(0, tracker.getExpenses().toList().size());

    Files.deleteIfExists(path);
  }

}
