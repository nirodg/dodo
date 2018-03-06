/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbrage.core.dblib.rs;

import com.dbrage.core.dblib.jpa.AbstractDTOModel;
import com.dbrage.core.dblib.jpa.AbstractModel;
import com.dbrage.core.dblib.jpa.AbstractService;
import com.dbrage.core.dblib.jpa.mapper.AbstractModelMapper;
import java.util.List;

/**
 *
 * @author Dorin Brage
 */
public abstract class AbstractRestServiceBean<ENTITY extends AbstractModel, DTO extends AbstractDTOModel> implements AbstractRestService<DTO> {

    @Override
    public List<DTO> getAll() {
        List<ENTITY> data = getService().getAll();
        return getMapper().mapDTOs(data);
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

    private ENTITY mapEntity(DTO dto) {
        return getMapper().mapEntity(dto);
    }

    private DTO mapDTO(ENTITY entity) {
        return getMapper().mapDTO(entity);
    }

    public abstract AbstractService<ENTITY, DTO> getService();

    public abstract AbstractModelMapper<ENTITY, DTO> getMapper();

}
