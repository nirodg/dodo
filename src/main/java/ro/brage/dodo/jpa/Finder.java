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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.brage.dodo.jpa.enums.JpaErrorKeys;
import ro.brage.dodo.jpa.enums.OrderBy;
import ro.brage.dodo.jpa.utils.JpaLog;

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

    typedQuery = entityManager.createQuery(cq).setFlushMode(FlushModeType.AUTO);

    if (maxResults != null) {
      typedQuery.setMaxResults(maxResults);
    }

    LOG.info("Finder => Finding single entity for {}", service.getEntityClass().getName());

    try {
      return typedQuery.getSingleResult();
    } catch (Exception e) {
      return (ENTITY) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITY, e, null);
    }
  }

  /**
   * Get a list of entities using Predicates
   *
   * @return a set of entities
   */
  @SuppressWarnings("unchecked")
  public List<ENTITY> findItems() {

    if (!predicates.isEmpty()) {
      cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    }

    typedQuery = entityManager.createQuery(cq).setFlushMode(FlushModeType.AUTO);

    if (maxResults != null) {
      typedQuery.setMaxResults(maxResults);
    }

    LOG.info("Finder => Finding multiples single entity for {} and {} max results ",
        service.getEntityClass().getName(), maxResults);

    try {
      return (List<ENTITY>) typedQuery.getResultList();
    } catch (Exception e) {
      return (List<ENTITY>) JpaLog.info(LOG, JpaErrorKeys.FAILED_TO_FIND_ENTITIES, e,
          new ArrayList<ENTITY>());
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
  public Finder<ENTITY> equalTo(SingularAttribute<? extends Model, ?> attribute, Object value) {
    if (attribute != null && value != null) {
      Path<Date> objAttribute = root.get(attribute.getName());
      predicates.add(cb.equal(objAttribute, value));
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
  public Finder<ENTITY> equalTo(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, String> attribute, String value) {
    if (joinEntity != null && attribute != null && value != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.equal(rootJoinEntity.get(attribute.getName()), value));
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
  public Finder<ENTITY> notEqualTo(SingularAttribute<Model, ?> attribute, Object value) {
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
  public Finder<ENTITY> notEqualTo(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<?, ?> attribute, Object value) {
    if (joinEntity != null && attribute != null && value != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.notEqual(rootJoinEntity.get(attribute.getName()), value));
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

  public Finder<ENTITY> between(SingularAttribute<? extends Model, Date> attribute, Date from,
      Date to) {
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
  public Finder<ENTITY> between(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Date> attribute, Date from, Date to) {
    greaterThan(joinEntity, attribute, from);
    lessThan(joinEntity, attribute, to);
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
  public Finder<ENTITY> between(SingularAttribute<Model, Integer> attribute, Integer from,
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
  public Finder<ENTITY> between(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Integer> attribute, Integer from, Integer to) {
    greaterThan(joinEntity, attribute, from);
    lessThan(joinEntity, attribute, to);
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
  public Finder<ENTITY> between(SingularAttribute<Model, Double> attribute, Double from,
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
  public Finder<ENTITY> between(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Double> attribute, Double from, Integer to) {
    greaterThan(joinEntity, attribute, from);
    lessThan(joinEntity, attribute, to);
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
  public Finder<ENTITY> between(SingularAttribute<Model, Float> attribute, Float from, Float to) {
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
  public Finder<ENTITY> between(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Float> attribute, Float from, Float to) {
    greaterThan(joinEntity, attribute, from);
    lessThan(joinEntity, attribute, to);
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
  public Finder<ENTITY> between(SingularAttribute<Model, Long> attribute, Long from, Integer to) {
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
  public Finder<ENTITY> between(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Long> attribute, Long from, Long to) {
    greaterThan(joinEntity, attribute, from);
    lessThan(joinEntity, attribute, to);
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
  public Finder<ENTITY> greaterThan(SingularAttribute<? extends Model, Date> attribute,
      Date value) {
    if (attribute != null && value != null) {
      predicates.add(cb.greaterThan(root.get(attribute.getName()), value));
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
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<?, Date> attribute, Date value) {
    if (joinEntity != null && attribute != null && value != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.greaterThan(rootJoinEntity.get(attribute.getName()), value));
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
  public Finder<ENTITY> greaterThan(SingularAttribute<Model, Integer> attribute, int value) {
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
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Integer> attribute, int value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      Path<? extends Number> objAttribute = rootJoinEntity.get(attribute.getName());
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
  public Finder<ENTITY> greaterThan(SingularAttribute<Model, Double> attribute, double value) {
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
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Double> attribute, double value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.gt(rootJoinEntity.get(attribute.getName()), (double) value));
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
  public Finder<ENTITY> greaterThan(SingularAttribute<Model, Float> attribute, float value) {
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
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Float> attribute, float value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.gt(rootJoinEntity.get(attribute.getName()), (float) value));
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
  public Finder<ENTITY> greaterThan(SingularAttribute<Model, Long> attribute, long value) {
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
  public Finder<ENTITY> greaterThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Long> attribute, long value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.gt(rootJoinEntity.get(attribute.getName()), (long) value));
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<? extends Model, Date> attribute,
      Date value) {
    if (attribute != null && value != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get(attribute.getName()), (Date) value));
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Date> attribute, Date value) {
    if (joinEntity != null && attribute != null && value != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates
          .add(cb.greaterThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (Date) value));
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<Model, Integer> attribute,
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Integer> attribute, int value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.greaterThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (int) value));
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<Model, Double> attribute,
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Double> attribute, double value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates
          .add(cb.greaterThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (double) value));
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<Model, Float> attribute,
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Float> attribute, float value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates
          .add(cb.greaterThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (float) value));
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<Model, Long> attribute, long value) {
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
  public Finder<ENTITY> greaterThanOrEqualTo(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Long> attribute, long value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates
          .add(cb.greaterThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (long) value));
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
  public Finder<ENTITY> lessThan(SingularAttribute<? extends Model, Date> attribute, Date value) {
    if (attribute != null && value != null) {
      Path<Date> objAttribute = root.get(attribute.getName());
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
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<?, Date> attribute, Date value) {
    if (joinEntity != null && attribute != null && value != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lessThan(rootJoinEntity.get(attribute.getName()), value));
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
  public Finder<ENTITY> lessThan(SingularAttribute<Model, Integer> attribute, int value) {
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
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Integer> attribute, int value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lt(rootJoinEntity.get(attribute.getName()), (int) value));
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
  public Finder<ENTITY> lessThan(SingularAttribute<Model, Double> attribute, double value) {
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
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Double> attribute, double value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lt(rootJoinEntity.get(attribute.getName()), (double) value));
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
  public Finder<ENTITY> lessThan(SingularAttribute<Model, Float> attribute, float value) {
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
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Float> attribute, float value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lt(rootJoinEntity.get(attribute.getName()), (float) value));
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
  public Finder<ENTITY> lessThan(SingularAttribute<Model, Long> attribute, long value) {
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
  public Finder<ENTITY> lessThan(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<? extends Model, Long> attribute, long value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lt(rootJoinEntity.get(attribute.getName()), (Long) value));
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<? extends Model, Date> attribute,
      Date value) {
    if (attribute != null && value != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get(attribute.getName()), (Date) value));
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Date> attribute, Date value) {
    if (joinEntity != null && attribute != null && value != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lessThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (Date) value));
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<Model, Integer> attribute, int value) {
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Integer> attribute, int value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lessThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (int) value));
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<Model, Double> attribute,
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Double> attribute, double value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lessThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (double) value));
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<Model, Float> attribute, float value) {
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Float> attribute, float value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lessThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (float) value));
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<Model, Long> attribute, long value) {
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
  public Finder<ENTITY> lesserThanOrEquals(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Long> attribute, long value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lessThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (long) value));
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
  public Finder<ENTITY> orderBy(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, ?> attribute, OrderBy orderBy) {

    if (joinEntity != null && attribute != null && orderBy != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      switch (orderBy) {
        case ASC:
          cq.orderBy(cb.asc(rootJoinEntity.get(attribute.getName())));
          break;
        case DESC:
          cq.orderBy(cb.desc(rootJoinEntity.get(attribute.getName())));
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

  /**
   * Will eliminate duplicated query results.
   * 
   * @return this
   */
  public Finder<ENTITY> distinct() {
    cq.distinct(true);
    return this;
  }

  /**
   * Specify whether duplicate query results will be eliminated. A true value will cause duplicates
   * to be eliminated. A false value will cause duplicates to be retained. If distinct has not been
   * specified, duplicate results must be retained. This method only overrides the return type of
   * the corresponding <code>AbstractQuery</code> method.
   * 
   * @param distinct boolean value specifying whether duplicate results must be eliminated from the
   *        query result or whether they must be retained
   * @return this
   */
  public Finder<ENTITY> distinct(boolean distinct) {
    cq.distinct(distinct);
    return this;
  }

  public Finder<ENTITY> in(SingularAttribute<Model, Long> attribute, List<E> values) {
    if (attribute != null) {
      cb.isTrue(root.get(attribute).in(values));
    }
    return this;
  }

  public Finder<ENTITY> in(SingularAttribute<ENTITY, ? extends Model> joinEntity,
      SingularAttribute<Model, Long> attribute, long value) {
    if (joinEntity != null && attribute != null) {
      Join<ENTITY, ? extends Model> rootJoinEntity = addJoin(joinEntity);
      predicates.add(cb.lessThanOrEqualTo(rootJoinEntity.get(attribute.getName()), (long) value));
    }
    return this;
  }

  /**
   * Create a left join to the specified single-valued attribute
   * 
   * @param entity the generated metamodel
   * @return the resulting <b>left</b> join
   * @see StaticMetamodel
   * @see SingularAttribute
   */
  private Join<ENTITY, ? extends Model> addJoin(
      SingularAttribute<ENTITY, ? extends Model> entity) {
    return addJoin(entity, JoinType.LEFT);
  }

  /**
   * Create a join to the specified single-valued attribute using the given join type.
   * 
   * @param entity
   * @param joinType
   * @return the resulting join
   * @see StaticMetamodel
   * @see SingularAttribute
   */
  @SuppressWarnings("unchecked")
  private Join<ENTITY, ? extends Model> addJoin(SingularAttribute<ENTITY, ? extends Model> entity,
      JoinType joinType) {

    Join<ENTITY, ? extends Model> joinEntity = null;

    LOG.info("=> Check already defined join for the entity");


    if (root.getJoins().isEmpty()) {
      return root.join(entity, joinType);
    }

    LOG.info("=> Total Joins {}", root.getJoins().size());

    String entityClassName = entity.getType().getJavaType().getSimpleName();

    for (Join<ENTITY, ?> join : root.getJoins()) {
      String joinClassName = join.getModel().getBindableJavaType().getSimpleName();
      if (!joinClassName.equals(entityClassName)) {
        joinEntity = root.join(entity, joinType);
        LOG.info("=> Will join the {}", entity.getName());
      } else {
        joinEntity = (Join<ENTITY, ? extends Model>) join;
        LOG.info("=> Returns already existing join : {}", entity.getName());
      }
    }

    return joinEntity;
  }


}
