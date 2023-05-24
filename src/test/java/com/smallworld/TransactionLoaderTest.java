package com.smallworld;


import com.smallworld.model.Transaction;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransactionLoaderTest {

  @Test
  void loadTransactions() throws IOException {
    List<Transaction> transactions = new java.util.ArrayList<>(
        TransactionLoader.loadTransactions(TRANSACTION_JSON).stream().toList());
    TransactionDataFetcherTest.DUMMY_TRANSACTION_LIST
        .sort(Comparator.comparing(Transaction::transactionID));
    transactions.sort(Comparator.comparing(Transaction::transactionID));
    Assertions.assertEquals(TransactionDataFetcherTest.DUMMY_TRANSACTION_LIST, transactions);
  }
  private static final String TRANSACTION_JSON = "[\n"
      + "  {\n"
      + "    \"mtn\": 663458,\n"
      + "    \"amount\": 430.2,\n"
      + "    \"senderFullName\": \"Tom Shelby\",\n"
      + "    \"senderAge\": 22,\n"
      + "    \"beneficiaryFullName\": \"Alfie Solomons\",\n"
      + "    \"beneficiaryAge\": 33,\n"
      + "    \"issueId\": 1,\n"
      + "    \"issueSolved\": false,\n"
      + "    \"issueMessage\": \"Looks like money laundering\"\n"
      + "  },\n"
      + "  {\n"
      + "    \"mtn\": 1284564,\n"
      + "    \"amount\": 150.2,\n"
      + "    \"senderFullName\": \"Tom Shelby\",\n"
      + "    \"senderAge\": 22,\n"
      + "    \"beneficiaryFullName\": \"Arthur Shelby\",\n"
      + "    \"beneficiaryAge\": 60,\n"
      + "    \"issueId\": 2,\n"
      + "    \"issueSolved\": true,\n"
      + "    \"issueMessage\": \"Never gonna give you up\"\n"
      + "  },\n"
      + "  {\n"
      + "    \"mtn\": 1284564,\n"
      + "    \"amount\": 150.2,\n"
      + "    \"senderFullName\": \"Tom Shelby\",\n"
      + "    \"senderAge\": 22,\n"
      + "    \"beneficiaryFullName\": \"Arthur Shelby\",\n"
      + "    \"beneficiaryAge\": 60,\n"
      + "    \"issueId\": 3,\n"
      + "    \"issueSolved\": false,\n"
      + "    \"issueMessage\": \"Looks like money laundering\"\n"
      + "  },\n"
      + "  {\n"
      + "    \"mtn\": 96132456,\n"
      + "    \"amount\": 67.8,\n"
      + "    \"senderFullName\": \"Aunt Polly\",\n"
      + "    \"senderAge\": 34,\n"
      + "    \"beneficiaryFullName\": \"Aberama Gold\",\n"
      + "    \"beneficiaryAge\": 58,\n"
      + "    \"issueId\": null,\n"
      + "    \"issueSolved\": true,\n"
      + "    \"issueMessage\": null\n"
      + "  },\n"
      + "  {\n"
      + "    \"mtn\": 5465465,\n"
      + "    \"amount\": 985.0,\n"
      + "    \"senderFullName\": \"Arthur Shelby\",\n"
      + "    \"senderAge\": 60,\n"
      + "    \"beneficiaryFullName\": \"Ben Younger\",\n"
      + "    \"beneficiaryAge\": 47,\n"
      + "    \"issueId\": 15,\n"
      + "    \"issueSolved\": false,\n"
      + "    \"issueMessage\": \"Something's fishy\"\n"
      + "  },\n"
      + "  {\n"
      + "    \"mtn\": 1651665,\n"
      + "    \"amount\": 97.66,\n"
      + "    \"senderFullName\": \"Tom Shelby\",\n"
      + "    \"senderAge\": 22,\n"
      + "    \"beneficiaryFullName\": \"Oswald Mosley\",\n"
      + "    \"beneficiaryAge\": 37,\n"
      + "    \"issueId\": 65,\n"
      + "    \"issueSolved\": true,\n"
      + "    \"issueMessage\": \"Never gonna let you down\"\n"
      + "  }"
      + "]";
}