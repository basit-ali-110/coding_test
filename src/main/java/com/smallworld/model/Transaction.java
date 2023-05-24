package com.smallworld.model;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

public record Transaction(Long transactionID, BigDecimal transactionAmount, String senderFullName,
                          Integer senderAge, String beneficiaryFullName, Integer beneficiaryAge,
                          Set<Issue> issues) {
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Transaction that = (Transaction) o;
    return transactionID.equals(that.transactionID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionID);
  }
}
