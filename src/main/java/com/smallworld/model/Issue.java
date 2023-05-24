package com.smallworld.model;


import java.util.Objects;

public record Issue( Integer issueId, Boolean issueSolved, String issueMessage) {

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Issue issue = (Issue) o;
    return issueId.equals(issue.issueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(issueId);
  }
}
