package com.brage.dodo.jpa;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import com.brage.dodo.jpa.enums.JpaErrorKeys;
import com.brage.dodo.jpa.enums.OrderBy;
import com.brage.dodo.jpa.utils.JpaLog;

public class Finder<ENTITY extends Model> {

  private static final Logger LOG = Logger.getAnonymousLogger();

  @PersistenceContext
  private EntityManager entityManager;

  protected CriteriaBuilder cb;
  protected CriteriaQuery<ENTITY> cq;

  protected Root<ENTITY> root;

  protected TypedQuery<ENTITY> typedQuery;

  Class<ENTITY> entityClass;

  protected List<Predicate> predicates = new ArrayList<>();


  @SuppressWarnings({"unchecked"})
  public Finder() {
    entityClass = (Class<ENTITY>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0];

    cb = entityManager.getCriteriaBuilder();
    cq = cb.createQuery(entityClass);
    root = cq.from(entityClass);
  }

  /**
   * Get a single Finder using Predicates
   *
   * @return a single Finder
   */
  @SuppressWarnings("unchecked")
  public ENTITY findItem() {
    if (!predicates.isEmpty()) {
      cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    }
    typedQuery = entityManager.createQuery(cq);

    try {
      return typedQuery.getSingleResult();
    } catch (Exception e) {
      return (ENTITY) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITY, null);
    }
  }

  /**
   * Get a list of entities using Predicates
   *
   * @return a set of entities
   */
  @SuppressWarnings({"unchecked"})
  public Set<ENTITY> findItems() {

    if (!predicates.isEmpty()) {
      cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    }

    typedQuery = entityManager.createQuery(cq);

    try {
      return (Set<ENTITY>) typedQuery.getResultList();
    } catch (Exception e) {
      return (Set<ENTITY>) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES, new HashSet<>());
    }

  }

  public Finder<ENTITY> equalTo(SingularAttribute<?, ?> attribute, Object value) {
    // TODO Auto-generated method stub
    return null;
  }


  public Finder<ENTITY> equalTo(SingularAttribute<?, ?> entity, SingularAttribute<?, ?> attribute,
      Object value) {
    // TODO Auto-generated method stub
    return null;
  }

  public Finder<ENTITY> notEqualTo(SingularAttribute<?, ?> attribute, Object value) {
    // TODO Auto-generated method stub
    return null;
  }

  public Finder<ENTITY> between(SingularAttribute<?, ?> attribute, Object from, Object to) {
    // TODO Auto-generated method stub
    return null;
  }

  public Finder<ENTITY> greaterThan(SingularAttribute<?, ?> attribute, Object value) {
    // TODO Auto-generated method stub
    return null;
  }

  public Finder<ENTITY> lessThan(SingularAttribute<?, ?> attribute, Object value) {
    // TODO Auto-generated method stub
    return null;
  }

  public Finder<ENTITY> orderBy(SingularAttribute<?, ?> attribute, OrderBy orderBy) {
    // TODO Auto-generated method stub
    return null;
  }

  public Finder<ENTITY> maxItems(Integer maxItems) {
    // TODO Auto-generated method stub
    return null;
  }

}
