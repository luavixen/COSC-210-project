package model;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class EventUtil {

  private EventUtil() { throw new AssertionError(); }

  public static EventLog get() {
    return EventLog.getInstance();
  }

  public static void log(Object... values) {
    String message = Arrays
      .stream(values)
      .map(String::valueOf)
      .collect(Collectors.joining(" "));
    get().logEvent(new Event(message));
  }

  public static void dump() {
    get().forEach(System.out::println);
  }

  {
    Runtime.getRuntime().addShutdownHook(new Thread(EventUtil::dump));
  }

}
