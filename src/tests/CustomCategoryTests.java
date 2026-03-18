package tests;

import model.CustomCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CustomCategory class
 */
public final class CustomCategoryTests {

  @Test
  void testGetName() {
    CustomCategory category = new CustomCategory("Test Category");
    assertEquals("Test Category", category.getName());
  }

  @Test
  void testIsCustom() {
    CustomCategory category = new CustomCategory("Test Category");
    assertTrue(category.isCustom());
  }

  @Test
  void testToString() {
    CustomCategory category = new CustomCategory("Test Category");
    assertEquals("Test Category (custom)", category.toString());
  }

  @Test
  void testEquals() {
    CustomCategory category1 = new CustomCategory("My Awesome Category");
    CustomCategory category2 = new CustomCategory("My Awesome Category");
    CustomCategory category3 = new CustomCategory("Other Category");
    assertEquals(category1, category2);
    assertNotEquals(category3, category1);
    assertNotEquals(category3, category2);
  }

}
