package model;

public enum Category {
  GROCERIES,
  TRANSPORTATION,
  HEALTH,
  EDUCATION,
  UTILITIES,
  RENT,
  DINING,
  ENTERTAINMENT,
  TRAVEL,
  OTHER,
  PAYMENT;

  private final String displayName;
  {
    displayName =
      name().charAt(0) +
      name().substring(1).toLowerCase();
  }

  public String getDisplayName() {
    return displayName;
  }
}
