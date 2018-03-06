/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbrage.core.dblib.jpa.mapper;

import com.dbrage.core.dblib.jpa.AbstractDTOModel;
import com.dbrage.core.dblib.jpa.AbstractModel;
import java.util.List;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingTarget;

/**
 *
 * @author Dorin Brage
 * @param <Entity>
 * @param <DTO>
 */
@MapperConfig(componentModel = "cdi")
public abstract class AbstractModelMapper<Entity extends AbstractModel, DTO extends AbstractDTOModel> {

    public abstract Entity mapEntity(DTO entity);

    public abstract DTO mapDTO(Entity entity);

    public abstract void updateEntity(DTO dto, @MappingTarget Entity entity);

    public abstract void updateDTO(@MappingTarget DTO dto, Entity entity);

    public abstract List<DTO> mapDTOs(List<Entity> entities);

    public abstract List<Entity> mapEntities(List<DTO> dtos);

}
