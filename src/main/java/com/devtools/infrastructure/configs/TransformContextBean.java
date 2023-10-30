package com.devtools.infrastructure.configs;


import com.devtools.domain.transform.enums.TransformType;
import com.devtools.domain.transform.interfaces.Transform;
import com.devtools.infrastructure.transform.groovy.GroovyTransform;

public final class TransformContextBean {

    private TransformContextBean() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static Transform getInstance(final TransformType transformType) {
        return switch (transformType) {
            case GROOVY -> new GroovyTransform();
        };
    }
}
