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
package ro.brage.dodo.jpa.queries;

import java.util.List;
import ro.brage.dodo.jpa.Finder;

/**
 * This class will represent all SQL clauses which will be implemented for each entity using the
 * {@link Finder}'s annotation.
 * 
 * @author Dorin Brage
 *
 * @param <C> the new generated source
 * @param <T> the Entity
 */
public interface SqlClausule<T> {

  public SqlClausule<?> equalsTo(Object val);

  public SqlClausule<?> notEqualsTo(Object val);

  public SqlClausule<?> lessThan(Object val);

  public SqlClausule<?> lesserThanOrEquals(Object val);

  public SqlClausule<?> greaterThan(Object val);

  public SqlClausule<?> greaterThanOrEqualTo(Object val);

  public SqlClausule<?> between(Object val1, Object val2);

  public SqlClausule<?> distinct();

  public SqlClausule<?> in(List<Object> vals);

  public SqlClausule<?> maxItems(Integer maxResults);

  public T getItem();

  public List<T> getItems();
}

