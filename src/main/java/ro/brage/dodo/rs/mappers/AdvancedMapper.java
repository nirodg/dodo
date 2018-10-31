/*******************************************************************************
 * Copyright 2018 Dorin Brage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package ro.brage.dodo.rs.mappers;

import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingTarget;
import ro.brage.dodo.jpa.Model;
import ro.brage.dodo.rs.DtoModel;
import ro.brage.dodo.rs.mappers.qualifiers.LoadEntity;

/**
 * Advanced mapper
 * 
 * @author Dorin Brage
 * 
 * @param <Entity>
 * @param <DTO>
 */
@MapperConfig(componentModel = "cdi")
public abstract class AdvancedMapper<Entity extends Model, DTO extends DtoModel>
    extends SimpleMapper<Entity, DTO> {


  public abstract void updateEntity(DTO dto, @MappingTarget Entity entity);

  public abstract void updateDTO(@MappingTarget DTO dto, Entity entity);

  public abstract List<DTO> findDTOs(List<Entity> entities);

  public abstract List<Entity> findEntities(List<DTO> dtos);

  @LoadEntity
  @IterableMapping(qualifiedBy = {LoadEntity.class})
  public abstract List<DTO> loadDTOs(List<Entity> entities);

  @LoadEntity
  @IterableMapping(qualifiedBy = {LoadEntity.class})
  public abstract List<Entity> loadEntities(List<DTO> dtos);

  @LoadEntity
  public abstract Entity load(DTO dto);

  @LoadEntity
  public abstract DTO load(Entity entity);

}
