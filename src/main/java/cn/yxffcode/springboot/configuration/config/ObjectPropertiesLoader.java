package cn.yxffcode.springboot.configuration.config;

import cn.yxffcode.springboot.configuration.utils.Reflections;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;
import groovy.lang.GString;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author gaohang
 */
public class ObjectPropertiesLoader {

  public Map<String, Object> load(Object scriptedObject) {

    final Map<String, Object> properties = Maps.newHashMap();

    if (scriptedObject instanceof Map) {
      putToProperties(properties, (Map<?, ?>) scriptedObject);
    } else {
      final List<Field> fields = Reflections.getFields(scriptedObject.getClass());
      for (Field field : fields) {
        final Object value = Reflections.getField(field.getName(), scriptedObject);
        if (value == null) {
          //不支持null
          continue;
        }
        if (value instanceof Map) {
          putToProperties(properties, (Map<?, ?>) value);
          //同时将map也加入到properties中，用于支持JValue的注入
          properties.put(field.getName(), value);
        } else if (value instanceof String || value instanceof GString) {
          properties.put(field.getName(), String.valueOf(value));
        } else if (Primitives.allPrimitiveTypes().contains(value.getClass())
            || Primitives.allWrapperTypes().contains(value.getClass())) {
          properties.put(field.getName(), value);
        } else if (value instanceof Iterable
            || value.getClass().isArray()) {
          properties.put(field.getName(), value);
        }
      }
    }
    return properties;
  }

  private void putToProperties(final Map<String, Object> properties, final Map<?, ?> values) {
    if (values == null || values.isEmpty()) {
      return;
    }
    for (Map.Entry<?, ?> en : values.entrySet()) {
      if (en.getValue() instanceof GString) {
        properties.put(String.valueOf(en.getKey()), String.valueOf(en.getValue()));
      } else {
        properties.put(String.valueOf(en.getKey()), en.getValue());
      }
    }
  }

}
