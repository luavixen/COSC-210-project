package model;

/**
 * Thrown when an invalid date range was used to filter expenses
 */
public class InvalidDateRangeFilterException extends InvalidArgumentFilterException {

  public InvalidDateRangeFilterException(String message) {
    super(message);
  }

}
