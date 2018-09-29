package ro.brage.dodo.jpa.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ddqqod
 */
public class Arrays {

    protected final static int DEFAULT_QUANTITY_PER_LIST = 2000;

    /**
     * Returns a map of lists with a size of 2000 per list
     *
     * @param list the list of values
     * @return a Map
     * @see DEFAULT_QUANTITY_PER_LIST
     */
    public static Map<Long, List<Object>> splitList(List<Object> list) {
        return splitList(list, DEFAULT_QUANTITY_PER_LIST);
    }

    /**
     * Returns a map of lists with a given size per list
     *
     * @param list the list of values
     * @param quantityPerList the quantity of values per List
     * @return a Map
     * @see DEFAULT_QUANTITY_PER_LIST
     */
    public static Map<Long, List<Object>> splitList(List<Object> list, int quantityPerList) {
        Map<Long, List<Object>> mapList = new HashMap<>();

        float dividers = Math.round(((double) list.size() / quantityPerList));
        if (dividers == 0) {
            dividers++;
        }

        for (Long key = 0L; key < dividers; key++) {
            mapList.put(key, new ArrayList<>());
        }

        int lastPos = 0;
        for (Long key = 0L; key < dividers; key++) {

            for (int value = 1; value <= quantityPerList; value++) {

                if ((lastPos + value) >= list.size()) {
                    break;
                }

                mapList.get(key).add(list.get(lastPos + value));

                if ((lastPos + value) == (lastPos + quantityPerList)) {
                    lastPos = list.indexOf(list.get(lastPos + value));
                }

            }

        }

        return mapList;
    }
}
