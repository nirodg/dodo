/*******************************************************************************
 * Copyright 2018 Dorin Brage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package com.brage.dodo.jpa.mapper;

import java.util.List;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingTarget;

import com.brage.dodo.jpa.AbstractDTOModel;
import com.brage.dodo.jpa.AbstractModel;
import com.brage.dodo.jpa.mapper.qualifiers.LoadEntity;

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