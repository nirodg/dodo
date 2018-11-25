package ro.brage.dodo.jpa.queries;

import java.util.List;
import javax.persistence.metamodel.Attribute;
import ro.brage.dodo.jpa.EntityService;
import ro.brage.dodo.jpa.Finder;
import ro.brage.dodo.jpa.Model;

public class SqlHelper<T extends Model, S> {

  private Finder<? extends Model> finder;
  private Attribute<? extends Model, ?> attribute;

  public SqlHelper(EntityService<? extends Model> service) throws Exception {
    this.finder = new Finder<>(service);
  }


  public S equalsTo(Object value) {
    finder.equalsTo(attribute, value);
    return null;
  }


  public S notEqualsTo(Object value) {
    finder.notEqualsTo(attribute, value);
    return null;
  }


  public S lessThan(Object value) {
    finder.lessThan(attribute, value);
    return null;
  }


  public S lesserThanOrEquals(Object value) {
    finder.lesserThanOrEquals(attribute, value);
    return null;
  }


  public S greaterThan(Object value) {
    finder.greaterThan(attribute, value);
    return null;
  }


  public S greaterThanOrEqualTo(Object value) {
    finder.greaterThanOrEqualTo(attribute, value);
    return null;
  }


  public S between(Object from, Object to) {
    finder.between(attribute, from, to);
    return null;
  }

  public S in(List<?> values) {
    finder.in(attribute, values);
    return null;
  }


  public void maxItems(Integer maxResults) {
    finder.maxItems(maxResults);
  }


  public void distinct() {
    finder.distinct(true);
  }


  @SuppressWarnings("unchecked")
  public T getItem() {
    return (T) finder.findItem();
  }

  @SuppressWarnings("unchecked")
  public List<T> getItems() {
    return (List<T>) finder.findItems();
  }


  public Finder<? extends Model> getFinder() {
    return finder;
  }


  public void setAttribute(Attribute<? extends Model, ?> attribute) {
    this.attribute = attribute;
  }



}
