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
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.brage.dodo.jpa.Model;

/**
 * Represents the annotated class
 * 
 * @author Dorin Brage
 *
 */
public class AnnotatedClass {

  private final static Logger LOGGER = LoggerFactory.getLogger(AnnotatedClass.class);

  private final static String SERIAL_VERSION_UID = "serialVersionUID";
  private final static String ENTITY_ANNOTATION = "javax.persistence.Entity";


  private Name qualifiedName;
  private String className;
  private String namespace;
  private boolean entity;
  private boolean extendingModel;
  private HashMap<String, String> fields = new HashMap<>();

  private TypeElement typeElement;

  // [ project [ [ name, String ], [ active, boolean] ]
  // [ address [ [ street, String], [ provence, String] ]
  private Map<String, Map<String, String>> complextFields = new HashMap<>();



  public AnnotatedClass(TypeElement annotatedClazz, Finder annotation)
      throws ClassNotFoundException {
    typeElement = annotatedClazz;
    qualifiedName = annotatedClazz.getQualifiedName();
    className = annotatedClazz.getSimpleName().toString();
    namespace = annotatedClazz.getQualifiedName().toString().replace("." + className, "");

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

            if (Utils.isFieldNotRequired(annotated)
                && !fields.containsKey(element.getSimpleName().toString())) {
              fields.put(element.getSimpleName().toString(), element.asType().toString());
              if (!Utils.isNotComplexType(element.asType().toString())) {
                if (complextFields.get(element.getSimpleName().toString()) == null) {
                  complextFields.put(element.getSimpleName().toString(), new HashMap<>());
                }

                // ((Map)complextFields.get(element.getSimpleName().toString()).put(, value);

              }
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
                && Utils.isFieldNotRequired(annotatedField.annotationType().getCanonicalName())) {
              fields.put(field.getName(), field.getType().getCanonicalName());
            }
          }
        }
      }

      LOGGER.info("The entity {} has {} field/s", className, fields.size());
    }
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

  public String getNamespace() {
    return namespace;
  }

  public TypeElement getTypeElement() {
    return typeElement;
  }


}
