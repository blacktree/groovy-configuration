package cn.yxffcode.springboot.configuration.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.util.Map;

/**
 * @author gaohang
 */
public class GroovyPropertiesLoader {

  private static final ObjectPropertiesLoader objectPropertiesLoader = new ObjectPropertiesLoader();
  private final ClassPathResource resource;

  private final Object scriptObject;

  public GroovyPropertiesLoader(final ClassPathResource resource) throws IOException {
    if (resource == null || !resource.exists() || !resource.isReadable()) {
      throw new IllegalArgumentException("cannot read resource:" + resource);
    }
    this.resource = resource;
    this.scriptObject = newInstance(resource);
  }

  public Map<String, Object> load() throws IOException {
    return objectPropertiesLoader.load(newInstance(resource));
  }

  public Object getScriptObject() {
    return scriptObject;
  }

  private Object newInstance(final ClassPathResource scriptSourceLocator) throws IOException {
    final GroovyScriptFactory groovyScriptFactory = new GroovyScriptFactory(scriptSourceLocator.getPath());
    groovyScriptFactory.setBeanClassLoader(getClass().getClassLoader());

    final ResourceScriptSource resourceScriptSource = new ResourceScriptSource(scriptSourceLocator);
    return groovyScriptFactory.getScriptedObject(resourceScriptSource);
  }

}
