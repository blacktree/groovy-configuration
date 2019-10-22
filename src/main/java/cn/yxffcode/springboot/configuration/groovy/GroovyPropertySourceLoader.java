package cn.yxffcode.springboot.configuration.groovy;

import cn.yxffcode.springboot.configuration.config.GroovyPropertiesLoader;
import com.google.common.collect.Sets;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author gaohang
 */
public class GroovyPropertySourceLoader implements PropertySourceLoader {

  private static final String[] EXTENSIONS = {"groovy"};

  private final Set<String> loaded = Sets.newHashSet();

  @Override
  public String[] getFileExtensions() {
    return EXTENSIONS;
  }

  @Override
  public PropertySource<?> load(final String name, final Resource resource, final String profile) throws IOException {
    return createStringValueResolver((ClassPathResource) resource);
  }

  private PropertySource createStringValueResolver(final ClassPathResource resource) throws IOException {

    if (loaded.contains(resource.getPath()) || !resource.isReadable()) {
      return null;
    }

    final Properties properties = new Properties();

    try {
      final GroovyPropertiesLoader groovyPropertiesLoader = new GroovyPropertiesLoader(resource);

      final Map<String, Object> configs = groovyPropertiesLoader.load();
      properties.putAll(configs);

      GroovyConfigHolder.getInstance().registerConfigObject(groovyPropertiesLoader.getScriptObject());
      return new PropertiesPropertySource("groovy:" + resource.getPath(), properties);
    } finally {
      loaded.add(resource.getPath());
    }
  }
}
