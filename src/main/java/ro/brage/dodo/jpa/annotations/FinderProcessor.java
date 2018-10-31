package ro.brage.dodo.jpa.annotations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import com.google.auto.service.AutoService;
import ro.brage.dodo.jpa.Model;
import ro.brage.dodo.jpa.annotations.enums.ProcessingError;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FinderProcessor extends AbstractProcessor {

  /** The Generator factory */
  private ClassBuilder generator;

  protected Elements elements;
  protected Filer filer;
  protected RoundEnvironment roundEnv;

  /** Contains all the annotated interfaces */
  protected Map<String, AnnotatedClass> container;

  protected Set<AnnotatedClass> processedInterfaces;

  public final Elements getElements() {
    return elements;
  }

  public final void setElements(Elements elements) {
    this.elements = elements;
  }

  public final Filer getFiler() {
    return filer;
  }

  public final void setFiler(Filer filer) {
    this.filer = filer;
  }

  public Map<String, AnnotatedClass> getContainer() {
    return container;
  }

  public void setContainer(Map<String, AnnotatedClass> container) {
    this.container = container;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new LinkedHashSet<String>();
    annotations.add(Finder.class.getCanonicalName());
    return annotations;
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elements = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
    container = new HashMap<String, AnnotatedClass>();
    processedInterfaces = new HashSet<>();
  }

  public void getAnnotatedInterfaces() throws Exception {
    try {
      // Iterate over each class
      for (Element element : roundEnv.getElementsAnnotatedWith(Finder.class)) {


        checkAnnotatedClasses(element);

        // The class with the annotation to be tested
        TypeElement annotatedClazz = (TypeElement) element;

        // It checks to do not generate the class twice
        if (!container.containsKey(annotatedClazz.getSimpleName().toString())) {
          container.put(annotatedClazz.getSimpleName().toString(),
              new AnnotatedClass(annotatedClazz, element.getAnnotation(Finder.class)));
        } else {
          throw new Exception(ProcessingError.CLASS_CANT_BE_DUPLICATED.name());
        }

      }
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  /**
   * Checks if the annotation was used only by interfaces
   * 
   * @param element Represents a program element such as a package, class, or method
   * @return {@code true} if the element is an {@code Interface}, {@code false} is it's not
   * @throws Exception
   */
  protected boolean checkAnnotatedClasses(Element element) throws Exception {


    if (!element.getKind().equals(ElementKind.CLASS)) {

      processingEnv.getMessager()
          .printMessage(Diagnostic.Kind.ERROR, "Only for classes", element);


      throw new Exception(ProcessingError.PROCESSOR_ONLY_CLASSES.name());
    }
    return true;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    this.roundEnv = roundEnv;
    generator = new ClassBuilder();

    try {
      getAnnotatedInterfaces();
    } catch (Exception e) {
      e.printStackTrace();
    }

    for (Entry<String, AnnotatedClass> item : container.entrySet()) {
      if (!processedInterfaces.contains(item.getValue()) && item.getValue().isEntity()) {

        TypeElement annotatedClazz = null;
        try {

          for (Element element : roundEnv.getElementsAnnotatedWith(Finder.class)) {
            if (element.getSimpleName().toString().equals(item.getKey())) {
              annotatedClazz = (TypeElement) element;
            }
          }

          generator.init(annotatedClazz, container.get(item.getKey()), filer, elements).build();
        } catch (Exception e) {
          e.printStackTrace();
        }

        // add processed interface to the list
        processedInterfaces.add(item.getValue());
      }
    }
    return true;
  }

}
