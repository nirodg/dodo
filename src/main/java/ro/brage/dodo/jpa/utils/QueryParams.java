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
package ro.brage.dodo.jpa.utils;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.metamodel.SingularAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The QueryParams is an utility for the static queries
 * <hr>
 * The new way
 * 
 * <pre>
 * List<Todo> results = getService()
 *     .getResults("UPDATE Todo t SET t.enabled=0 where t.guid = :guid",
 *         new QueryParams()
 *             .addParameter(Todo_.enabled, false)
 *             .addParameter(Todo_.guid, guid));
 * </pre>
 * 
 * <hr>
 * The old way
 * 
 * <pre>
 * String updateQuery = "UPDATE Todo t SET t.enabled=0 where t.guid =  :guid";
 * 
 * getEntityManager()
 *     .createQuery(updateQuery)
 *     .setParameter("id", id).executeUpdate();
 * </pre>
 * 
 * @author Dorin Brage
 */
public class QueryParams {

  private final Logger LOG = LoggerFactory.getLogger(QueryParams.class);

  Map<String, Object> data;

  public QueryParams() {
    data = new HashMap<>();
  }

  public QueryParams addParameter(SingularAttribute<?, ?> key, Object value) {
    addParameter(key.getName(), value);
    return this;
  }


  public QueryParams addParameter(String key, Object value) {
    data.put(key, value);
    LOG.debug("QueryParams:addParameter({},{})", key, value);
    return this;
  }


  public Map<String, Object> getParams() {
    return data;
  }
}
