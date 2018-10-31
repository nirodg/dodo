package ro.brage.dodo.jpa.annotations;

import java.util.HashMap;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

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

  private Name qualifiedName;
  private String className;
  private boolean entity;
  private HashMap<String, String> fields = new HashMap<>();


  public AnnotatedClass(TypeElement annotatedClazz, Finder annotation)
      throws ClassNotFoundException {
    qualifiedName = annotatedClazz.getQualifiedName();
    className = annotatedClazz.getSimpleName().toString();

    // Check if has the @Entity annotation
    for (AnnotationMirror annotationClass : annotatedClazz.getAnnotationMirrors()) {
      if (annotationClass.getAnnotationType().toString().equals(ENTITY_ANNOTATION)) {
        System.out.println("Is an entity");
        entity = true;
      }
    }

    // Add fields with the @Column, @Embedded ,@OneToMany,
    for (Element element : annotatedClazz.getEnclosedElements()) {
      if (element.getKind().isField() && !element.getKind().toString().equals(SERIAL_VERSION_UID)) {
        for (AnnotationMirror annotationsField : element.getAnnotationMirrors()) {
          String annotated = annotationsField.getAnnotationType().toString();
          if (fields.get(annotated) == null && annotated.equals(COLUMN_ANNOTATION)
              || annotated.equals(EMBEDDED_ANNOTATION)
              || annotated.equals(ONE_TO_MANY_ANNOTATION)
              || annotated.equals(ONE_TO_ONE_ANNOTATION) || annotated.equals(MANY_TO_ONE_ANNOTATION)
              || annotated.equals(MANY_TO_MANY_ANNOTATION)
              || annotated.equals(JOIN_COLUMN_ANNOTATION)
              || annotated.equals(ELEMENT_COLLETION_ANNOTATION)) {
            fields.put(element.getSimpleName().toString(), element.asType().toString());
          }
        }
      }
    }

    System.out.println(className + " has " + fields.size() + " fields with @Column's annotation");

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


}
