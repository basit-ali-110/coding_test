package com.smallworld;

import com.smallworld.exception.NotFoundException;
import com.smallworld.model.Transaction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TransactionDataFetcher {

    /**
     * Returns the sum of the amounts of all transactions
     */
    public double getTotalTransactionAmount(List<Transaction> transactions) {
        Optional<Double> amount= transactions.stream()
            .distinct()
            .map(Transaction::transactionAmount)
            .reduce(Double::sum);

        return amount.orElse(0.0);
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public double getTotalTransactionAmountSentBy(String senderFullName, List<Transaction> transactions)
        throws NotFoundException {
        Optional<Double> amount= transactions.stream().distinct().filter(transaction ->
            transaction.senderFullName().equals(senderFullName))
            .map(Transaction::transactionAmount)
            .reduce(Double::sum);

        return amount.orElseThrow(() -> new NotFoundException(String.format("Sender with name: %s not found.", senderFullName)));
    }

    /**
     * Returns the highest transaction amount
     */
    public double getMaxTransactionAmount(List<Transaction> transactions) {
        Optional<Double> amount= transactions.stream().distinct()
            .map(Transaction::transactionAmount)
            .reduce(Double::max);

        return amount.orElse(0.0);
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients(List<Transaction> transactions) {
        return transactions.stream()
            .mapMulti((transaction, consumer) -> {
                consumer.accept(transaction.senderFullName());
                consumer.accept(transaction.beneficiaryFullName());
            }).distinct().count();
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    public boolean hasOpenComplianceIssues(String clientFullName, List<Transaction> transactions)
        throws NotFoundException {
        AtomicReference<Boolean> clientFound = new AtomicReference<>(Boolean.FALSE);
        boolean hasAnyUnResolvedIssue = transactions.stream()
            .filter(transaction -> {
                if (transaction.senderFullName().equals(clientFullName)
                    || transaction.beneficiaryFullName().equals(clientFullName)) {
                    clientFound.set(true);
                    return true;
                }
                return false;
            }).map(Transaction::issueSolved).anyMatch(issueResolved -> !issueResolved);

        if(Boolean.FALSE.equals(clientFound.get())) {
            throw new NotFoundException(String.format("Client: %s not found in transactions", clientFullName));
        }
        return hasAnyUnResolvedIssue;
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, List<Transaction>> getTransactionsByBeneficiaryName(List<Transaction> transactions) {
        Map<String, List<Transaction>> beneficiaryToTransactionsListMap = new HashMap<>();
        transactions.stream().distinct()
            .forEach(transaction -> {
                List<Transaction> transactionList = beneficiaryToTransactionsListMap.computeIfAbsent(transaction.beneficiaryFullName(),
                    s -> new ArrayList<>());
                transactionList.add(transaction);
            });
        return beneficiaryToTransactionsListMap;
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    public Set<Integer> getUnsolvedIssueIds(List<Transaction> transactions) {
        return transactions.stream().filter(transaction -> !transaction.issueSolved())
            .map(Transaction::issueId)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Returns a list of all solved issue messages
     */
    public List<String> getAllSolvedIssueMessages(List<Transaction> transactions) {
        return transactions.stream().filter(transaction -> transaction.issueSolved() && transaction.issueId() != null)
            .map(Transaction::issueMessage)
            .toList();
    }

    /**
     * Returns the 3 transactions with highest amount sorted by amount descending
     */
    public List<Double> getTop3TransactionsByAmount(List<Transaction> transactions) {
        return transactions.stream().distinct()
            .map(Transaction::transactionAmount)
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .toList();
    }

    /**
     * Returns the sender with the most total sent amount
     */
    public Optional<String> getTopSender(List<Transaction> transactions) {
        Map<String, Double> senderToAmountSentMap = new HashMap<>();
        transactions.stream().distinct()
            .forEach(transaction -> {
                Double tran2Amount = senderToAmountSentMap.getOrDefault(
                    transaction.senderFullName(), 0.0);
                senderToAmountSentMap.put(transaction.senderFullName(),
                    tran2Amount + transaction.transactionAmount());
            });
        return senderToAmountSentMap.entrySet().stream()
            .max(Entry.comparingByValue())
            .map(Entry::getKey);
    }

}
