package ro.brage.dodo.jpa.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Utils {

  /**
   * Initializes all the fields given the object
   * 
   * @param clazz the class. It can be any POJO class
   * @param object the object to be initialized. It can be any POJO class
   * @throws IllegalAccessException in case the path was not created
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalArgumentException
   */
  public static void initializeFields(Class<?> clazz, Object object) throws IllegalAccessException,
      IllegalArgumentException, InstantiationException, ClassNotFoundException {
    for (Field field : clazz.getDeclaredFields()) {

      if (!field.isAccessible()) {
        field.setAccessible(true);
      }

      Type type = field.getGenericType();

      if (String.class.getName().equals(type.getTypeName())) {
        field.set(object, String.valueOf(""));

      } else if (Boolean.class.getName().equals(type.getTypeName())
          || boolean.class.getName().equals(type.getTypeName())) {
        field.set(object, Boolean.FALSE);

      } else if (Integer.class.getName().equals(type.getTypeName())
          || int.class.getName().equals(type.getTypeName())
          || byte.class.getName().equals(type.getTypeName())
          || long.class.getName().equals(type.getTypeName())
          || float.class.getName().equals(type.getTypeName())
          || double.class.getName().equals(type.getTypeName())) {

        field.set(object, 0);
      } else if (char.class.getName().equals(type.getTypeName())
          || Character.class.getName().equals(type.getTypeName())) {
        field.set(object, ' ');

      } else if (type.getTypeName().startsWith(List.class.getName())
          || type.getTypeName().startsWith(ArrayList.class.getName())) {
        field.set(object, new ArrayList<>());

      } else if (type.getTypeName().startsWith(Map.class.getName())
          || type.getTypeName().startsWith(HashMap.class.getName())) {
        field.set(object, new HashMap<>());

      } else if (type.getTypeName().startsWith(TreeMap.class.getName())) {
        field.set(object, new TreeMap<>());

      } else if (type.getTypeName().startsWith(LinkedHashMap.class.getName())) {
        field.set(object, new LinkedHashMap<>());

      } else if (type.getTypeName().startsWith(Set.class.getName())
          || type.getTypeName().startsWith(HashSet.class.getName())) {
        field.set(object, new HashSet<>());

      } else if (type.getTypeName().startsWith(Date.class.getName())) {
        field.set(object, new Date());

      } else {
        Object complexTypeField = field.getType().newInstance();
        initializeFields(field.getType(), complexTypeField);
        field.set(object, complexTypeField);
      }
    }
  }



  /**
   * Fixes the given path
   * 
   * @param path the path
   * @return the fixed path
   */
  private static String getCleanedPath(String path) {
    // Replaces all the slashes and backshales based on the OS's file separator
    path = path.replace("\\", System.getProperty("file.separator"));
    path = path.replace("/", System.getProperty("file.separator"));

    if (!path.endsWith(System.getProperty("file.separator"))) {
      path = path + System.getProperty("file.separator");
    }
    return path;
  }
}
