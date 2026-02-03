package tests;

import model.Category;
import model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public final class ExpenseTests {

  private Expense expense;

  @BeforeEach
  void setUp() {
    expense = new Expense(
      LocalDate.parse("2025-01-01"),
      Category.GROCERIES,
      BigDecimal.valueOf(120),
      "Save-On Foods"
    );
  }

  @Test
  void testGetDate_basic() {
    assertEquals(LocalDate.parse("2025-01-01"), expense.getDate());
  }

  @Test
  void testSetDate_differentDate() {
    expense.setDate(LocalDate.parse("2024-12-25"));
    assertEquals(LocalDate.parse("2024-12-25"), expense.getDate());
  }

  @Test
  void testSetDate_futureDate() {
    expense.setDate(LocalDate.parse("2032-06-15"));
    assertEquals(LocalDate.parse("2032-06-15"), expense.getDate());
  }

  @Test
  void testSetDate_pastDate() {
    expense.setDate(LocalDate.parse("1997-01-01"));
    assertEquals(LocalDate.parse("1997-01-01"), expense.getDate());
  }

  @Test
  void testGetCategory_basic() {
    assertEquals(Category.GROCERIES, expense.getCategory());
  }

  @Test
  void testSetCategory_differentCategory() {
    expense.setCategory(Category.DINING);
    assertEquals(Category.DINING, expense.getCategory());
  }

  @Test
  void testSetCategory_anotherCategory() {
    expense.setCategory(Category.TRAVEL);
    expense.setCategory(Category.ENTERTAINMENT);
    assertEquals(Category.ENTERTAINMENT, expense.getCategory());
  }

  @Test
  void testGetAmount_basic() {
    assertEquals(BigDecimal.valueOf(120), expense.getAmount());
  }

  @Test
  void testSetAmount_zero() {
    expense.setAmount(BigDecimal.ZERO);
    assertEquals(BigDecimal.ZERO, expense.getAmount());
  }

  @Test
  void testSetAmount_positiveValue() {
    expense.setAmount(BigDecimal.valueOf(75));
    assertEquals(BigDecimal.valueOf(75), expense.getAmount());
  }

  @Test
  void testSetAmount_negativeValue() {
    expense.setAmount(BigDecimal.valueOf(-30));
    assertEquals(BigDecimal.valueOf(-30), expense.getAmount());
  }

  @Test
  void testSetAmount_decimalValue() {
    expense.setAmount(BigDecimal.valueOf(13_37, 2));
    assertEquals(BigDecimal.valueOf(13.37), expense.getAmount());
  }

  @Test
  void testGetDescription_basic() {
    assertEquals("Save-On Foods", expense.getDescription());
  }

  @Test
  void testSetDescription_differentDescription() {
    expense.setDescription("Walmart");
    assertEquals("Walmart", expense.getDescription());
  }

  @Test
  void testSetDescription_emptyString() {
    expense.setDescription("");
    assertEquals("", expense.getDescription());
  }

  @Test
  void testSetDescription_longDescription() {
    String description = "I went on a run to the grocery store and got ingredients to make burgers";
    expense.setDescription(description);
    assertEquals(description, expense.getDescription());
  }

  @Test
  void testToString_basic() {
    assertEquals("2025-01-01\tGroceries\t$120.00\tSave-On Foods", expense.toString());
  }

  @Test
  void testToString_emptyDescription() {
    expense.setDescription("");
    assertEquals("2025-01-01\tGroceries\t$120.00\t", expense.toString());
  }

  @Test
  void testToString_longDescription() {
    String description = "I went on a run to the grocery store and got ingredients to make burgers";
    expense.setDescription(description);
    assertEquals("2025-01-01\tGroceries\t$120.00\t" + description, expense.toString());
  }

  @Test
  void testToString_negativeAmount() {
    expense.setAmount(BigDecimal.valueOf(-20));
    assertEquals("2025-01-01\tGroceries\t-$20.00\tSave-On Foods", expense.toString());
  }

}
