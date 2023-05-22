package com.smallworld.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record Transaction(@JsonProperty("mtn") Long transactionID,
                          @JsonProperty("amount") Double transactionAmount,
                          @JsonProperty("senderFullName") String senderFullName,
                          @JsonProperty("senderAge") Integer senderAge,
                          @JsonProperty("beneficiaryFullName") String beneficiaryFullName,
                          @JsonProperty("beneficiaryAge") Integer beneficiaryAge,
                          @JsonProperty("issueId") Integer issueId,
                          @JsonProperty("issueSolved") Boolean issueSolved,
                          @JsonProperty("issueMessage") String issueMessage
                          ) {

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Transaction that = (Transaction) o;
    return transactionID.equals(that.transactionID) && transactionAmount.equals(
        that.transactionAmount) && senderFullName.equals(that.senderFullName) && senderAge.equals(
        that.senderAge) && beneficiaryFullName.equals(that.beneficiaryFullName)
        && beneficiaryAge.equals(that.beneficiaryAge);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionID, transactionAmount, senderFullName, senderAge,
        beneficiaryFullName, beneficiaryAge);
  }
}
