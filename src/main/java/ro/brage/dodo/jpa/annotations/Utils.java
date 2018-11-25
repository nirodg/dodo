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
package ro.brage.dodo.jpa.annotations;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Utility class
 * 
 * @author Dorin Brage
 */
public class Utils {

  private final static Logger LOGGER = LoggerFactory.getLogger(Utils.class);

  private final static String TRANSIENT_ANNOTATION = "javax.persistence.Transient";

  /**
   * Create a set of Modifiers
   * 
   * @param objects the modifiers
   * @return a set of modifiers
   */
  public static Set<Modifier> getCustomModifier(Modifier... objects) {
    Set<Modifier> type = new HashSet<>();

    for (Object modifier : objects) {
      type.add((Modifier) modifier);
    }
    return type;
  }

  /**
   * Check if the type of class is not a complex type.
   * 
   * @param val
   * @return
   * @throws ClassNotFoundException
   */
  public static boolean isNotComplexType(String val) throws ClassNotFoundException {

    // primitive
    if (val.equals("byte") ||
        val.equals("short") ||
        val.equals("int") ||
        val.equals("long") ||
        val.equals("float") ||
        val.equals("double") ||
        val.equals("boolean") ||
        val.equals("char")) {
      LOGGER.info("{} is a primitive value", val);
      return true;
    }

    // wraps a primitive value
    try {
      Class<?> clazz = Class.forName(val);
      if (clazz.equals(Boolean.class) ||
          clazz.equals(Integer.class) ||
          clazz.equals(Character.class) ||
          clazz.equals(Byte.class) ||
          clazz.equals(Short.class) ||
          clazz.equals(Double.class) ||
          clazz.equals(Long.class) ||
          clazz.equals(Float.class)) {
        LOGGER.info("{} wraps a primitive value", val);
        return true;
      }
    } catch (Exception e) {
      LOGGER.warn("{} is not primitive", val);
    }

    // implements Serializable
    try {
      Class<?> clazz = Class.forName(val);
      if (clazz.equals(String.class) ||
          clazz.equals(Date.class)) {
        LOGGER.info("{} implements the Serializable", val);
        return true;
      }

    } catch (Exception e) {
      LOGGER.warn("{} does not implement Serializable", val);
    }


    // is collection
    try {
      Class<?> clazz = Class.forName(val);
      if (clazz.equals(Set.class) ||
          clazz.equals(List.class) ||
          clazz.equals(Map.class)) {
        LOGGER.info("{} is a collection", val);
        return true;
      }

    } catch (Exception e) {
      LOGGER.warn("{} is not a collection", val);
    }


    LOGGER.warn(
        "The type of Class is not either primitive, a collection or impletends Serializable!");

    return false;
  }

  /**
   * Check if the value is one of the required annotations in order to define the value on the
   * generated Finder
   * 
   * @param property
   * @return
   */
  protected static boolean isFieldNotRequired(String property) {
    if (property == null) {
      return false;
    }

    if (property.equals(TRANSIENT_ANNOTATION)) {
      return false;
    }

    return true;
  }
  
  public static Method[] extractMethods(String val) throws ClassNotFoundException {
    return extractMethods(val);
  }

  public static Method[] extractMethods(Object val) throws ClassNotFoundException {

    Class<?> clazz = null;

    if (val instanceof String) {
      clazz = Class.forName((String) val);
    } else if (val instanceof Comparable) {
      System.out.println(((Comparable<?>) val).getClass());
    } else {
      clazz = ((Class<?>) val);
    }

    return clazz.getDeclaredMethods();

  }
}
