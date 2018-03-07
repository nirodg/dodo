/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbrage.core.dblib.jpa.mapper;

import com.dbrage.core.dblib.jpa.AbstractDTOModel;
import com.dbrage.core.dblib.jpa.AbstractModel;
import com.dbrage.core.dblib.jpa.mapper.qualifiers.LoadEntity;
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

    public abstract Entity find(DTO entity);

    public abstract DTO find(Entity entity);

    public abstract void updateEntity(DTO dto, @MappingTarget Entity entity);

    public abstract void updateDTO(@MappingTarget DTO dto, Entity entity);

    public abstract List<DTO> findDTOs(List<Entity> entities);

    public abstract List<Entity> findEntities(List<DTO> dtos);

    @LoadEntity
    public abstract Entity load(DTO dto);

    @LoadEntity
    public abstract DTO load(Entity entity);

}
