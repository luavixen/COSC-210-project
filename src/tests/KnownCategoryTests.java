package tests;

import model.KnownCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the KnownCategory class to confirm name functionality
 */
public final class KnownCategoryTests {

  @Test
  void testGetName() {
    assertEquals("Groceries", KnownCategory.GROCERIES.getName());
    assertEquals("Transportation", KnownCategory.TRANSPORTATION.getName());
    assertEquals("Health", KnownCategory.HEALTH.getName());
    assertEquals("Education", KnownCategory.EDUCATION.getName());
    assertEquals("Utilities", KnownCategory.UTILITIES.getName());
    assertEquals("Rent", KnownCategory.RENT.getName());
    assertEquals("Dining", KnownCategory.DINING.getName());
    assertEquals("Entertainment", KnownCategory.ENTERTAINMENT.getName());
    assertEquals("Travel", KnownCategory.TRAVEL.getName());
    assertEquals("Payment", KnownCategory.PAYMENT.getName());
  }

  @Test
  void testKnownCategories() {
    assertEquals(10, KnownCategory.KNOWN_CATEGORIES.size());
  }

  @Test
  void testValues() {
    assertEquals(10, KnownCategory.values().length);
    assertArrayEquals(KnownCategory.values(), KnownCategory.KNOWN_CATEGORIES.toArray(new KnownCategory[0]));
  }

  @Test
  void testIsCustom() {
    for (KnownCategory category : KnownCategory.KNOWN_CATEGORIES) {
      assertFalse(category.isCustom());
    }
  }

  @Test
  void testToString() {
    for (KnownCategory category : KnownCategory.KNOWN_CATEGORIES) {
      assertEquals(category.getName(), category.toString());
    }
  }

  @Test
  void testEquals() {
    for (KnownCategory category : KnownCategory.KNOWN_CATEGORIES) {
      assertEquals(category, KnownCategory.fromName(category.getName()));
    }
  }

}
