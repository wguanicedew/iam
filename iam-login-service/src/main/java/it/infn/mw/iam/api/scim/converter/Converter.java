package it.infn.mw.iam.api.scim.converter;

/**
 * 
 * Converts IAM entities to DTO objects. Design shamelessly inspired by
 * https://github.com/osiam/osiam.
 *
 * @param <D> the DTO type @param <E> The Entity type
 */
public interface Converter<D, E> {

  /**
   * Converts a DTO object in an entity object
   * 
   * @param dto the DTO object to be converted
   * 
   * @return an Entity object
   */
  E entityFromDto(D dto);

  /**
   * Converts an IAM entity to the related DTO.
   * 
   * @param entity an entity 
   * 
   * @return a DTO representation of the entity
   */
  D dtoFromEntity(E entity);

}
