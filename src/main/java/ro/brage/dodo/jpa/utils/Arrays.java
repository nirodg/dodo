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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains methods for manipulating Arrays
 * 
 * @author Dorin Brage
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
  public static Map<Long, List<Object>> splitList(List<?> list) {
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
  public static Map<Long, List<Object>> splitList(List<?> list, int quantityPerList) {
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
