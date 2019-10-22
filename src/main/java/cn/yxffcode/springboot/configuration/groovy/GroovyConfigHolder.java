package cn.yxffcode.springboot.configuration.groovy;

import com.google.common.collect.Lists;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collections;
import java.util.List;

/**
 * @author gaohang
 */
final class GroovyConfigHolder {

  private static final class Holder {
    private static GroovyConfigHolder ourInstance = new GroovyConfigHolder();
  }

  static GroovyConfigHolder getInstance() {
    return Holder.ourInstance;
  }

  private MutablePropertySources mutablePropertySources;

  private final List<Object> configObjects = Lists.newArrayList();

  private GroovyConfigHolder() {
  }

  void bind(MutablePropertySources mutablePropertySources) {
    this.mutablePropertySources = mutablePropertySources;
  }

  void registerConfigObject(final Object configObject) {
    if (configObject == null) {
      return;
    }
    configObjects.add(configObject);
  }

  List<Object> getConfigObjects() {
    return Collections.unmodifiableList(configObjects);
  }

  MutablePropertySources getMutablePropertySources() {
    return mutablePropertySources;
  }
}
