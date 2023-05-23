package com.smallworld;

import com.smallworld.exception.NotFoundException;
import com.smallworld.model.Transaction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import util.TestUtility;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class TransactionDataFetcherTest {

  private static List<Transaction> transactions;
  private static TransactionDataFetcher dataFetcher;

  @BeforeAll
  public static void setup() throws IOException {
    transactions = List.of(TestUtility.loadTransactions("transactions.json"));
    dataFetcher = new TransactionDataFetcher();
  }

  @Test
  void getTotalTransactionAmount() {
    double totalAmount = dataFetcher.getTotalTransactionAmount(transactions);
    Assertions.assertEquals(EXPECTED_TOTAL_AMOUNT, totalAmount);
  }

  @Test
  void getTotalTransactionAmountSentBy() throws NotFoundException {
      double totalAmountSent = dataFetcher.getTotalTransactionAmountSentBy(TEST_CLIENT_1, transactions);
      Assertions.assertEquals(EXPECTED_TOTAL_SENT_AMOUNT_BY_CLIENT1, totalAmountSent);
  }

  @Test
  void getTotalTransactionAmountSentBy_throwsNotFoundException() {
    try {
      dataFetcher.getTotalTransactionAmountSentBy("test_client", transactions);
      Assertions.fail("Expected to throw " + NotFoundException.class);
    } catch (NotFoundException e) {}
  }

  @Test
  void getMaxTransactionAmount() {
    double maxAmount = dataFetcher.getMaxTransactionAmount(transactions);
    Assertions.assertEquals(EXPECTED_MAX_AMOUNT, maxAmount);
  }

  @Test
  void countUniqueClients() {
    long count = dataFetcher.countUniqueClients(transactions);
    Assertions.assertEquals(UNIQUE_CLIENT_COUNT, count);
  }

  @Test
  void hasOpenComplianceIssues() throws NotFoundException {
    boolean hasIssues = dataFetcher.hasOpenComplianceIssues(TEST_CLIENT_1, transactions);
    Assertions.assertTrue(hasIssues);
  }

  @Test
  void hasOpenComplianceIssues_clientNotFoundException() {
    try {
      dataFetcher.hasOpenComplianceIssues("test client", transactions);
      Assertions.fail("Expected to throw " + NotFoundException.class);
    } catch (NotFoundException ignored) {}
  }

  @Test
  void getTransactionsByBeneficiaryName() {
    Map<String, List<Transaction>> beneficiaryToTransactionsMap = dataFetcher.getTransactionsByBeneficiaryName(DUMMY_TRANSACTION_LIST);
    for(Map.Entry<String, List<Transaction>> beneficiaryToTransactionsPair : EXPECTED_BENEFICIARY_TO_TRANSACTIONS_MAP.entrySet()) {
      String expectedBeneficiary = beneficiaryToTransactionsPair.getKey();
      List<Transaction> expectedTransactionsList = beneficiaryToTransactionsPair.getValue();
      Collections.sort(expectedTransactionsList, Comparator.comparing(Transaction::transactionID));

      Assertions.assertTrue(beneficiaryToTransactionsMap.containsKey(expectedBeneficiary));
      List<Transaction> actualTransactionsList = beneficiaryToTransactionsMap.get(expectedBeneficiary);
      Collections.sort(actualTransactionsList,
          Comparator.comparing(Transaction::transactionID));
      Assertions.assertEquals(actualTransactionsList, expectedTransactionsList);
    }
  }

  @Test
  void getUnsolvedIssueIds() {
    Set<Integer> unresolvedIssueIDs = dataFetcher.getUnsolvedIssueIds(transactions);
    Assertions.assertEquals(UNRESOLVED_ISSUE_IDs, unresolvedIssueIDs);
  }

  @Test
  void getAllSolvedIssueMessages() {
    List<String> resolvedTranMessages = dataFetcher.getAllSolvedIssueMessages(transactions);
    Assertions.assertEquals(RESOLVED_ISSUE_MSGs, resolvedTranMessages);
  }

  @Test
  void getTop3TransactionsByAmount() {
    List<Double> max3TransactionAmounts = dataFetcher.getTop3TransactionsByAmount(transactions);
    Assertions.assertEquals(EXPECTED_MAX_3_TRAN_AMOUNTS, max3TransactionAmounts);
  }

  @Test
  void getTopSender() {
    Optional<String> topSenderClient = dataFetcher.getTopSender(transactions);
    Assertions.assertEquals(EXPECTED_TOP_SENDER_CLIENT, topSenderClient.get());
  }


  private static final double EXPECTED_TOTAL_AMOUNT = 2889.17;
  private static final String TEST_CLIENT_1 = "Grace Burgess";
  private static final double EXPECTED_TOTAL_SENT_AMOUNT_BY_CLIENT1 = 666.0;
  private static final double EXPECTED_MAX_AMOUNT = 985.0;
  private static final long UNIQUE_CLIENT_COUNT = 14L;
  private static final String TEST_CLIENT_2 = "Michael Gray";
  private static final Set<Integer> UNRESOLVED_ISSUE_IDs = new LinkedHashSet<>(Arrays.asList(1,3,15,54,99));
  private static final List<String> RESOLVED_ISSUE_MSGs = Arrays.asList("Never gonna give you up",
      "Never gonna let you down",
      "Never gonna run around and desert you"
      );
  private static final List<Double> EXPECTED_MAX_3_TRAN_AMOUNTS = Arrays.asList(985.0,666.0,430.2);
  private static final String EXPECTED_TOP_SENDER_CLIENT = "Arthur Shelby";
  private static final List<Transaction> DUMMY_TRANSACTION_LIST = Arrays.asList(
      new Transaction(663458L, 430.2
          , "Tom Shelby", 22, "Alfie Solomons",
          33, 1, false, "Looks like money laundering"),
      new Transaction(1284564L, 150.2,
          "Tom Shelby", 22, "Arthur Shelby", 60,
          2, true, "Never gonna give you up"),
      new Transaction(1284564L, 150.2,
          "Tom Shelby", 22, "Arthur Shelby",
          60,3, false,"Looks like money laundering"),
      new Transaction(96132456L, 67.8,
          "Aunt Polly", 34, "Aberama Gold", 58,
          null, true, null),
      new Transaction(5465465L, 985.0,
          "Arthur Shelby", 60, "Ben Younger", 47,
          15, false, "Something's fishy"),
      new Transaction(1651665L, 97.6,
          "Tom Shelby", 22 , "Oswald Mosley", 37,
          65, true, "Never gonna let you down")
  );
  private static final Map<String, List<Transaction>> EXPECTED_BENEFICIARY_TO_TRANSACTIONS_MAP =
      new HashMap<>() {{
        put("Alfie Solomons", Arrays.asList( new Transaction(663458L, 430.2
            , "Tom Shelby", 22, "Alfie Solomons",
            33, 1, false, "Looks like money laundering") ));
        put("Arthur Shelby", Arrays.asList(new Transaction(1284564L, 150.2,
            "Tom Shelby", 22, "Arthur Shelby", 60,
            2, true, "Never gonna give you up")));
        put("Aberama Gold", Arrays.asList(new Transaction(96132456L, 67.8,
            "Aunt Polly", 34, "Aberama Gold", 58,
            null, true, null)));
        put("Ben Younger", Arrays.asList(new Transaction(5465465L, 985.0,
            "Arthur Shelby", 60, "Ben Younger", 47,
            15, false, "Something's fishy")));
        put("Oswald Mosley", Arrays.asList(new Transaction(1651665L, 97.6,
            "Tom Shelby", 22 , "Oswald Mosley", 37,
            65, true, "Never gonna let you down")));
      }};

}