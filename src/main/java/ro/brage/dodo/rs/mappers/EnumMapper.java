package ro.brage.dodo.rs.mappers;

/**
 * Enumeration mapper
 * 
 * @author Dorin Brage
 *
 * @param <Entity>
 * @param <Dto>
 */
public abstract class EnumMapper<Entity extends Enum<?>, Dto extends Enum<?>> {

  public abstract Entity fromDto(Dto entity);

  public abstract Dto fromEntity(Entity entity);
}
