package com.smallworld;

import com.smallworld.exception.NotFoundException;
import com.smallworld.model.Issue;
import com.smallworld.model.Transaction;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import util.TestUtility;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class TransactionDataFetcherTest {
  private static TransactionDataFetcher dataFetcher;

  @BeforeAll
  public static void setup() throws IOException {
    String json = TestUtility.getJsonFromTransactionsFile("transactions.json");
    Collection<Transaction> transactions = TransactionLoader.loadTransactions(json);
    dataFetcher = new TransactionDataFetcher(transactions);
  }

  @Test
  void getTotalTransactionAmount() {
    BigDecimal totalAmount = dataFetcher.getTotalTransactionAmount();
    Assertions.assertEquals(EXPECTED_TOTAL_AMOUNT, totalAmount);
  }

  @Test
  void getTotalTransactionAmountSentBy() throws NotFoundException {
      BigDecimal totalAmountSent = dataFetcher.getTotalTransactionAmountSentBy(TEST_CLIENT_1);
      Assertions.assertEquals(EXPECTED_TOTAL_SENT_AMOUNT_BY_CLIENT1, totalAmountSent);
  }

  @Test
  void getTotalTransactionAmountSentBy_throwsNotFoundException() {
    try {
      dataFetcher.getTotalTransactionAmountSentBy("test_client");
      Assertions.fail("Expected to throw " + NotFoundException.class);
    } catch (NotFoundException e) {}
  }

  @Test
  void getMaxTransactionAmount() {
    BigDecimal maxAmount = dataFetcher.getMaxTransactionAmount();
    Assertions.assertEquals(EXPECTED_MAX_AMOUNT, maxAmount);
  }

  @Test
  void countUniqueClients() {
    long count = dataFetcher.countUniqueClients();
    Assertions.assertEquals(UNIQUE_CLIENT_COUNT, count);
  }

  @Test
  void hasOpenComplianceIssues() throws NotFoundException {
    boolean hasIssues = dataFetcher.hasOpenComplianceIssues(TEST_CLIENT_1);
    Assertions.assertTrue(hasIssues);
  }

  @Test
  void hasOpenComplianceIssues_clientNotFoundException() {
      Assertions.assertThrows(NotFoundException.class, () -> dataFetcher.hasOpenComplianceIssues("test client"));
  }

  @Test
  void getTransactionsByBeneficiaryName() {
    Map<String, Collection<Transaction>> beneficiaryToTransactionsMap = dataFetcher.getTransactionsByBeneficiaryName();
    for(Map.Entry<String, Collection<Transaction>> beneficiaryToTransactionsPair : EXPECTED_BENEFICIARY_TO_TRANSACTIONS_MAP.entrySet()) {
      String expectedBeneficiary = beneficiaryToTransactionsPair.getKey();
      List<Transaction> expectedTransactionsList = new java.util.ArrayList<>(
          beneficiaryToTransactionsPair.getValue().stream().toList());
      expectedTransactionsList.sort(Comparator.comparing(Transaction::transactionID));

      Assertions.assertTrue(beneficiaryToTransactionsMap.containsKey(expectedBeneficiary));
      List<Transaction> actualTransactionsList = new java.util.ArrayList<>(
          beneficiaryToTransactionsMap.get(expectedBeneficiary).stream().toList());
      actualTransactionsList.sort(Comparator.comparing(Transaction::transactionID));
      Assertions.assertEquals(actualTransactionsList, expectedTransactionsList);
    }
  }

  @Test
  void getUnsolvedIssueIds() {
    Set<Integer> unresolvedIssueIDs = dataFetcher.getUnsolvedIssueIds();
    Assertions.assertEquals(UNRESOLVED_ISSUE_IDs, unresolvedIssueIDs);
  }

  @Test
  void getAllSolvedIssueMessages() {
    List<String> resolvedTranMessages = dataFetcher.getAllSolvedIssueMessages();
    Assertions.assertEquals(RESOLVED_ISSUE_MSGs, resolvedTranMessages);
  }

  @Test
  void getTop3TransactionsByAmount() {
    List<BigDecimal> max3TransactionAmounts = dataFetcher.getTop3TransactionsByAmount();
    Assertions.assertEquals(EXPECTED_MAX_3_TRAN_AMOUNTS, max3TransactionAmounts);
  }

  @Test
  void getTopSender() throws NotFoundException {
    String topSenderClient = dataFetcher.getTopSender();
    Assertions.assertEquals(EXPECTED_TOP_SENDER_CLIENT, topSenderClient);
  }


  private static final BigDecimal EXPECTED_TOTAL_AMOUNT = BigDecimal.valueOf(2889.17);
  private static final String TEST_CLIENT_1 = "Grace Burgess";
  private static final BigDecimal EXPECTED_TOTAL_SENT_AMOUNT_BY_CLIENT1 = BigDecimal.valueOf(666.0);
  private static final BigDecimal EXPECTED_MAX_AMOUNT = BigDecimal.valueOf(985.0);
  private static final long UNIQUE_CLIENT_COUNT = 14L;
  private static final String TEST_CLIENT_2 = "Michael Gray";
  private static final Set<Integer> UNRESOLVED_ISSUE_IDs = new LinkedHashSet<>(Arrays.asList(1,3,15,54,99));
  private static final List<String> RESOLVED_ISSUE_MSGs = Arrays.asList("Never gonna give you up",
      "Never gonna let you down",
      "Never gonna run around and desert you"
      );
  private static final List<BigDecimal> EXPECTED_MAX_3_TRAN_AMOUNTS =
      Arrays.asList(BigDecimal.valueOf(985.0),BigDecimal.valueOf(666.0),BigDecimal.valueOf(430.2));
  private static final String EXPECTED_TOP_SENDER_CLIENT = "Arthur Shelby";
  static final List<Transaction> DUMMY_TRANSACTION_LIST = Arrays.asList(
      new Transaction(663458L, BigDecimal.valueOf(430.2)
          , "Tom Shelby", 22, "Alfie Solomons",
          33,
          Set.of( new Issue(1, false, "Looks like money laundering"))),
      new Transaction(1284564L, BigDecimal.valueOf(150.2),
          "Tom Shelby", 22, "Arthur Shelby", 60,
          Set.of( new Issue(2, true, "Never gonna give you up"),
              new Issue(3, false,"Looks like money laundering"))),
      new Transaction(96132456L, BigDecimal.valueOf(67.8),
          "Aunt Polly", 34, "Aberama Gold", 58,
          Collections.emptySet()),
      new Transaction(5465465L, BigDecimal.valueOf(985.0),
          "Arthur Shelby", 60, "Ben Younger", 47,
          Set.of( new Issue(15, false, "Something's fishy"))),
      new Transaction(1651665L, BigDecimal.valueOf(97.6),
          "Tom Shelby", 22 , "Oswald Mosley", 37,
          Set.of( new Issue(65, true, "Never gonna let you down")))
  );
  private static final Map<String, Collection<Transaction>> EXPECTED_BENEFICIARY_TO_TRANSACTIONS_MAP =
      new HashMap<>() {{
        put("Alfie Solomons", Arrays.asList( new Transaction(663458L, BigDecimal.valueOf(430.2)
            , "Tom Shelby", 22, "Alfie Solomons",
            33,
            Set.of( new Issue(1, false, "Looks like money laundering")))));
        put("Arthur Shelby", Arrays.asList(new Transaction(1284564L, BigDecimal.valueOf(150.2),
            "Tom Shelby", 22, "Arthur Shelby", 60,
            Set.of( new Issue(2, true, "Never gonna give you up"),
                new Issue(3, false,"Looks like money laundering")))));
        put("Aberama Gold", Arrays.asList(new Transaction(96132456L, BigDecimal.valueOf(67.8),
            "Aunt Polly", 34, "Aberama Gold", 58,
            Collections.emptySet())));
        put("Ben Younger", Arrays.asList(new Transaction(5465465L, BigDecimal.valueOf(985.0),
            "Arthur Shelby", 60, "Ben Younger", 47,
            Set.of( new Issue(15, false, "Something's fishy")))));
        put("Oswald Mosley", Arrays.asList(new Transaction(1651665L, BigDecimal.valueOf(97.6),
            "Tom Shelby", 22 , "Oswald Mosley", 37,
            Set.of( new Issue(65, true, "Never gonna let you down")))));
      }};

}