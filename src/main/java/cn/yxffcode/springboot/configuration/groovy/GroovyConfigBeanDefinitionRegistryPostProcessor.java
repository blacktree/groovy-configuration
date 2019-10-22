package cn.yxffcode.springboot.configuration.groovy;

import cn.yxffcode.springboot.configuration.utils.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 支持在application.groovy或application-xxx.groovy中通过{@link Bean}注解配置bean，但只支持简单的bean，支持的bean
 * 需要能够通过其所在的groovy文件中的groovy代码完整的创建出来，不依赖外部的bean，否则需要使用
 * {@link org.springframework.context.annotation.Configuration}等方式
 * <p>
 * 支持的bean要求：
 * <ul>
 * <li>1.没有initMethod和destroyMethod</li>
 * <li>1.仅依赖所在的groovy配置文件就可以自己手动创建出来，不依赖其它外部的bean</li>
 * </ul>
 *
 * @author gaohang
 */
@Component
public class GroovyConfigBeanDefinitionRegistryPostProcessor implements BeanFactoryPostProcessor {

  @Override
  public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
    final List<Object> configObjects = GroovyConfigHolder.getInstance().getConfigObjects();
    try {
      for (Object configObject : configObjects) {
        //检查是否有@Bean标记的方法
        final List<Method> methods = Reflections.getMethods(configObject.getClass());
        if (CollectionUtils.isEmpty(methods)) {
          continue;
        }
        for (Method method : methods) {
          final Bean bean = method.getAnnotation(Bean.class);
          if (bean == null) {
            continue;
          }
          final Object instance = method.invoke(configObject);
          final String[] values = bean.value();
          final String[] names = bean.name();
          if (values.length == 0 && names.length == 0) {
            //没有bean name，则生成默认的
            beanFactory.registerSingleton(instance.getClass().getCanonicalName()
                + '#' + configObject.getClass().getCanonicalName() + '#' + method.getName(), instance);
          }
          for (String value : values) {
            beanFactory.registerSingleton(value, instance);
          }
          for (String name : names) {
            beanFactory.registerSingleton(name, instance);
          }
        }
      }
    } catch (Exception e) {
      throw new BeanCreationException("cannot create bean, only simple bean config is supported by groovy, " +
          "you may need to use @Configuration to config the bean", e);
    }
  }
}
