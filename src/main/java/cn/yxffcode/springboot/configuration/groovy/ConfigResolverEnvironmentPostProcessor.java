package cn.yxffcode.springboot.configuration.groovy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

/**
 * 获取配置
 *
 * @author gaohang
 */
public class ConfigResolverEnvironmentPostProcessor implements EnvironmentPostProcessor {
  @Override
  public void postProcessEnvironment(final ConfigurableEnvironment environment, final SpringApplication application) {
    final MutablePropertySources propertySources = environment.getPropertySources();
    GroovyConfigHolder.getInstance().bind(propertySources);
  }
}
