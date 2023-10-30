package com.devtools.domain.transform.interfaces;


import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.List;

public interface Transform {

    String apply(final String payload) throws ScriptException, NoSuchMethodException;

    List<String> apply(final List<String> payload) throws ScriptException, NoSuchMethodException;

    Transform setFilePath(final String filePath) throws FileNotFoundException, ScriptException;

    Transform setMethodName(final String name);
}
