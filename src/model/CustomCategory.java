package model;

import java.util.Objects;

/**
 * CustomCategory - represents a custom, user-specified expense category
 */
public final class CustomCategory extends Category {

  private final String name;

  public CustomCategory(String name) {
    Objects.requireNonNull(name);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isCustom() {
    return true;
  }

}
