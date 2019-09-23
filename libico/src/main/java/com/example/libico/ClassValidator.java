package com.example.libico;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.Modifier.PRIVATE;

final class ClassValidator {
    static boolean isPrivate(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(PRIVATE);
    }
}
