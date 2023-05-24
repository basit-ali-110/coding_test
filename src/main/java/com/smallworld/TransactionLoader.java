package com.smallworld;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallworld.model.Issue;
import com.smallworld.model.RawJsonTransaction;
import com.smallworld.model.Transaction;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransactionLoader {
  public static Collection<Transaction> loadTransactions(String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    RawJsonTransaction[] transactions = mapper.readValue(json, RawJsonTransaction[].class);
    Map<Long, Transaction> transactionMap = new HashMap<>();
    for(RawJsonTransaction rawTransaction : transactions) {
      Transaction transaction = transactionMap.computeIfAbsent(rawTransaction.transactionID(),
          s -> new Transaction(rawTransaction.transactionID(),
              rawTransaction.transactionAmount(), rawTransaction.senderFullName(),
              rawTransaction.senderAge(), rawTransaction.beneficiaryFullName(),
              rawTransaction.beneficiaryAge(), new HashSet<>()));
      Set<Issue> issueList = transaction.issues();
      if(rawTransaction.issueId() != null) {
        issueList.add(new Issue(rawTransaction.issueId(), rawTransaction.issueSolved(),
            rawTransaction.issueMessage()));
      }
    }
    return Collections.unmodifiableCollection(transactionMap.values());
  }
}
