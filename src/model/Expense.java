package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public final class Expense {

  private LocalDate date;
  private Category category;
  private BigDecimal amount;
  private String description;

  public Expense(LocalDate date, Category category, BigDecimal amount, String description) {
    this.date = date;
    this.category = category;
    this.amount = amount;
    this.description = description;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return String.format(
      "%s\t%s\t%s\t%s",
      date,
      category.getDisplayName(),
      NumberFormat.getCurrencyInstance(Locale.CANADA).format(amount),
      description
    );
  }

}
