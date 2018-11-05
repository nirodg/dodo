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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import ro.brage.dodo.jpa.Model;

/**
 * Represents the annotated class
 * 
 * @author Dorin Brage
 *
 */
public class AnnotatedClass {

  private final static String ENTITY_ANNOTATION = "javax.persistence.Entity";
  private final static String COLUMN_ANNOTATION = "javax.persistence.Column";
  private final static String ONE_TO_MANY_ANNOTATION = "javax.persistence.OneToMany";
  private final static String ONE_TO_ONE_ANNOTATION = "javax.persistence.OneToOne";
  private final static String MANY_TO_ONE_ANNOTATION = "javax.persistence.ManyToOne";
  private final static String MANY_TO_MANY_ANNOTATION = "javax.persistence.ManyToMany";
  private final static String JOIN_COLUMN_ANNOTATION = "javax.persistence.JoinColumn";
  private final static String ELEMENT_COLLETION_ANNOTATION = "javax.persistence.ElementCollection";
  private final static String EMBEDDED_ANNOTATION = "javax.persistence.Embedded";
  private final static String SERIAL_VERSION_UID = "serialVersionUID";
  private final static String TRANSIENT_ANNOTATION = "javax.persistence.Transient";


  private Name qualifiedName;
  private String className;
  private boolean entity;
  private boolean extendingModel;
  private HashMap<String, String> fields = new HashMap<>();

  public AnnotatedClass(TypeElement annotatedClazz, Finder annotation)
      throws ClassNotFoundException {
    qualifiedName = annotatedClazz.getQualifiedName();
    className = annotatedClazz.getSimpleName().toString();

    // Check if has the @Entity annotation
    for (AnnotationMirror annotationClass : annotatedClazz.getAnnotationMirrors()) {
      if (annotationClass.getAnnotationType().toString().equals(ENTITY_ANNOTATION)) {
        entity = true;
      }
    }

    if (entity) {
      // Add fields with the @Column, @Embedded ,@OneToMany,
      for (Element element : annotatedClazz.getEnclosedElements()) {
        if (element.getKind().isField()
            && !element.getSimpleName().toString().equals(SERIAL_VERSION_UID)) {

          // check if the element has @Transfient annotation, if exists won't be defined
          // in the generated Finder
          for (AnnotationMirror annotationsField : element.getAnnotationMirrors()) {
            String annotated = annotationsField.getAnnotationType().toString();

            if (!annotated.equals(TRANSIENT_ANNOTATION)
                && !fields.containsKey(element.getSimpleName().toString())) {
              fields.put(element.getSimpleName().toString(), element.asType().toString());
            }
          }
          if (!fields.containsKey(element.getSimpleName().toString())) {
            fields.put(element.getSimpleName().toString(), element.asType().toString());
          }
        }
      }

      // Add fields from extended class, if is defined
      if (annotatedClazz.getSuperclass().toString().equals(Model.class.getCanonicalName())) {
        extendingModel = true; // It extends Model
        for (Field field : Model.class.getDeclaredFields()) {
          for (Annotation annotatedField : field.getDeclaredAnnotations()) {
            if (fields.get(field.getName()) == null
                && isPersistedProperty(annotatedField.annotationType().getCanonicalName())) {
              fields.put(field.getName(), field.getType().getSimpleName());
            }
          }
        }
      }

      System.out.println(className + " has " + fields.size());
    }
  }

  /**
   * Check if the value is one of the required annotations in order to define the value on the
   * generated Finder
   * 
   * @param property
   * @return
   */
  protected boolean isPersistedProperty(String property) {
    if (property == null) {
      return false;
    }

    if (property.equals("javax.persistence.Transient")) {
      return false;
    }

    return true; // TODO
    /*
     * if (property.equals(COLUMN_ANNOTATION) || property.equals(EMBEDDED_ANNOTATION) ||
     * property.equals(ONE_TO_MANY_ANNOTATION) || property.equals(ONE_TO_ONE_ANNOTATION) ||
     * property.equals(MANY_TO_ONE_ANNOTATION) || property.equals(MANY_TO_MANY_ANNOTATION) ||
     * property.equals(JOIN_COLUMN_ANNOTATION) || property.equals(ELEMENT_COLLETION_ANNOTATION)) {
     * return true; } return false;
     */
  }

  public Name getQualifiedName() {
    return qualifiedName;
  }


  public void setQualifiedName(Name qualifiedName) {
    this.qualifiedName = qualifiedName;
  }


  public String getClassName() {
    return className;
  }


  public void setClassName(String className) {
    this.className = className;
  }


  public boolean isEntity() {
    return entity;
  }


  public HashMap<String, String> getFields() {
    return fields;
  }

  public boolean isExtendingModel() {
    return extendingModel;
  }


}
