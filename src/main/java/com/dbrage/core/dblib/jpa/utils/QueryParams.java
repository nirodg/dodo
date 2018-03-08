package com.dbrage.core.dblib.jpa.utils;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.metamodel.SingularAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dorin Brage
 */
//public class QueryParams<K extends SingularAttribute, O> {
public class QueryParams {

    private final Logger LOG = LoggerFactory.getLogger(QueryParams.class);

    Map<String, Object> data;

    public QueryParams() {
        data = new HashMap<>();
    }

    public QueryParams addParameter(SingularAttribute<?, ?> key, Object value) {
        data.put(key.getName(), value);
        LOG.debug("QueryParams:addParameter({},{})", key.getName(), value);
        return this;
    }

    public Map<String, Object> getParams() {
        return data;
    }
}
