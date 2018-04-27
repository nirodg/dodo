package com.brage.dodo.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brage.dodo.jpa.enums.JpaErrorKeys;
import com.brage.dodo.jpa.enums.OrderBy;
import com.brage.dodo.jpa.utils.JpaLog;

/**
 * The Finder provides additional methods when querying an entity based on {@link CriteriaBuilder}
 * 
 * <pre>
 * &#64;Stateless
 * &#64;TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
 * public class CarService extends AbstractService<Car> {
 * 
 *   public Car getByLicensePlate(String licensePlate) {
 *     return new Finder<>(this).equalTo(Car_.make, make).equalTo(Car_.model, model).findItems();
 *   }
 * 
 * }
 * </pre>
 * 
 * @author Dorin Brage
 *
 * @param <ENTITY> The entity extending the {@link Model} class.
 */
public class Finder<ENTITY extends Model> {

  private final Logger LOG = LoggerFactory.getLogger(Finder.class);

  /** Used for accessing the injected Persistent Manager **/
  AbstractService<ENTITY> service;

  EntityManager entityManager;

  protected CriteriaBuilder cb;
  protected CriteriaQuery<ENTITY> cq;
  protected Root<ENTITY> root;
  protected TypedQuery<ENTITY> typedQuery;

  /** Predicates **/
  protected List<Predicate> predicates = new ArrayList<>();

  Integer maxResults;

  public Finder(AbstractService<ENTITY> service) throws Exception {

    if (service == null) {
      throw new Exception("The service cannot be null!");
    }

    this.service = service;
    this.entityManager = service.getEntityManager();
    this.cb = service.cb;
    this.cq = service.cq;
    this.root = service.root;

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

    if (maxResults != null) {
      typedQuery.setMaxResults(maxResults);
    }

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
  @SuppressWarnings("unchecked")
  public Set<ENTITY> findItems() {

    if (!predicates.isEmpty()) {
      cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    }

    typedQuery = entityManager.createQuery(cq);

    if (maxResults != null) {
      typedQuery.setMaxResults(maxResults);
    }

    try {
      return (Set<ENTITY>) typedQuery.getResultList();
    } catch (Exception e) {
      return (Set<ENTITY>) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES, new HashSet<>());
    }

  }

  /**
   * WHERE clause in the SELECT statement to filter rows in the result set.
   * 
   * <pre>
   * SELECT c FROM Car c where c.id != 123
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param value is the filter to be applied
   * @return this
   */
  public Finder<ENTITY> equalTo(SingularAttribute<ENTITY, ?> attribute, Object value) {
    if (attribute != null && value != null) {
      predicates.add(cb.equal(root.get(attribute), value));
    }
    return this;
  }

  /**
   * WHERE clause in the SELECT statement to filter rows in the result set.
   * 
   * <pre>
   * SELECT c FROM Car c where c.id = 123
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param value is the filter to be applied
   * @return this
   */
  public Finder<ENTITY> equalTo(Join<ENTITY, ? extends Model> join,
      SingularAttribute<?, ?> attribute, Object value) {
    if (join != null && attribute != null && value != null) {
      predicates.add(cb.equal(join.get(attribute.getName()), value));
    }
    return this;
  }

  /**
   * WHERE NOT clause in the SELECT statement to filter rows in the result set.
   * 
   * <pre>
   * SELECT c FROM Car c where c.id != 123
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param value is the filter to be applied
   * @return this
   */
  public Finder<ENTITY> notEqualTo(SingularAttribute<ENTITY, ?> attribute, Object value) {
    if (attribute != null && value != null) {
      predicates.add(cb.notEqual(root.get(attribute), value));
    }
    return this;
  }

  /**
   * WHERE NOT clause in the SELECT statement to filter rows in the result set.
   * 
   * <pre>
   * SELECT c FROM Car c where c.id != 123
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param value is the filter to be applied
   * @return this
   */
  public Finder<ENTITY> notEqualTo(Join<ENTITY, ? extends Model> join,
      SingularAttribute<?, ?> attribute, Object value) {
    if (join != null && attribute != null && value != null) {
      predicates.add(cb.notEqual(join.get(attribute.getName()), value));
    }
    return this;
  }

