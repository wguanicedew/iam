package it.infn.mw.iam.api.account.search;

public class AccountSearchPagedRequest {

  private int count;
  private int startIndex;
  private String filter;

  public AccountSearchPagedRequest(int count, int startIndex, String filter) {

    this.count = count;
    this.startIndex = startIndex;
    this.filter = filter;
  }

  public int getCount() {
    return count;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public String getFilter() {
    return filter;
  }

  class Builder {

    private int count;
    private int startIndex;
    private String filter;

    Builder count(int count) {
      this.count = count;
      return this;
    }

    Builder startIndex(int startIndex) {
      this.startIndex = startIndex;
      return this;
    }

    Builder filter(String filter) {
      this.filter = filter;
      return this;
    }

    public AccountSearchPagedRequest build() {
      return new AccountSearchPagedRequest(count, startIndex, filter);
    }
  }
}
