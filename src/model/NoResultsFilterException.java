package model;

/**
 * Thrown when no expenses match a given filter criteria
 */
public final class NoResultsFilterException extends FilterException {

  public NoResultsFilterException(String message) {
    super(message);
  }

}
