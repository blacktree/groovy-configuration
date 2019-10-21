package cn.yxffcode.springboot.configuration.groovy;

import com.google.common.collect.Sets;
import groovy.lang.GString;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * springboot 支持groovy配置
 *
 * @author gaohang
 */
public class GroovyPropertySourceLoader implements PropertySourceLoader {

  private static final String[] STRINGS = {"groovy"};

  private final Set<String> loaded = Sets.newHashSet();

  @Override
  public String[] getFileExtensions() {
    return STRINGS;
  }

  @Override
  public PropertySource<?> load(final String name, final Resource resource, final String profile) throws IOException {
    return createStringValueResolver((ClassPathResource) resource);
  }

  private PropertySource createStringValueResolver(final ClassPathResource resource) throws IOException {

    if (loaded.contains(resource.getPath())) {
      return null;
    }

    final Properties properties = new Properties();

    try {
      final Object scriptedObject = getGroovyConfigObject(resource);

      if (scriptedObject instanceof Map) {
        putToProperties(properties, (Map<?, ?>) scriptedObject);
      } else {
        final List<Field> fields = Reflections.getFields(scriptedObject.getClass());
        for (Field field : fields) {
          final Object value = Reflections.getField(field.getName(), scriptedObject);
          if (value instanceof Map) {
            putToProperties(properties, (Map<?, ?>) value);
          } else if (value instanceof String || value instanceof GString) {
            properties.put(field.getName(), String.valueOf(value));
          }
        }
      }
      return new PropertiesPropertySource("groovy:" + resource.getPath(), properties);
    } finally {
      loaded.add(resource.getPath());
    }
  }

  private void putToProperties(final Properties properties, final Map<?, ?> values) {
    if (CollectionUtils.isEmpty(values)) {
      return;
    }
    for (Map.Entry<?, ?> en : values.entrySet()) {
      properties.put(String.valueOf(en.getKey()), String.valueOf(en.getValue()));
    }
  }

  private Object getGroovyConfigObject(final ClassPathResource scriptSourceLocator) throws IOException {
    final GroovyScriptFactory groovyScriptFactory = new GroovyScriptFactory(scriptSourceLocator.getPath());
    groovyScriptFactory.setBeanClassLoader(getClass().getClassLoader());

    final ResourceScriptSource resourceScriptSource = new ResourceScriptSource(scriptSourceLocator);
    return groovyScriptFactory.getScriptedObject(resourceScriptSource);
  }

}
