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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TransactionDataFetcher {

    /**
     * Returns the sum of the amounts of all transactions
     */
    public BigDecimal getTotalTransactionAmount(Collection<Transaction> transactions) {
        return transactions.stream()
            .map(Transaction::transactionAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public BigDecimal getTotalTransactionAmountSentBy(String senderFullName,
        Collection<Transaction> transactions) throws NotFoundException {
        AtomicBoolean clientFound = new AtomicBoolean(false);
        BigDecimal amount= transactions.stream().filter(transaction ->
            transaction.senderFullName().equals(senderFullName))
            .map(transaction -> {
                clientFound.set(true);
                return transaction.transactionAmount();
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(clientFound.get()) {
            return amount;
        }
        throw  new NotFoundException(String.format("Sender with name: %s not found.", senderFullName));
    }

    /**
     * Returns the highest transaction amount
     */
    public BigDecimal getMaxTransactionAmount(Collection<Transaction> transactions) {
        return transactions.stream()
            .map(Transaction::transactionAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::max);
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients(Collection<Transaction> transactions) {
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
    public boolean hasOpenComplianceIssues(String clientFullName, Collection<Transaction> transactions)
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
            }).<Boolean>mapMulti((transaction, consumer) -> transaction.issues()
                .forEach(issue -> consumer.accept(issue.issueSolved())))
            .anyMatch(issueResolved -> !issueResolved);

        if(Boolean.FALSE.equals(clientFound.get())) {
            throw new NotFoundException(String.format("Client: %s not found in transactions", clientFullName));
        }
        return hasAnyUnResolvedIssue;
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, Collection<Transaction>> getTransactionsByBeneficiaryName(Collection<Transaction> transactions) {
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
    public Set<Integer> getUnsolvedIssueIds(Collection<Transaction> transactions) {
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
    public List<String> getAllSolvedIssueMessages(Collection<Transaction> transactions) {
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
    public List<BigDecimal> getTop3TransactionsByAmount(Collection<Transaction> transactions) {
        return transactions.stream()
            .map(Transaction::transactionAmount)
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .toList();
    }

    /**
     * Returns the sender with the most total sent amount
     */
    public Optional<String> getTopSender(Collection<Transaction> transactions) {
        Map<String, BigDecimal> senderToAmountSentMap = new HashMap<>();
        transactions.stream()
            .forEach(transaction -> {
                BigDecimal tran2Amount = senderToAmountSentMap.getOrDefault(
                    transaction.senderFullName(), BigDecimal.ZERO);
                senderToAmountSentMap.put(transaction.senderFullName(),
                    tran2Amount.add(transaction.transactionAmount()));
            });
        return senderToAmountSentMap.entrySet().stream()
            .max(Entry.comparingByValue())
            .map(Entry::getKey);
    }

}
