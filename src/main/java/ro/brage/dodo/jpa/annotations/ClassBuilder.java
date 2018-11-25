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

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.StaticMetamodel;
import javax.tools.JavaFileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.squareup.javawriter.JavaWriter;
import ro.brage.dodo.jpa.EntityService;
import ro.brage.dodo.jpa.Model;
import ro.brage.dodo.jpa.queries.SqlClausule;

/**
 * The class builder defines the structure of the new generated source (aka template)
 * 
 * @author Dorin Brage
 *
 */
public class ClassBuilder {

  private final static Logger LOGGER = LoggerFactory.getLogger(ClassBuilder.class);

  private static final String GENERATED_CLASS_SUFFIX = "Finder";
  private static final String GENERATED_SUBCLASS_PREFIX = "Query";

  private Filer filer;
  private AnnotatedClass annotatedClass;
  private String finalGeneratedClass = null;
  private Set<MethodBuilder> methods;

  private boolean isInitialized;

  private JavaWriter jw;

  /**
   * The {@code init() } initializes the builder
   * 
   * @param typeAnnotatedClazz the annotated class
   * @param filer the {@link Filer}
   * @param elements a list of {@link Elements}
   * @param annotatedClass the annotated class
   * @return {@link ClassBuilder}
   * @throws Exception if it was unsuccessful
   */
  public ClassBuilder init(AnnotatedClass annotatedClass,
      Map<String, AnnotatedClass> annotatedClasses,
      Filer filer) throws Exception {

    if (filer == null || annotatedClass == null || annotatedClasses == null) {
      throw new Exception("Failed to start building the class");
    }

    this.annotatedClass = annotatedClass;
    this.filer = filer;

    /** Class name + suffix **/
    this.finalGeneratedClass = annotatedClass.getClassName().concat(GENERATED_CLASS_SUFFIX);

    this.isInitialized = true;

    return this;
  }

  public ClassBuilder build() throws Exception {

    LOGGER.info("Generating sources for {}", annotatedClass.getClassName());

    if (!this.isInitialized) {
      throw new Exception("INIT_NOT_DEFINED");
    }

    initWritter();

    setPackage();

    setImports();

    setGenerateAnnotation();

    setStartClass();

    defineMandatoryLogic();

    defineTypeSafeMethods();

    defineSubclasses();

    setEndClass();

    return this;
  }


  private void defineMandatoryLogic() throws IOException {


    // define other objects
    jw.emitField(
        String.format("protected Finder<%s>", annotatedClass.getQualifiedName().toString()),
        "finder");
    jw.emitField(String.format("protected EntityService<%s> ",
        annotatedClass.getQualifiedName().toString()), "service");

    jw.emitEmptyLine();

    String fieldAttribute = "protected Attribute<%s, ?>";
    if (annotatedClass.isExtendingModel()) {
      fieldAttribute = "protected Attribute<? extends Model, ?>";
    }
    jw.emitField(String.format(fieldAttribute,
        annotatedClass.getQualifiedName().toString()), "currentField");

    jw.emitEmptyLine();

    // Define constructor
    List<String> constructorParameters = Arrays.asList(
        String.format("EntityService<%s>", annotatedClass.getQualifiedName().toString()),
        "service");
    List<String> constructorThrowTypes = Arrays.asList(Exception.class.getSimpleName().toString());
    jw.beginConstructor(Utils.getCustomModifier(Modifier.PUBLIC), constructorParameters,
        constructorThrowTypes);
    jw.emitStatement("this.service = service", (Object[]) null);
    jw.emitStatement("this.finder = new Finder<>(service)", (Object[]) null);
    jw.endConstructor();

    // Override methods
    for (Method method : SqlClausule.class.getDeclaredMethods()) {

      String returnType =
          method.getAnnotatedReturnType().getType().getTypeName().toString().equals("C")
              ? finalGeneratedClass
              : method.getAnnotatedReturnType().getType().getTypeName().toString().replaceAll("T",
                  annotatedClass.getQualifiedName().toString());

      jw.emitAnnotation(Override.class.getCanonicalName());


      if (method.getParameterCount() > 0) {
        List<String> parameters = new ArrayList<>();
        for (int i = 0; i < method.getParameterCount(); i++) {
          parameters.add(method.getParameters()[i].getType().getSimpleName());
          parameters.add("arg" + i);
        }

        jw.beginMethod(returnType, method.getName(), Utils.getCustomModifier(Modifier.PUBLIC),
            parameters, null);

      } else {
        jw.beginMethod(returnType, method.getName(), Utils.getCustomModifier(Modifier.PUBLIC));
      }


      if (method.getName().toString().equals("getItem")) {
        jw.emitStatement("return finder.findItem()", (Object[]) null);
      } else if (method.getName().toString().equals("getItems")) {
        jw.emitStatement("return finder.findItems()", (Object[]) null);
      } else {

        String argsPattern = "";
        String[] argsNames = new String[method.getParameterCount()];

        for (int i = 0; i < method.getParameterCount();) {
          argsPattern += "%s";
          argsNames[i] = "arg" + i;
          // current != to getParameterCount and next count != getParameterCount
          if (i != method.getParameterCount() && (i + 1) != method.getParameterCount()) {
            argsPattern += ", ";
          }
          i++;
        }

        if (argsNames.length == 0) {
          jw.emitStatement("finder.%s()", method.getName());
        } else if (method.getName().toString().equals("maxItems")) {
          jw.emitStatement("finder.maxItems(" + argsPattern + ")",
              (Object[]) argsNames);
        } else {
          jw.emitStatement("finder." + method.getName() + "(currentField, " + argsPattern + ")",
              (Object[]) argsNames);
        }

        jw.emitStatement("return this", (Object[]) null);
      }

      jw.endMethod();

    }

  }

