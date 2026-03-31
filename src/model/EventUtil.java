package model;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class EventUtil {

  private EventUtil() { throw new AssertionError(); }

  public static EventLog get() {
    registerShutdownHook();
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
    System.out.flush();
  }

  private static final AtomicBoolean shutdownHookRegistered = new AtomicBoolean(false);

  private static void registerShutdownHook() {
    if (shutdownHookRegistered.compareAndSet(false, true)) {
      Runtime.getRuntime().addShutdownHook(new Thread(EventUtil::dump));
    }
  }

}
