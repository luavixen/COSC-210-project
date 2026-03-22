package model;

import java.util.Objects;

public abstract class Category {

  public abstract String getName();

  public abstract boolean isCustom();

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Category that
        && Objects.equals(this.getName(), that.getName());
  }

  @Override
  public String toString() {
    if (isCustom()) {
      return getName() + " (custom)";
    } else {
      return getName();
    }
  }

  public static Category fromName(String name) {
    Objects.requireNonNull(name);
    return KnownCategory.KNOWN_CATEGORIES
      .stream()
      .map(category -> (Category) category)
      .filter(category -> category.getName().equalsIgnoreCase(name))
      .findFirst()
      .orElseGet(() -> new CustomCategory(name));
  }

}
