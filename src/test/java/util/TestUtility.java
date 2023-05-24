package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtility {
  public static String getJsonFromTransactionsFile(String fileName) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(".", fileName));
    return new String(bytes);
  }
}
