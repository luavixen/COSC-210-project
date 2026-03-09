package model;

/**
 * Thrown when an invalid argument was used to filter expenses
 */
public class InvalidArgumentFilterException extends FilterException {

  public InvalidArgumentFilterException(String message) {
    super(message);
  }

}
