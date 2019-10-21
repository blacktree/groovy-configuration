
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