  /**
   * Selects values within a given range dates
   * 
   * <pre>
   * SELECT c FROM Car c where c.year between '1999-01-01' and '2017-31-12'
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM date
   * @param to the TO date
   * @return this
   */
  public Finder<ENTITY> between(SingularAttribute<ENTITY, Date> attribute, Date from, Date to) {
    greaterThan(attribute, from);
    lessThan(attribute, to);
    return this;
  }

  /**
   * Selects values within a given range dates
   * 
   * <pre>
   * SELECT c FROM Car c where c.year between '1999-01-01' and '2017-31-12'
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM date
   * @param to the TO date
   * @return this
   */
  public Finder<ENTITY> between(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Date> attribute, Date from, Date to) {
    greaterThan(join, attribute, from);
    lessThan(join, attribute, to);
    return this;
  }

  /**
   * Selects values within a given range
   *
   * <pre>
   * SELECT c FROM Car c where c.year between 1999 and 2018
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM
   * @param to the
   * @return this
   */
  public Finder<ENTITY> between(SingularAttribute<ENTITY, Integer> attribute, Integer from,
      Integer to) {
    greaterThan(attribute, from);
    lessThan(attribute, to);
    return this;
  }

  /**
   * Selects values within a given range
   *
   * <pre>
   * SELECT c FROM Car c where c.year between 1999 and 2018
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM
   * @param to the
   * @return this
   */
  public Finder<ENTITY> between(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Integer> attribute, Integer from,
      Integer to) {
    greaterThan(join, attribute, from);
    lessThan(join, attribute, to);
    return this;
  }

  /**
   * Selects values within a given range
   * 
   * <pre>
   * SELECT c FROM Car c where c.year between 1999 and 2018
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM
   * @param to the
   * @return this
   */
  public Finder<ENTITY> between(SingularAttribute<ENTITY, Double> attribute, Double from,
      Integer to) {
    greaterThan(attribute, from);
    lessThan(attribute, to);
    return this;
  }

  /**
   * Selects values within a given range
   * 
   * <pre>
   * SELECT c FROM Car c where c.year between 1999 and 2018
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM
   * @param to the
   * @return this
   */
  public Finder<ENTITY> between(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Double> attribute, Double from,
      Integer to) {
    greaterThan(join, attribute, from);
    lessThan(join, attribute, to);
    return this;
  }

  /**
   * Selects values within a given range
   * 
   * <pre>
   * SELECT c FROM Car c where c.year between 1999 and 2018
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM
   * @param to the
   * @return this
   */
  public Finder<ENTITY> between(SingularAttribute<ENTITY, Float> attribute, Float from, Float to) {
    greaterThan(attribute, from);
    lessThan(attribute, to);
    return this;
  }

  /**
   * Selects values within a given range
   * 
   * <pre>
   * SELECT c FROM Car c where c.year between 1999 and 2018
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM
   * @param to the
   * @return this
   */
  public Finder<ENTITY> between(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Float> attribute, Float from, Float to) {
    greaterThan(join, attribute, from);
    lessThan(join, attribute, to);
    return this;
  }

  /**
   * Selects values within a given range
   * 
   * <pre>
   * SELECT c FROM Car c where c.year between 1999 and 2018
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM
   * @param to the
   * @return this
   */
  public Finder<ENTITY> between(SingularAttribute<ENTITY, Long> attribute, Long from, Integer to) {
    greaterThan(attribute, from);
    lessThan(attribute, to);
    return this;
  }

  /**
   * Selects values within a given range
   * 
   * <pre>
   * SELECT c FROM Car c where c.year between 1999 and 2018
   * </pre>
   * 
   * @param attribute the entity's date attribute
   * @param from the FROM
   * @param to the
   * @return this
   */
  public Finder<ENTITY> between(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Long> attribute, Long from, Long to) {
    greaterThan(join, attribute, from);
    lessThan(join, attribute, to);
    return this;
  }

