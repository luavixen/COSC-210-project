package tests;

import model.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CategoryTests {

  @Test
  void testGetDisplayName() {
    assertEquals("Groceries", Category.GROCERIES.getDisplayName());
    assertEquals("Transportation", Category.TRANSPORTATION.getDisplayName());
    assertEquals("Health", Category.HEALTH.getDisplayName());
    assertEquals("Education", Category.EDUCATION.getDisplayName());
    assertEquals("Utilities", Category.UTILITIES.getDisplayName());
    assertEquals("Rent", Category.RENT.getDisplayName());
    assertEquals("Dining", Category.DINING.getDisplayName());
    assertEquals("Entertainment", Category.ENTERTAINMENT.getDisplayName());
    assertEquals("Travel", Category.TRAVEL.getDisplayName());
    assertEquals("Other", Category.OTHER.getDisplayName());
    assertEquals("Payment", Category.PAYMENT.getDisplayName());
  }

}
