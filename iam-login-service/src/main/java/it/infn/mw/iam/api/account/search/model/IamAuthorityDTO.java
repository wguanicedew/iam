package it.infn.mw.iam.api.account.search.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.infn.mw.iam.persistence.model.IamAuthority;

@JsonInclude(NON_NULL)
public class IamAuthorityDTO {

  private Long id;
  private String authority;

  @JsonCreator
  public IamAuthorityDTO(@JsonProperty("id") Long id, @JsonProperty("authority") String authority) {

    this.id = id;
    this.authority = authority;
  }

  public IamAuthorityDTO(Builder builder) {

    this.id = builder.id;
    this.authority = builder.authority;
  }

  public Long getId() {
    return id;
  }

  public String getAuthority() {
    return authority;
  }

  @Generated("eclipse")
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((authority == null) ? 0 : authority.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Generated("eclipse")
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IamAuthorityDTO other = (IamAuthorityDTO) obj;
    if (authority == null) {
      if (other.authority != null)
        return false;
    } else if (!authority.equals(other.authority))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private Long id;
    private String authority;

    public Builder fromIamAuthority(IamAuthority a) {

      this.id = a.getId();
      this.authority = a.getAuthority();
      return this;
    }

    public IamAuthorityDTO build() {

      return new IamAuthorityDTO(this);
    }
  }
}
