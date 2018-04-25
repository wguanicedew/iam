package it.infn.mw.iam.api.account.search.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.infn.mw.iam.persistence.model.IamGroup;

@JsonInclude(NON_NULL)
public class IamGroupDTO {

  private Long id;
  private String uuid;
  private String name;
  private String description;

  @JsonCreator
  public IamGroupDTO(@JsonProperty("id") Long id, @JsonProperty("uuid") String uuid,
      @JsonProperty("name") String name, @JsonProperty("description") String description) {

    this.id = id;
    this.uuid = uuid;
    this.name = name;
    this.description = description;
  }

  public IamGroupDTO(Builder builder) {

    this.id = builder.id;
    this.uuid = builder.uuid;
    this.name = builder.name;
    this.description = builder.description;
  }

  public Long getId() {

    return id;
  }

  public String getUuid() {

    return uuid;
  }

  public String getName() {

    return name;
  }

  public String getDescription() {

    return description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IamGroupDTO other = (IamGroupDTO) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private Long id;
    private String uuid;
    private String name;
    private String description;

    public Builder fromIamGroup(IamGroup group) {

      this.id = group.getId();
      this.uuid = group.getUuid();
      this.name = group.getName();
      this.description = group.getDescription();
      return this;
    }

    public IamGroupDTO build() {

      return new IamGroupDTO(this);
    }
  }
}
