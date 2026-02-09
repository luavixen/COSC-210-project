package model;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents something that can be loaded from the disk
 */
public interface Loadable {

  void load(Path path) throws IOException;

}
