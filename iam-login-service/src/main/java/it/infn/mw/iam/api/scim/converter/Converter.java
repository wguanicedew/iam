package it.infn.mw.iam.api.scim.converter;

/**
 * 
 * Converts IAM entities to SCIM objects. Design shamelessly inspired by
 * https://github.com/osiam/osiam.
 *
 * @param <S> the Scim type @param <E> The Entity type
 */
public interface Converter<S, E> {

  /**
   * Converts a SCIM object in an entity object @param scim the SCIM object to
   * be converted @return an Entity object
   */
  E fromScim(S scim);

  /**
   * Converts an IAM entity to SCIM.
   * 
   * @param entity an entity @return a SCIM representation of the entity
   */
  S toScim(E entity);

}
