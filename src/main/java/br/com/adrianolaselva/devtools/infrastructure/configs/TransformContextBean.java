package br.com.adrianolaselva.devtools.infrastructure.configs;


import br.com.adrianolaselva.devtools.domain.transform.enums.TransformType;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import br.com.adrianolaselva.devtools.infrastructure.transform.groovy.GroovyTransform;

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
