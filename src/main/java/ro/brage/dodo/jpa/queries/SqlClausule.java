package ro.brage.dodo.jpa.queries;

import java.util.List;

public interface SqlClausule<C, T>{
  
  public C equalsTo(Object val);
  public C notEqualsTo(Object val);
  
  public T getItem();
  public List<T> getItems();
}

