/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbrage.core.dblib.jpa;

import com.dbrage.core.dblib.jpa.mapper.AbstractModelMapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Dorin Brage
 * @param <ENTITY> the ENTITY
 */
public abstract class AbstractService<ENTITY extends AbstractModel, DTO extends AbstractDTOModel> {

    private static final Logger LOG = Logger.getLogger(AbstractService.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    Class<ENTITY> entityClass;
    Class<DTO> dtoClass;

    public ENTITY create(ENTITY object) {
        entityManager.persist(object);
        return object;
    }

    public ENTITY findByGuid(String guid) {
        return entityManager.find(entityClass, guid);
    }

    public ENTITY updateByGuid(String guid, DTO dto) {
        ENTITY objectToUpdate = findByGuid(guid);
        if (objectToUpdate != null) {
            getMapper().updateEntity(dto, objectToUpdate);
            return create(objectToUpdate);
        }
        return null;
    }

    public boolean deleteByGuid(String guid) {
        try {
            ENTITY toDelete = findByGuid(guid);
            entityManager.remove(toDelete);
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Couldn't delete the entity {}", e.getMessage());
            return false;
        }
    }

    public List<ENTITY> getAll(){
        return null;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public abstract AbstractModelMapper getMapper();
}
