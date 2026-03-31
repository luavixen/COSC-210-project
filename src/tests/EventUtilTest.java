package tests;

import model.EventLog;
import model.EventUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the EventUtil class
 */
public final class EventUtilTest {

  @BeforeEach
  void resetEventLog() {
    // global mutable state is the enemy of clean code.
    // unfortunately ive written a JVM before!
    // and i know how reflection works!
    // this resets model.EventLog to its initial state between tests:

    try {
      Class<?> realClazz = EventLog.class;

      String name = realClazz.getName();
      String path = name.replace('.', '/') + ".class";

      byte[] bytes;
      try (var in = realClazz.getClassLoader().getResourceAsStream(path)) {
        bytes = in.readAllBytes();
      }

      // just re-load the entire class lmao
      // orphan classloader is perfect for this

      var hackLoader = new ClassLoader(null) {
        @Override
        protected Class<?> findClass(String name) {
          return defineClass(name, bytes, 0, bytes.length);
        }
      };

      Class<?> hackClazz = hackLoader.loadClass(name);

      // and now (attempt to) overwrite the singleton instance's fields

      for (Field hackField : hackClazz.getDeclaredFields()) {
        try {
          hackField.setAccessible(true);
          Field realField = realClazz.getDeclaredField(hackField.getName());
          realField.setAccessible(true);
          realField.set(null, hackField.get(null));
        } catch (Throwable ignored) {}
      }
    } catch (Throwable ignored) {}

    // i could have just hardcoded theLog=null
    // but that wouldn't be good!
    // these tests would break if EventLog's internal implementation changed!
    // so i do this instead. now it doesn't matter if EventLog's implementation changes :3
  }

  private final PrintStream realOut = System.out;
  private final ByteArrayOutputStream mockOut = new ByteArrayOutputStream();

  @BeforeEach
  void setupOutputStream() {
    mockOut.reset();
    System.setOut(new PrintStream(mockOut));
  }

  @AfterEach
  void restoreOutputStream() {
    System.setOut(realOut);
  }

  @Test
  void testGet() {
    assertNotNull(EventUtil.get());
    assertSame(EventLog.getInstance(), EventUtil.get());
  }

  @Test
  void testLog() {
    EventUtil.log("Hello", "world", 1337);
    assertEquals("Hello world 1337", EventUtil.get().iterator().next().getDescription());
  }

  @Test
  void testLog_empty() {
    EventUtil.log();
    assertEquals("", EventUtil.get().iterator().next().getDescription());
  }

  @Test
  void testDump() {
    EventUtil.log("my awesome event");
    EventUtil.log("another event");
    EventUtil.dump();
    String output = mockOut.toString();
    assertTrue(output.contains("my awesome event"));
    assertTrue(output.contains("another event"));
  }

  @Test
  void testDump_empty() {
    EventUtil.dump();
    assertEquals("", mockOut.toString());
  }

}
