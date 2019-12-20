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
 * *****************************************************************************
 */
package ro.brage.dodo.jpa;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import ro.brage.dodo.jpa.utils.QueryParams;

/**
 * The Abstract Service
 *
 * @author Dorin Brage
 * @param <ENTITY> the ENTITY
 */
public abstract class EntityService<ENTITY extends Model> {
    
    protected final static String HINT_FETCH_GRAPH = "javax.persistence.fetchgraph";
    protected final static String HINT_LOAD_GRAPH = "javax.persistence.loadgraph";
    
    @PersistenceContext
    private EntityManager entityManager;
    
    protected CriteriaBuilder cb;
    protected CriteriaQuery<ENTITY> cq;
    protected Root<ENTITY> root;
    protected TypedQuery<ENTITY> typedQuery;
    
    protected Class<ENTITY> entityClass;
    
    public abstract String entity();
    
    @PostConstruct
    private void initialize() throws ClassNotFoundException {
        
        entityClass = (Class<ENTITY>) Class.forName(entity());
        cb = entityManager.getCriteriaBuilder();
        cq = cb.createQuery(entityClass);
        root = cq.from(entityClass);
    }

    /**
     * Create a new ENTITY
     *
     * @param object the ENTITY to be persisted
     * @return the object
     */
    @Transactional
    public ENTITY create(ENTITY object) {
        // Quarkus & hibernate-orm does not support @PrePersist
//        object.setGuid(UUID.randomUUID().toString());
        
        entityManager.persist(object);
        return object;
    }

    /**
     * Find an entity by it's GUID
     *
     * @param guid the GUID
     * @return the ENTITY object
     */
    public ENTITY findByGuid(Object guid) {
        return entityManager.find(entityClass, guid);
    }

    /**
     * Update the entity
     *
     * @param guid
     * @param dto
     * @return
     * @throws Exception if the ENTITY is not yet persisted in the DB
     */
    @Transactional
    public ENTITY updateByGuid(String guid, ENTITY entity) throws Exception {
        ENTITY objectToUpdate = findByGuid(guid);
        if (objectToUpdate == null) {
            throw new Exception("Entity cannot be updated because is not yet in the database");
        }
        
        return create(objectToUpdate);
    }

    /**
     * Delete an entity by it's GUID
     *
     * @param guid the GUID
     * @return TRUE if the entity is deleted, otherwise FALSE
     */
    @Transactional
    public boolean deleteByGuid(Object guid) throws Exception {
        try {
            ENTITY toDelete = findByGuid(guid);
            entityManager.remove(toDelete);
            return true;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            return false;
        }
    }

    /**
     * Load an entity and it's collections by the guid
     *
     * @param guid
     * @return the ENTITY if is found, otherwise NULL
     */
    public ENTITY loadByGuid(String guid) throws Exception {
        cq.where(cb.equal(root.get(Model.GUID), guid));
        typedQuery = entityManager.createQuery(cq);
        typedQuery.setHint(HINT_LOAD_GRAPH, entityClass.getSimpleName() + ".loadByGuid");
        try {
            return typedQuery.getSingleResult();
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            return null;
        }
    }

    /**
     * Get all entities using namedQuery
     *
     * @return return a list of entities
     */
    public List<ENTITY> getAll() {
        Query query = entityManager.createNamedQuery(entityClass.getSimpleName() + ".findAll");
        return query.getResultList();
    }

    /**
     * The Entity Manager
     *
     * @return
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * The Criteria Builder
     *
     * @return
     */
    public CriteriaBuilder getCB() {
        return cb;
    }

    /**
     * The Root
     *
     * @return
     */
    public Root<ENTITY> getRoot() {
        return root;
    }

    /**
     * Get a single entity using a namedQuery
     *
     * @param namedQuery the name of the query
     * @param parameters the QueryParams object
     * @return a list of entities
     */
    public ENTITY getSingleResult(String namedQuery, QueryParams parameters) {
        Query query = createQueryParam(namedQuery, parameters);
        try {
            return (ENTITY) query.getSingleResult();
//        } catch (Exception e) {
//            JpaLog.error(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITY, e, null);
//        }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            return null;
        }
    }

    /**
     * Get a list of entities using a namedQuery
     *
     * @param namedQuery the name of the query
     * @param parameters the QueryParams object
     * @return a list of entities
     */
    public List<ENTITY> getResults(String namedQuery, QueryParams parameters) {
        Query query = createQueryParam(namedQuery, parameters);
        
        try {
            return query.getResultList();
//        } catch (Exception e) {
////            return (List<ENTITY>) JpaLog.error(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES, e,
////                    new ArrayList<>());
////        }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            return null;
        }
        
    }

    /**
     * Get total number of items
     *
     * <pre>
     * SELECT COUNT(c) FROM Car c
     * </pre>
     *
     * @return
     */
    public long getCount() {
        
        String entityName = entityClass.getSimpleName().toLowerCase();
        Query query = entityManager.createQuery(
                "SELECT COUNT(" + entityName + ") FROM " + entityClass.getSimpleName() + " " + entityName);
        
        return (long) query.getSingleResult();
    }

    /**
     * Create the QueryParam
     *
     * @param namedQuery the name of the query
     * @param parameters the QueryParams object
     * @return a Query object
     */
    protected Query createQueryParam(String namedQuery, QueryParams parameters) {
        if (namedQuery != null && parameters != null) {
            
            Query query
                    = getEntityManager().createNamedQuery(entityClass.getSimpleName() + "." + namedQuery);
            parameters.getParams().forEach((key, value) -> {
                if (value != null) {
                    query.setParameter(key, value);
                }
            });
            return query;
        }
        
        return null;
    }
    
}
