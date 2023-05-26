package com.smallworld;

import com.smallworld.exception.NotFoundException;
import com.smallworld.model.Issue;
import com.smallworld.model.Transaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionDataFetcher {

    private final Collection<Transaction> transactions;

    public TransactionDataFetcher(Collection<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Returns the sum of the amounts of all transactions
     */
    public BigDecimal getTotalTransactionAmount() {
        return transactions.stream()
            .map(Transaction::transactionAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public BigDecimal getTotalTransactionAmountSentBy(String senderFullName) throws NotFoundException {
        List<Transaction> clientTransactions = getClientTransactions(senderFullName, Transaction::senderFullName);

        if(clientTransactions.isEmpty()) {
            throw  new NotFoundException(String.format("Sender with name: %s not found.", senderFullName));
        }

        return clientTransactions.stream()
            .map(Transaction::transactionAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the highest transaction amount
     */
    public BigDecimal getMaxTransactionAmount() {
        return transactions.stream()
            .map(Transaction::transactionAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::max);
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients() {
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
    public boolean hasOpenComplianceIssues(String clientFullName)
        throws NotFoundException {
        List<Transaction> clientTransactions = getClientTransactions(clientFullName, Transaction::senderFullName);
        clientTransactions.addAll(getClientTransactions(clientFullName, Transaction::beneficiaryFullName));

        if(clientTransactions.isEmpty()) {
            throw new NotFoundException(String.format("Client with name: %s not found.", clientFullName));
        }

        return clientTransactions.stream()
            .<Boolean>mapMulti((transaction, consumer) -> transaction.issues()
                .forEach(issue -> consumer.accept(issue.issueSolved())))
            .anyMatch(issueResolved -> !issueResolved);
    }

    private <T> List<Transaction>  getClientTransactions(String clientFullName,
        Function<Transaction, T> filterByFieldGetter) {
        return  transactions.stream()
            .filter(transaction -> filterByFieldGetter.apply(transaction).equals(clientFullName))
            .collect(Collectors.toList());
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, Collection<Transaction>> getTransactionsByBeneficiaryName() {
        Map<String, Collection<Transaction>> beneficiaryToTransactionsListMap = new HashMap<>();
        transactions.forEach(transaction -> {
                Collection<Transaction> transactionList =
                    beneficiaryToTransactionsListMap.computeIfAbsent(
                        transaction.beneficiaryFullName(),
                    s -> new ArrayList<>());
                transactionList.add(transaction);
            });
        return beneficiaryToTransactionsListMap;
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    public Set<Integer> getUnsolvedIssueIds() {
        return transactions.stream().<Issue>mapMulti((transaction, consumer) ->
                transaction.issues().forEach(
                consumer))
            .filter(issue -> !issue.issueSolved())
            .map(Issue::issueId)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Returns a list of all solved issue messages
     */
    public List<String> getAllSolvedIssueMessages() {
        return transactions.stream().<Issue>mapMulti((transaction, consumer) ->
                transaction.issues().forEach(
                    consumer))
            .filter(Issue::issueSolved)
            .map(Issue::issueMessage)
            .toList();
    }

    /**
     * Returns the 3 transactions with highest amount sorted by amount descending
     */
    public List<BigDecimal> getTop3TransactionsByAmount() {
        return transactions.stream()
            .map(Transaction::transactionAmount)
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .toList();
    }

    /**
     * Returns the sender with the most total sent amount
     */
    public String getTopSender() {
        Map<String, BigDecimal> senderToAmountSentMap = new HashMap<>();
        transactions
            .forEach(transaction -> {
                BigDecimal tran2Amount = senderToAmountSentMap.getOrDefault(
                    transaction.senderFullName(), BigDecimal.ZERO);
                senderToAmountSentMap.put(transaction.senderFullName(),
                    tran2Amount.add(transaction.transactionAmount()));
            });
        return senderToAmountSentMap.entrySet().stream()
            .max(Entry.comparingByValue())
            .map(Entry::getKey).get();
    }

}