  /**
   * Grater than the given Date
   *
   * <pre>
   * SELECT c FROM Car c where c.year > '1999-31-12'
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the Date
   * @return this
   */
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, Date> attribute, Date value) {
    if (attribute != null && value != null) {
      Path<Date> objAttribute = root.get(attribute);
      predicates.add(cb.greaterThan(objAttribute, value));
    }
    return this;
  }

  /**
   * Grater than the given Date
   *
   * <pre>
   * SELECT c FROM Car c where c.year > '1999-31-12'
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the Date
   * @return this
   */
  public Finder<ENTITY> greaterThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<?, Date> attribute, Date value) {
    if (join != null && attribute != null && value != null) {
      Path<Date> objAttribute = join.get(attribute.getName());
      predicates.add(cb.greaterThan(objAttribute, value));
    }
    return this;
  }

  /**
   * Grater than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year > 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the integer value
   * @return this
   */
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, Integer> attribute, int value) {
    if (attribute != null) {
      Path<? extends Number> objAttribute = root.get(attribute);
      predicates.add(cb.gt(objAttribute, (int) value));
    }
    return this;
  }

  /**
   * Grater than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year > 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the integer value
   * @return this
   */
  public Finder<ENTITY> greaterThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Integer> attribute, int value) {
    if (join != null && attribute != null) {
      Path<? extends Number> objAttribute = join.get(attribute.getName());
      predicates.add(cb.gt(objAttribute, (int) value));
    }
    return this;
  }

  /**
   * Grater than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year > 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the double value
   * @return this
   */
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, Double> attribute, double value) {
    if (attribute != null) {
      Path<? extends Number> objAttribute = root.get(attribute);
      predicates.add(cb.gt(objAttribute, (double) value));
    }
    return this;
  }

  /**
   * Grater than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year > 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the double value
   * @return this
   */
  public Finder<ENTITY> greaterThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Double> attribute, double value) {
    if (join != null && attribute != null) {
      Path<? extends Number> objAttribute = join.get(attribute.getName());
      predicates.add(cb.gt(objAttribute, (double) value));
    }
    return this;
  }

  /**
   * Grater than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year > 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the float value
   * @return this
   */
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, Float> attribute, float value) {
    if (attribute != null) {
      Path<? extends Number> objAttribute = root.get(attribute);
      predicates.add(cb.gt(objAttribute, (float) value));
    }
    return this;
  }

  /**
   * Grater than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year > 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the float value
   * @return this
   */
  public Finder<ENTITY> greaterThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Float> attribute, float value) {
    if (join != null && attribute != null) {
      Path<? extends Number> objAttribute = join.get(attribute.getName());
      predicates.add(cb.gt(objAttribute, (float) value));
    }
    return this;
  }

  /**
   * Grater than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year > 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the long value
   * @return this
   */
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, Long> attribute, long value) {
    if (attribute != null) {
      Path<? extends Number> objAttribute = root.get(attribute);
      predicates.add(cb.gt(objAttribute, (long) value));
    }
    return this;
  }

  /**
   * Grater than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year > 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the long value
   * @return this
   */
  public Finder<ENTITY> greaterThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Long> attribute, long value) {
    if (join != null && attribute != null) {
      Path<? extends Number> objAttribute = join.get(attribute.getName());
      predicates.add(cb.gt(objAttribute, (long) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= '1999-31-12'
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the DATE value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, Date> attribute,
      Date value) {
    if (attribute != null && value != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get(attribute), (Date) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= '1999-31-12'
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the DATE value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Date> attribute,
      Date value) {
    if (join != null && attribute != null && value != null) {
      predicates.add(cb.greaterThanOrEqualTo(join.get(attribute.getName()), (Date) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the integer value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, Integer> attribute,
      int value) {
    if (attribute != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get(attribute), (int) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the integer value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Integer> attribute,
      int value) {
    if (join != null && attribute != null) {
      predicates.add(cb.greaterThanOrEqualTo(join.get(attribute.getName()), (int) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the double value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, Double> attribute,
      double value) {
    if (attribute != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get(attribute), (double) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the double value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Double> attribute,
      double value) {
    if (join != null && attribute != null) {
      predicates.add(cb.greaterThanOrEqualTo(join.get(attribute.getName()), (double) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the float value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, Float> attribute,
      float value) {
    if (attribute != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get(attribute), (float) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the float value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Float> attribute,
      float value) {
    if (join != null && attribute != null) {
      predicates.add(cb.greaterThanOrEqualTo(join.get(attribute.getName()), (float) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the long value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, Long> attribute,
      long value) {
    if (attribute != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get(attribute), (long) value));
    }
    return this;
  }

  /**
   * Grater than or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year >= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the long value
   * @return this
   */
  public Finder<ENTITY> greaterThanOrEqualTo(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Long> attribute,
      long value) {
    if (join != null && attribute != null) {
      predicates.add(cb.greaterThanOrEqualTo(join.get(attribute.getName()), (long) value));
    }
    return this;
  }

  /**
   * Lesser than the given Date
   *
   * <pre>
   * SELECT c FROM Car c where c.year < '1999-31-12'
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the Date
   * @return this
   */
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, Date> attribute, Date value) {
    if (attribute != null && value != null) {
      Path<Date> objAttribute = root.get(attribute);
      predicates.add(cb.lessThan(objAttribute, value));
    }
    return this;
  }

  /**
   * Lesser than the given Date
   *
   * <pre>
   * SELECT c FROM Car c where c.year < '1999-31-12'
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the Date
   * @return this
   */
  public Finder<ENTITY> lessThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<?, Date> attribute, Date value) {
    if (join != null && attribute != null && value != null) {
      Path<Date> objAttribute = join.get(attribute.getName());
      predicates.add(cb.lessThan(objAttribute, value));
    }
    return this;
  }

  /**
   * Lesser than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year < 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the integer value
   * @return this
   */
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, Integer> attribute, int value) {
    if (attribute != null) {
      Path<? extends Number> objAttribute = root.get(attribute);
      predicates.add(cb.lt(objAttribute, (int) value));
    }
    return this;
  }

  /**
   * Lesser than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year < 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the integer value
   * @return this
   */
  public Finder<ENTITY> lessThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Integer> attribute, int value) {
    if (join != null && attribute != null) {
      Path<? extends Number> objAttribute = join.get(attribute.getName());
      predicates.add(cb.lt(objAttribute, (int) value));
    }
    return this;
  }

  /**
   * Lesser than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year < 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the double value
   * @return this
   */
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, Double> attribute, double value) {
    if (attribute != null) {
      Path<? extends Number> objAttribute = root.get(attribute);
      predicates.add(cb.lt(objAttribute, (double) value));
    }
    return this;
  }

  /**
   * Lesser than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year < 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the double value
   * @return this
   */
  public Finder<ENTITY> lessThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Double> attribute, double value) {
    if (join != null && attribute != null) {
      Path<? extends Number> objAttribute = join.get(attribute.getName());
      predicates.add(cb.lt(objAttribute, (double) value));
    }
    return this;
  }

  /**
   * Lesser than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year < 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the float value
   * @return this
   */
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, Float> attribute, float value) {
    if (attribute != null) {
      Path<? extends Number> objAttribute = root.get(attribute);
      predicates.add(cb.lt(objAttribute, (float) value));
    }
    return this;
  }

  /**
   * Lesser than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year < 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the float value
   * @return this
   */
  public Finder<ENTITY> lessThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Float> attribute, float value) {
    if (join != null && attribute != null) {
      Path<? extends Number> objAttribute = join.get(attribute.getName());
      predicates.add(cb.lt(objAttribute, (float) value));
    }
    return this;
  }

  /**
   * Lesser than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year < 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the long value
   * @return this
   */
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, Long> attribute, long value) {
    if (attribute != null) {
      Path<? extends Number> objAttribute = root.get(attribute);
      predicates.add(cb.lt(objAttribute, (Long) value));
    }
    return this;
  }

  /**
   * Lesser than the given value
   *
   * <pre>
   * SELECT c FROM Car c where c.year < 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the long value
   * @return this
   */
  public Finder<ENTITY> lessThan(Join<ENTITY, ? extends Model> join,
      SingularAttribute<? extends Model, Long> attribute, long value) {
    if (join != null && attribute != null) {
      Path<? extends Number> objAttribute = join.get(attribute.getName());
      predicates.add(cb.lt(objAttribute, (Long) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= '1999-31-12'
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the Date value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, Date> attribute, Date value) {
    if (attribute != null && value != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get(attribute), (Date) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= '1999-31-12'
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the Date value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Date> attribute, Date value) {
    if (join != null && attribute != null && value != null) {
      predicates.add(cb.lessThanOrEqualTo(join.get(attribute.getName()), (Date) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the integer value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, Integer> attribute,
      int value) {
    if (attribute != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get(attribute), (int) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the integer value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Integer> attribute,
      int value) {
    if (join != null && attribute != null) {
      predicates.add(cb.lessThanOrEqualTo(join.get(attribute.getName()), (int) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the double value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, Double> attribute,
      double value) {
    if (attribute != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get(attribute), (double) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the double value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Double> attribute,
      double value) {
    if (join != null && attribute != null) {
      predicates.add(cb.lessThanOrEqualTo(join.get(attribute.getName()), (double) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the float value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, Float> attribute,
      float value) {
    if (attribute != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get(attribute), (float) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the float value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Float> attribute,
      float value) {
    if (join != null && attribute != null) {
      predicates.add(cb.lessThanOrEqualTo(join.get(attribute.getName()), (float) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the long value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, Long> attribute, long value) {
    if (attribute != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get(attribute), (long) value));
    }
    return this;
  }

  /**
   * Lesser or equals to the given value
   * 
   * <pre>
   * SELECT c FROM Car c where c.year <= 1999
   * </pre>
   * 
   * @param attribute the entity's attribute
   * @param value the long value
   * @return this
   */
  public Finder<ENTITY> lesserThanOrEquals(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, Long> attribute, long value) {
    if (join != null && attribute != null) {
      predicates.add(cb.lessThanOrEqualTo(join.get(attribute.getName()), (long) value));
    }
    return this;
  }

  /**
   * ORDER BY clause to sort the result <b>ASC</b> or <b>DESC</b> based on the given attribute
   * 
   * @param attribute
   * @param orderBy
   * @return
   */
  public Finder<ENTITY> orderBy(SingularAttribute<ENTITY, ?> attribute, OrderBy orderBy) {
    if (attribute != null && orderBy != null) {
      switch (orderBy) {
        case ASC:
          cq.orderBy(cb.asc(root.get(attribute)));
          break;
        case DESC:
          cq.orderBy(cb.desc(root.get(attribute)));
          break;
        default:
          break;
      }
    }

    return this;
  }

  /**
   * ORDER BY clause to sort the result <b>ASC</b> or <b>DESC</b> based on the given attribute
   * 
   * @param attribute
   * @param orderBy
   * @return
   */

  public Finder<ENTITY> orderBy(Join<ENTITY, ? extends Model> join,
      SingularAttribute<ENTITY, ?> attribute, OrderBy orderBy) {
    if (join != null && attribute != null && orderBy != null) {
      switch (orderBy) {
        case ASC:
          cq.orderBy(cb.asc(join.get(attribute.getName())));
          break;
        case DESC:
          cq.orderBy(cb.desc(join.get(attribute.getName())));
          break;
        default:
          break;
      }
    }


    return this;
  }

  /**
   * LIMIT clause that is used to specify the number of records to return.
   * 
   * @param maxResults the number of record to return
   * @return this
   */
  public Finder<ENTITY> maxItems(Integer maxResults) {
    this.maxResults = maxResults;
    return this;
  }

}
