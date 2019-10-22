package cn.yxffcode.springboot.configuration.utils;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author gaohang on 15/12/4.
 */
public final class Reflections {
  private Reflections() {
  }

  private static Field findField(Class<?> clazz, String name) {
    return findField(clazz, name, null);
  }

  public static Field findField(Class<?> clazz, String name, Class<?> type) {
    Class<?> searchType = clazz;
    while (!Object.class.equals(searchType) && searchType != null) {
      Field[] fields = searchType.getDeclaredFields();
      for (Field field : fields) {
        if ((name == null || name.equals(field.getName())) && (type == null || type
            .equals(field.getType()))) {
          return field;
        }
      }
      searchType = searchType.getSuperclass();
    }
    return null;
  }

  public static List<Field> getFields(Class<?> clazz) {
    final List<Field> fields = Lists.newArrayList();
    Class<?> type = clazz;
    while (type != Object.class) {
      final Field[] declaredFields = type.getDeclaredFields();
      fields.addAll(Arrays.asList(declaredFields));
      type = type.getSuperclass();
    }
    return fields;
  }

  public static Object getField(String fieldName, Object target) {
    Field field = findField(target.getClass(), fieldName);
    if (!field.isAccessible()) {
      field.setAccessible(true);
    }
    try {
      return field.get(target);
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass()
          .getName() + ": " + ex.getMessage(), ex);
    }
  }

  public static void setField(Object target, Field field, Object value) {
    try {
      if (!field.isAccessible()) {
        field.setAccessible(true);
      }
      field.set(target, value);
    } catch (Exception ex) {
      throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass()
          .getName() + ": " + ex.getMessage(), ex);
    }
  }

  public static List<Method> getMethods(Class<?> clazz) {
    final List<Method> methods = Lists.newArrayList();
    Class<?> type = clazz;
    while (type != Object.class) {
      final Method[] declaredMethods = type.getDeclaredMethods();
      if (declaredMethods != null) {
        methods.addAll(Arrays.asList(declaredMethods));
      }
      type = type.getSuperclass();
    }
    return methods;
  }
}