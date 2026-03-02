package tests;

import model.Category;
import model.CustomCategory;
import model.KnownCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class CategoryTests {

  @Test
  void testFromName_known() {
    Category category = Category.fromName("Groceries");
    assertFalse(category.isCustom());
    assertEquals("Groceries", category.getName());
  }

  @Test
  void testFromName_unknown() {
    Category category = Category.fromName("My Awesome Category");
    assertTrue(category.isCustom());
    assertEquals("My Awesome Category", category.getName());
  }

  @Test
  void testFromName_null() {
    assertThrows(NullPointerException.class, () -> Category.fromName(null));
  }

  @Test
  void testEquals_matchingKnownAndUnknown() {
    Category knownCategory = KnownCategory.GROCERIES;
    Category unknownCategory = new CustomCategory("Groceries");
    assertEquals(knownCategory, unknownCategory);
  }

}