  private void defineTypeSafeMethods()
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

    methods = new HashSet<>();

    // Define methods
    for (Entry<String, String> field : annotatedClass.getFields().entrySet()) {

      // define not complex type fields
      if (Utils.isNotComplexType(field.getValue())) {
        methods.add(new MethodBuilder(String.format("SqlClausule<%s, %s>", finalGeneratedClass,
            annotatedClass.getQualifiedName().toString()), field.getKey())
                .addStatements("currentField = %s", field.getKey()).addStatements("return this"));

      }

      if (!Utils.isNotComplexType(field.getValue())) {

        // extract and define methods from each class
        methods.add(new MethodBuilder(GENERATED_SUBCLASS_PREFIX + field.getKey(), field.getKey())
            .addStatements("// complex type")
            .addStatements("return (%s) this", GENERATED_SUBCLASS_PREFIX + field.getKey())
            .addEmptyLine());
      }


    }

    for (MethodBuilder method : methods) {
      jw.beginMethod(method.getTypeMethod(), method.getName(), method.getModifiers(), null, null);

      for (String statement : method.getStatements()) {
        if (statement.equals(MethodBuilder.EMPTY_LINE)) {
          jw.emitEmptyLine();
        } else {
          jw.emitStatement(statement, (Object[]) null);
        }
      }

      jw.endMethod();
      jw.emitEmptyLine();
    }

  }

  private void defineSubclasses() throws Exception {



    for (Entry<String, String> subclass : annotatedClass.getFields().entrySet()) {
      // define subclass for each complex type and its fields to be queried
      if (!Utils.isNotComplexType(subclass.getValue())) {

        try {
          // Beginning of the subclass
          // i.e. QueryUser extends EmailFinder {}
          jw.beginType(GENERATED_SUBCLASS_PREFIX + subclass.getKey(), "class",
              Utils.getCustomModifier(Modifier.PUBLIC),
              this.finalGeneratedClass);
          jw.emitEmptyLine();


          jw.beginConstructor(Utils.getCustomModifier(Modifier.PUBLIC),
              Arrays.asList(String.format("EntityService<%s>", annotatedClass.getQualifiedName()),
                  "service"),
              Arrays.asList("Exception"));
          jw.emitStatement("super(service)", (Object[]) null);
          jw.endConstructor();

        } catch (IOException e) {
          throw new Exception("SET_INIT_SUBCLASS");
        }

        // define methods
        // for (Entry<String, String> field : annotatedClass.getFields().entrySet()) {
        // if (field.getKey().equals(field.getKey())) {
        // if (!Utils.isNotComplexType(field.getValue())) {
        //
        // jw.beginMethod(subclassPrefix + subclass.getKey(), field.getKey(),
        // Utils.getCustomModifier(Modifier.PUBLIC), null,
        // null);
        //
        // jw.emitStatement("return this", (Object[]) null);
        //
        // jw.endMethod();
        // jw.emitEmptyLine();
        // }
        // }
        // }

        jw.emitSingleLineComment("define methods", (Object[]) null);


        try {
          // end of the subclass
          jw.endType();
        } catch (IOException e) {
          throw new Exception("SET_END_CLASS");
        }

      }
    }

  }

  /**
   * Initializes the writer
   * 
   * @throws Exception if it was unsuccessful
   */
  private void initWritter() throws Exception {
    JavaFileObject jfo;
    try {
      jfo = filer.createSourceFile(this.finalGeneratedClass);
      Writer writer = jfo.openWriter();
      jw = new JavaWriter(writer);
    } catch (IOException e) {
      throw new Exception("INIT_WRITER");
    }
  }



  /**
   * Initializes the package
   * 
   * @throws Exception if it was unsuccessful
   */
  private void setPackage() throws Exception {
    try {
      jw.emitPackage(annotatedClass.getNamespace());
    } catch (Exception e) {
      throw new Exception("INIT_PACKAGE");
    }
  }

  /**
   * Initializes the class
   * 
   * @throws Exception if it was unsuccessful
   */
  private void setStartClass() throws Exception {
    String implSqlClausule = String.format("%s<%s, %s>", SqlClausule.class.getSimpleName(),
        this.finalGeneratedClass, this.annotatedClass.getClassName());

    try {


      // Beginning of the class
      jw.beginType(this.finalGeneratedClass, "class", Utils.getCustomModifier(Modifier.PUBLIC),
          null,
          implSqlClausule);
      jw.emitEmptyLine();

    } catch (IOException e) {
      throw new Exception("SET_INIT_CLASS");
    }

  }

  /**
   * Sets the end of the class
   * 
   * @throws Exception if it was unsuccessful
   */
  private void setEndClass() throws Exception {

    try {
      jw.endType();
      jw.close();
    } catch (IOException e) {
      throw new Exception("SET_END_CLASS");
    }

  }

  /**
   * Defines the import classes
   * 
   * @throws Exception if it was unsuccessful
   */
  private void setImports() throws Exception {

    // Non-static classes
    List<String> imports = new ArrayList<String>();
    // Static classes
    List<String> staticImports = new ArrayList<String>();

    imports.add(annotatedClass.getQualifiedName().toString());
    imports.add(List.class.getCanonicalName());
    imports.add(Generated.class.getCanonicalName());
    imports.add(SqlClausule.class.getCanonicalName());
    imports.add(Attribute.class.getCanonicalName());
    imports.add(StaticMetamodel.class.getCanonicalName());
    imports.add(EntityService.class.getCanonicalName());
    imports.add(ro.brage.dodo.jpa.Finder.class.getCanonicalName());

    staticImports.add(annotatedClass.getQualifiedName().toString() + "_.*"); // import all fields
                                                                             // from the generated
                                                                             // JPA Model

    if (annotatedClass.isExtendingModel()) {
      imports.add(Model.class.getCanonicalName());
      staticImports.add(Model.class.getCanonicalName() + "_.*");
    }

    try {
      jw.emitImports(imports);
      jw.emitStaticImports(staticImports);
      jw.emitEmptyLine();
    } catch (IOException e) {
      throw new Exception("SET_IMPORTS");
    }
  }


  /**
   * Sets the Generate's annotation with some additional information regarding the generated class
   * 
   * @throws Exception if it was unsuccessful
   */
  private void setGenerateAnnotation() throws Exception {

    String comments = "\"vendor: %s %s\"";
    String vendorName = System.getProperty("java.vendor");
    String vendorVersion = System.getProperty("java.version");

    Map<String, Object> attributes = new TreeMap<>();
    attributes.put("value", String.format("\"%s\"", FinderProcessor.class.getCanonicalName()));
    attributes.put("date", String.format("\"%s\"", new Date().toString()));
    attributes.put("comments", String.format(comments, vendorName, vendorVersion));

    try {
      jw.emitAnnotation(Generated.class, attributes);
      jw.emitAnnotation(StaticMetamodel.class, annotatedClass.getClassName() + ".class");
    } catch (IOException e) {
      throw new Exception("SET_GENERATE_ANNOTATION");
    }

  }

}
