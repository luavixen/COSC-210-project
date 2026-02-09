package model;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents something that can be saved to the disk
 */
public interface Saveable {

  void save(Path path) throws IOException;

}
