/*******************************************************************************
 * Copyright 2018 Dorin Brage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/

package com.brage.dodo.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Dorin Brage
 */
public class Translation {

    private ResourceBundle bundle;
    protected String baseName;
    protected Locale locale;

    private final static String SUFIX = "[]";

    public Translation(String baseName, Locale locale) {
        this.bundle = ResourceBundle.getBundle(baseName, locale);
        this.baseName = baseName;
        this.locale = locale;
    }

    public String getTranslation(String value) {
        if (bundle.containsKey(value)) {
            return bundle.getString(value);
        }
        return value + SUFIX;
    }

    public String getTranslation(Enum<?> key) {
        String value = key.getClass().getSimpleName() + "." + key.toString();
        return getTranslation(value);
    }

}
