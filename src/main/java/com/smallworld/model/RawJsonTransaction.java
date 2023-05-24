package com.smallworld.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawJsonTransaction(@JsonProperty("mtn") Long transactionID,
                                 @JsonProperty("amount") BigDecimal transactionAmount,
                                 @JsonProperty("senderFullName") String senderFullName,
                                 @JsonProperty("senderAge") Integer senderAge,
                                 @JsonProperty("beneficiaryFullName") String beneficiaryFullName,
                                 @JsonProperty("beneficiaryAge") Integer beneficiaryAge,
                                 @JsonProperty("issueId") Integer issueId,
                                 @JsonProperty("issueSolved") Boolean issueSolved,
                                 @JsonProperty("issueMessage") String issueMessage
                          ) {
}
