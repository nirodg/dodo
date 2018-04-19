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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import com.brage.dodo.jpa.enums.JpaErrorKeys;
import com.brage.dodo.jpa.mapper.AbstractModelMapper;
import com.brage.dodo.jpa.utils.JpaLog;
import com.brage.dodo.jpa.utils.QueryParams;

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
  protected void initialize() {

    entityClass = (Class<ENTITY>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0];
    dtoClass = (Class<DTO>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[1];

    cb = entityManager.getCriteriaBuilder();
    cq = cb.createQuery(entityClass);
    root = cq.from(entityClass);
  }

  public abstract AbstractModelMapper getMapper();

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
  public ENTITY updateByGuid(String guid, DTO dto) {
    ENTITY objectToUpdate = findByGuid(guid);
    if (objectToUpdate != null) {
      getMapper().updateEntity(dto, objectToUpdate);
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
      LOG.log(Level.SEVERE, "Couldn't delete the entity {}", e.getMessage());
      return false;
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

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void addEqualsPredicate(SingularAttribute attribute, Object value) {
    addEqualsPredicate(root, attribute, value);
  }

  /* FIXME Bad query */
  @Deprecated
  public void addEqualsPredicate(Root<?> root, SingularAttribute attribute, Object value) {
    if (value != null) {
      getPredicates().add(cb.equal(root.get(attribute), value));
    }
  }

  /* FIXME Bad query */
  @Deprecated
  public void addEqualsPredicate(Join<?, ?> join, SingularAttribute<?, ?> attribute, Object value) {
    if (value != null) {
      getPredicates().add(cb.equal(join.get(attribute.getName()), value));
    }
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

  /**
   * Get a single entity using Predicates
   *
   * @param load if TRUE it fetches all complex objects, otherwise does LAZY fetch
   * @return a single entity
   */
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
   * Get a list of entities using Predicates
   *
   * @param load if TRUE it fetches all complex objects, otherwise does LAZY fetch
   * @return a list of entities
   */
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
      return (List<ENTITY>) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES,
          new ArrayList<>());
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
    } catch (Exception e) {
      return (List<ENTITY>) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES,
          new ArrayList<>());
    }

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
}
