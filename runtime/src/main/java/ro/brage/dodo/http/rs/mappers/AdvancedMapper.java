/**
 * *****************************************************************************
 * Copyright 2018 Dorin Brage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************
 */
package ro.brage.dodo.http.rs.mappers;

import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ro.brage.dodo.jpa.Model;
import ro.brage.dodo.http.rs.DtoModel;
import ro.brage.dodo.http.rs.mappers.qualifiers.LoadEntity;

/**
 * Advanced mapper
 *
 * @author Dorin Brage
 *
 * @param <Entity>
 * @param <DTO>
 */
@MapperConfig(disableSubMappingMethodsGeneration = true)
public interface AdvancedMapper<Entity extends Model, DTO extends DtoModel>
        extends SimpleMapper<Entity, DTO> {

    @Mapping(target = "guid", ignore = true)
    public void updateEntity(DTO dto, @MappingTarget Entity entity);

    @Mapping(target = "guid", ignore = true)
    public void updateDTO(@MappingTarget DTO dto, Entity entity);

    public List<DTO> findDTOs(List<Entity> entities);

    public List<Entity> findEntities(List<DTO> dtos);

    @LoadEntity
    @IterableMapping(qualifiedBy = {LoadEntity.class})
    public List<DTO> loadDTOs(List<Entity> entities);

    @LoadEntity
    @IterableMapping(qualifiedBy = {LoadEntity.class})
    public List<Entity> loadEntities(List<DTO> dtos);

    @LoadEntity
    public Entity load(DTO dto);

    @LoadEntity
    public DTO load(Entity entity);

}
