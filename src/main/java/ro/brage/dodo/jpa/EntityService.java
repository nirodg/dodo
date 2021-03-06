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
package ro.brage.dodo.jpa;

import java.lang.reflect.ParameterizedType;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.brage.dodo.jpa.enums.JpaErrorKeys;
import ro.brage.dodo.jpa.utils.JpaLog;
import ro.brage.dodo.jpa.utils.QueryParams;

/**
 * The Abstract Service
 * 
 * @author Dorin Brage
 * @param <ENTITY> the ENTITY
 */
@SuppressWarnings("unchecked")
public abstract class EntityService<ENTITY extends Model> {

  private Logger LOG = LoggerFactory.getLogger(EntityService.class);


  protected final static String HINT_FETCH_GRAPH = "javax.persistence.fetchgraph";
  protected final static String HINT_LOAD_GRAPH = "javax.persistence.loadgraph";

  @PersistenceContext
  private EntityManager entityManager;

  @Inject
  private Principal principal;

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
    object.setCreatedBy(principal.getName());
    object.setUpdatedBy(principal.getName());
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
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ENTITY updateByGuid(Object guid, ENTITY entity) {
    ENTITY objectToUpdate = findByGuid(guid);
    if (objectToUpdate != null) {
      objectToUpdate.setUpdatedBy(principal.getName());
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
  public boolean deleteByGuid(Object guid) {
    try {
      ENTITY toDelete = findByGuid(guid);
      entityManager.remove(toDelete);
      return true;
    } catch (Exception e) {
      LOG.error("Couldn't delete the entity {}", e.getMessage());
      return false;
    }
  }

  public ENTITY loadByGuid(String guid) {
    cq.where(cb.equal(root.get(Model.GUID), guid));
    typedQuery = entityManager.createQuery(cq);
    typedQuery.setHint(HINT_LOAD_GRAPH, entityClass.getSimpleName() + ".loadByGuid");
    return typedQuery.getSingleResult();
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
      JpaLog.error(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITY, e, null);
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
      return (List<ENTITY>) JpaLog.error(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES, e,
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
