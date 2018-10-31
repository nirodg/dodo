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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

/**
 * The method builder
 * 
 * 
 * <pre>
 * new MethodBuilder("boolean", "myMethod")
 *      .addStatements("boolean isReady currentField = %s", checkIfReady())
 *      .addStatements("return isReady"));
 * </pre>
 * 
 * To write it - <b>This is not implemented</b>
 * 
 * <pre>
 * 
 * for (MethodBuilder method : methods) {
 *   jw.beginMethod(method.getTypeMethod(), method.getName(), method.getModifiers(), null, null);
 * 
 *   for (String statement : method.getStatements()) {
 *     if (statement.equals(MethodBuilder.EMPTY_LINE)) {
 *       jw.emitEmptyLine();
 *     } else {
 *       jw.emitStatement(statement, (Object[]) null);
 *     }
 *   }
 * 
 *   jw.endMethod();
 *   jw.emitEmptyLine();
 * }
 * </pre>
 *
 * @author Dorin Brage
 *
 */
public class MethodBuilder {

  private final static String DEFAULT_TYPE = "void";

  static final String EMPTY_LINE = System.getProperty("line.separator");

  private String typeMethod;

  private String name;

  private Set<Modifier> modifiers;

  private List<String> statements;

  public MethodBuilder(Set<Modifier> modifiers) {
    super();
    this.modifiers = modifiers;
    this.statements = new ArrayList<>();
  }

  public MethodBuilder(String name) {
    super();
    this.typeMethod = DEFAULT_TYPE;
    this.name = name;
    this.modifiers = Utils.getCustomModifier(Modifier.PUBLIC);
    this.statements = new ArrayList<>();
  }

  public MethodBuilder(String typeMethod, String name) {
    super();
    this.typeMethod = typeMethod;
    this.name = name;
    this.modifiers = Utils.getCustomModifier(Modifier.PUBLIC);
    this.statements = new ArrayList<>();
  }

  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  public void setModifiers(Set<Modifier> modifiers) {
    this.modifiers = modifiers;
  }

  public String getTypeMethod() {
    return typeMethod;
  }

  public void setTypeMethod(String typeMethod) {
    this.typeMethod = typeMethod;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getStatements() {
    return statements;
  }

  public MethodBuilder addModifiers(Modifier... args) {
    for (Modifier arg : args) {
      modifiers.add(arg);
    }
    return this;
  }

  public void setStatements(List<String> statements) {
    this.statements = statements;
  }

  public MethodBuilder addStatements(String format, Object... args) {
    statements.add(String.format(format, args).toString());
    return this;
  }

  public MethodBuilder addEmptyLine() {
    statements.add(EMPTY_LINE);
    return this;
  }

  public static final String getEmptyLine() {
    return EMPTY_LINE;
  }
}
