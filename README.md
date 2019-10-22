# groovy-configuration
通过groovy配置springboot

实现方式见：https://zhuanlan.zhihu.com/p/87808920

## 使用
* 使用application.groovy替代application.properties或application.yml
* groovy配置与properties/yml实际上是可以共存的
* 可使用非string类型的配置，可使用复杂对象作为配置的值，但是需要使用@JValue做注入

## Groovy配置
创建application.groovy或者application-xxx.groovy，配置中使用map表示kv对，
可以认为map则仅仅用于分组，是对可读性的增强，map中的key与properties文件
中的key完全相同，意义也一样，内容类似如下：

```groovy
/**
 * 类名随意，配置解析时不会使用具体的类名
 */
class Config {

    /**
     * string/gstring类型的属性也会当作一个property
     */
    def appName = "groovy-config-test"

    /**
     * 定义map，一个map持有一类配置
     *
     * 这个map在解析配置时会忽略，仅用于组织配置，因此定义map的变量名没有硬性规定，取有业务意义的即可
     */
    def activity = [
            //这里是profile，和yml或者properties中的配置意义完全相同
            "spring.profiles.active": "env"
    ]

    def jacksonPrefix = "spring.jackson"
    def springApplication = [
            "spring.application.name"     : appName,

            //可使用groovy的任意语法
            "${jacksonPrefix}.date-format": "yyyy-MM-dd HH:mm:ss",
            "${jacksonPrefix}.time-zone"  : "GMT+8"

    ]

    //下面加入其它配置
}
```

如果只需要用一个Map表示所有配置，可以不定义类，直接定义Map即可：
```groovy
/**
 * 如果只使用一个map表示配置，则不需要定义groovy类
 */
def configs = [
        "zkServer": "127.0.0.1:8888"
]
```

## 使用@JValue注入属性值
既然支持了groovy配置，而groovy是代码，具有很大的灵活性，我们不必非要使用String，groovy可支持任意的对象，
那么我提供了@JValue注解，用于对属性做Java对象值的注入，而被注入的对象则来源于groovy配置，例如在groovy中有如下配置：

```groovy
class Config {

    /**
     * mybatis相关
     */
    def mybatis = [
            "mybatis.interceptors": [
                    new BatchExecutorInterceptor(),
                    new MacroInterceptor(),
                    new ListParameterResolver(),
                    new DefaultResultMapInterceptor()
                    //其它interceptor
            ]
    ]

}
```

在groovy配置中定义了mybatis拦截器，在初始化mybatis时可作为配置注入：
```java
@Configuration
public class MybatisConfig {
  
  /**
   *  注入Mybatis的拦截器，当groovy中配置的是list时，java字段类型也可以是List也可以是数组
   */
  @JValue("mybatis.interceptors")
  private Interceptor[] interceptors;

  @Bean
  public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {

    final SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource);
    sqlSessionFactoryBean.setPlugins(interceptors);
    return sqlSessionFactoryBean;
  }

  @Bean
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

}

```

## @Bean的初步支持
可在groovy配置中加入标有@Bean的方法，会将此bean作为一个单例对象注册到spring中，
但是此处的bean只支持简单的注册，不支持复杂的创建，这里的bean只能依赖groovy配置
中的内容，不能依赖工程中的bean，当前的实现中不支持，groovy在这里的定位只是属性配置，
不是用来配置bean的，如果需要定义复杂的bean需要使用springboot支持的方式：
```groovy
class Config {

    /**
     * 不要写复杂的bean创建，这里不支持注入 
     */
    @Bean
    def batchExecutorInterceptor() {
        return new BatchExecutorInterceptor();
    }
}
```