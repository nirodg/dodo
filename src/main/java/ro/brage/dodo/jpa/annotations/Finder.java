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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ro.brage.dodo.jpa.EntityService;

/**
 * Will generate for all annotated JPA entities a new Finder class which makes querying easier and
 * cleaner. The {@link EntityService} and the {@link Finder} are used for the generation of the
 * sources. <b>Entities with not annotated fields or using the <code>@Transient</code>'s annotation
 * will be ignored</b>
 * <hr>
 * Firstly you must annotate your entity with <code>@Finder</code>
 * 
 * <pre>
 * &#64;Entity
 * &#64;Table
 * &#64;Finder
 * public class Todo extends Model {
 * 
 *   &#64;Column
 *   String name;
 * 
 * 
 *   &#64;Column
 *   boolean enabled;
 * 
 * }
 * </pre>
 * 
 * <pre>
 * public Todo findByName(String name) throws Exception {
 *   Todo result = new TodoFinder(this)
 *       .name().equalsTo(name)
 *       .enabled().equalsTo(false)
 *       .createdOn().equalsTo(new Date())
 *       .getItem();
 * }
 * </pre>
 * 
 * @author Dorin Brage
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Finder {

}
