package com.github.windsekirun.autoasync.processor;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;

/**
 * Created by pyxis on 18. 4. 25.
 */

public class ContributesHolder {
    public Element element;
    public ClassName classNameComplete;
    public String className;

    public ContributesHolder(Element element, ClassName classNameComplete, String className) {
        this.element = element;
        this.classNameComplete = classNameComplete;
        this.className = className;
    }

}
