package model;

import java.util.List;
import java.util.Objects;

/**
 * KnownCategory - represents a well-known expense category like "rent", "utilities", or "groceries"
 */
public final class KnownCategory extends Category {

  private final String name;

  private KnownCategory(String name) {
    Objects.requireNonNull(name);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isCustom() {
    return false;
  }

  public static final KnownCategory GROCERIES = new KnownCategory("Groceries");
  public static final KnownCategory TRANSPORTATION = new KnownCategory("Transportation");
  public static final KnownCategory HEALTH = new KnownCategory("Health");
  public static final KnownCategory EDUCATION = new KnownCategory("Education");
  public static final KnownCategory UTILITIES = new KnownCategory("Utilities");
  public static final KnownCategory RENT = new KnownCategory("Rent");
  public static final KnownCategory DINING = new KnownCategory("Dining");
  public static final KnownCategory ENTERTAINMENT = new KnownCategory("Entertainment");
  public static final KnownCategory TRAVEL = new KnownCategory("Travel");
  public static final KnownCategory PAYMENT = new KnownCategory("Payment");

  public static final List<KnownCategory> KNOWN_CATEGORIES = List.of( // immutable
    GROCERIES, TRANSPORTATION, HEALTH, EDUCATION, UTILITIES, RENT, DINING, ENTERTAINMENT, TRAVEL, PAYMENT
  );

  public static KnownCategory[] values() {
    return KNOWN_CATEGORIES.toArray(new KnownCategory[0]);
  }

}
