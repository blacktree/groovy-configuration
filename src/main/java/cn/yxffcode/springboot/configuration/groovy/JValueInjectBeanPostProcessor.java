package cn.yxffcode.springboot.configuration.groovy;

import cn.yxffcode.springboot.configuration.utils.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author gaohang
 */
@Component
public class JValueInjectBeanPostProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
    final MutablePropertySources mutablePropertySources = GroovyConfigHolder.getInstance().getMutablePropertySources();
    if (mutablePropertySources == null) {
      return bean;
    }
    final List<Field> fields = Reflections.getFields(bean.getClass());
    if (fields == null || fields.isEmpty()) {
      return bean;
    }
    for (Field field : fields) {
      final JValue jvalue = field.getAnnotation(JValue.class);
      if (jvalue == null) {
        continue;
      }
      final String configKey = jvalue.value();
      for (PropertySource<?> propertySource : mutablePropertySources) {
        final Object property = propertySource.getProperty(configKey);
        if (property == null) {
          continue;
        }
        if (property instanceof List && field.getType().isArray()) {
          injectArray(bean, field, (List) property);
        } else {
          Reflections.setField(bean, field, property);
        }
        break;
      }
    }
    return bean;
  }

  private void injectArray(final Object bean, final Field field, final List property) {
    Object value;
    if (field.getType() != Object[].class) {
      final Object array = Array.newInstance(field.getType().getComponentType(), ((List<?>) property).size());
      ((List<?>) property).toArray((Object[]) array);
      value = array;
    } else {
      value = ((List<?>) property).toArray();
    }
    Reflections.setField(bean, field, value);
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
    return bean;
  }
}
