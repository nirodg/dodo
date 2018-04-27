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
package com.brage.dodo.jpa;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brage.dodo.jpa.enums.JpaErrorKeys;
import com.brage.dodo.jpa.utils.JpaLog;
import com.brage.dodo.jpa.utils.QueryParams;

/**
 *
 * @author Dorin Brage
 * @param <ENTITY> the ENTITY
 */
@SuppressWarnings("unchecked")
public abstract class AbstractService<ENTITY extends Model> {

  private Logger LOG = LoggerFactory.getLogger(AbstractService.class);

  @PersistenceContext
  private EntityManager entityManager;

  protected CriteriaBuilder cb;
  protected CriteriaQuery<ENTITY> cq;
  protected Root<ENTITY> root;
  protected TypedQuery<ENTITY> typedQuery;

  Class<ENTITY> entityClass;

  @PostConstruct
  protected void initialize() {

    entityClass = (Class<ENTITY>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0];

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
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ENTITY create(ENTITY object) {
    entityManager.persist(object);
    return object;
  }

  /**
   * Find an entity by it's GUID
   *
   * @param guid the GUID
   * @return the ENTITY object
   */
  public ENTITY findByGuid(String guid) {
    return entityManager.find(entityClass, guid);
  }

  /**
   * Update the entity
   *
   * @param guid
   * @param dto
   * @return
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ENTITY updateByGuid(String guid, ENTITY entity) {
    ENTITY objectToUpdate = findByGuid(guid);
    if (objectToUpdate != null) {
      return create(objectToUpdate);
    }
    return null;
  }

  /**
   * Delete an entity by it's GUID
   *
   * @param guid the GUID
   * @return TRUE if the entity is deleted, otherwise FALSE
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public boolean deleteByGuid(String guid) {
    try {
      ENTITY toDelete = findByGuid(guid);
      entityManager.remove(toDelete);
      return true;
    } catch (Exception e) {
      LOG.error("Couldn't delete the entity {}", e.getMessage());
      return false;
    }
  }

  /**
   * Get all entities using namedQuery
   *
   * @return return a list of entities
   */
  public Set<ENTITY> getAll() {
    Query query = entityManager.createNamedQuery(entityClass.getSimpleName() + ".findAll");
    return new HashSet<>(query.getResultList());
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public CriteriaBuilder getCB() {
    return cb;
  }

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
    } catch (Exception e) {
      JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITY, null);
    }
    return null;
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
    } catch (Exception e) {
      return (List<ENTITY>) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES,
          new ArrayList<>());
    }

  }

  /**
   * Get total nr. of items
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

      Query query =
          getEntityManager().createNamedQuery(entityClass.getSimpleName() + "." + namedQuery);
      parameters.getParams().forEach((key, value) -> {
        if (value != null) {
          query.setParameter(key, value);
        }
      });
      return query;
    }

    return null;
  }

  public Class<ENTITY> getEntityClass() {
    return entityClass;
  }
}
