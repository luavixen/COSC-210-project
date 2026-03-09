package model;

/**
 * Thrown when expenses cannot be filtered by a given criteria
 */
public abstract class FilterException extends Exception {

  public FilterException(String message) {
    super(message);
  }

}
