package ro.brage.dodo.jpa.mapper;

import org.mapstruct.MapperConfig;
import ro.brage.dodo.jpa.DtoModel;
import ro.brage.dodo.jpa.Model;

/**
 * Simple mapper
 * 
 * @author Dorin Brage
 *
 * @param <Entity>
 * @param <DTO>
 */
@MapperConfig(componentModel = "cdi")
public abstract class SimpleMapper<Entity extends Model, DTO extends DtoModel> {

  public abstract Entity map(DTO entity);

  public abstract DTO map(Entity entity);
}
