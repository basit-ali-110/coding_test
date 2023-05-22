package util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallworld.model.Transaction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtility {

  public static Transaction[] loadTransactions(String fileName) throws IOException {
    String json = getJsonFromTransactionsFile(fileName);
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(json, Transaction[].class);
  }

  private static String getJsonFromTransactionsFile(String fileName) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(".", fileName));
    return new String(bytes);
  }
}
