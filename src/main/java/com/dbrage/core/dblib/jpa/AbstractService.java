/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbrage.core.dblib.jpa;

import com.dbrage.core.dblib.jpa.enums.JpaErrorKeys;
import com.dbrage.core.dblib.jpa.mapper.AbstractModelMapper;
import com.dbrage.core.dblib.jpa.utils.JpaLog;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

/**
 *
 * @author Dorin Brage
 * @param <ENTITY> the ENTITY
 */
@SuppressWarnings("unchecked")
public abstract class AbstractService<ENTITY extends AbstractModel, DTO extends AbstractDTOModel> {

    private static final Logger LOG = Logger.getLogger(AbstractService.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    protected CriteriaBuilder cb;
    protected CriteriaQuery<ENTITY> cq;
    protected Root<ENTITY> root;
    protected TypedQuery<ENTITY> typedQuery;

    Class<ENTITY> entityClass;
    Class<DTO> dtoClass;

    protected List<Predicate> predicates = new ArrayList<>();

    @PostConstruct
    private void initialize() {

        entityClass = (Class<ENTITY>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        dtoClass = (Class<DTO>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];

        cb = entityManager.getCriteriaBuilder();
        cq = cb.createQuery(entityClass);
        root = cq.from(entityClass);
    }

    public abstract AbstractModelMapper getMapper();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ENTITY create(ENTITY object) {
        entityManager.persist(object);
        return object;
    }

    public ENTITY findByGuid(String guid) {
        return entityManager.find(entityClass, guid);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ENTITY updateByGuid(String guid, DTO dto) {
        ENTITY objectToUpdate = findByGuid(guid);
        if (objectToUpdate != null) {
            getMapper().updateEntity(dto, objectToUpdate);
            return create(objectToUpdate);
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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

    public List<ENTITY> getAll() {
        Query query = entityManager.createNamedQuery(entityClass.getSimpleName() + ".findAll");
        return query.getResultList();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void addEqualsPredicate(SingularAttribute sa, Object o) {
        addEqualsPredicate(root, sa, o);
    }

    public void addEqualsPredicate(Root root, SingularAttribute sa, Object o) {
        getPredicates().add(cb.equal(root.get(sa), o));
    }

    public void initializePredicates() {
        predicates = new ArrayList<>();
    }

    public List<Predicate> getPredicates() {
        return predicates;
    }

    public CriteriaBuilder getCB() {
        return cb;
    }

    public Root<ENTITY> getRoot() {
        return root;
    }

    public ENTITY getSingleResult(boolean load) {
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        if (load) {
            // fetch
        }
        typedQuery = entityManager.createQuery(cq);

        try {
            return typedQuery.getSingleResult();
        } catch (Exception e) {
            return (ENTITY) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITY, null);
        }
    }

    public List<ENTITY> getResults(boolean load) {
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        if (load) {
            // fetch
        }
        typedQuery = entityManager.createQuery(cq);

        try {
            return typedQuery.getResultList();
        } catch (Exception e) {
            return (List<ENTITY>) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES, new ArrayList<>());
        }
    }
}
