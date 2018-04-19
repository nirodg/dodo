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
package com.brage.dodo.rs;

import java.util.List;

import com.brage.dodo.jpa.AbstractDTOModel;
import com.brage.dodo.jpa.AbstractModel;
import com.brage.dodo.jpa.AbstractService;
import com.brage.dodo.jpa.mapper.AbstractModelMapper;

/**
 *
 * @author Dorin Brage
 */
public abstract class AbstractRestServiceBean<ENTITY extends AbstractModel, DTO extends AbstractDTOModel> implements AbstractRestService<DTO> {

    @Override
    public List<DTO> getAll() {
        List<ENTITY> data = getService().getAll();
        return getMapper().findDTOs(data);
    }

    @Override
    public DTO create(DTO entity) {
        ENTITY data = mapEntity(entity);
        return mapDTO(getService().create(data));
    }

    @Override
    public DTO updateByGuid(String guid, DTO entity) {
        ENTITY data = getService().updateByGuid(guid, entity);
        return mapDTO(data);
    }

    @Override
    public DTO getByGuid(String guid) {
        ENTITY data = getService().findByGuid(guid);
        return mapDTO(data);
    }

    @Override
    public boolean deleteByGuid(String guid) {
        return getService().deleteByGuid(guid);
    }

    public ENTITY mapEntity(DTO dto) {
        return getMapper().find(dto);
    }

    public DTO mapDTO(ENTITY entity) {
        return getMapper().find(entity);
    }

    public abstract AbstractService<ENTITY, DTO> getService();

    public abstract AbstractModelMapper<ENTITY, DTO> getMapper();

}
