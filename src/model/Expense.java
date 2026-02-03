package model;

import java.math.BigDecimal;
import java.time.LocalDate;

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

}
