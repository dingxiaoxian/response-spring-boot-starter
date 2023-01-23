package tech.xiaoxian.response.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 小贤的统一返回值配置
 */
@ConfigurationProperties("xiaoxian.response")
public class UnifiedResponseProperties {
    /**
     * log配置
     */
    @NestedConfigurationProperty
    private UnifiedResponseLogProperties log;

    /**
     * 返回log
     *
     * @return log配置
     */
    public UnifiedResponseLogProperties getLog() {
        return log;
    }

    /**
     * 设置log
     *
     * @param log log配置
     */
    public void setLog(UnifiedResponseLogProperties log) {
        this.log = log;
    }

    /**
     * 统一返回值log配置类
     */
    public static class UnifiedResponseLogProperties {
        private boolean printError;

        /**
         * 是否打印异常
         *
         * @return 是否打印异常
         */
        public boolean isPrintError() {
            return printError;
        }

        /**
         * 设置是否打印异常
         *
         * @param printError 是否打印异常
         */
        public void setPrintError(boolean printError) {
            this.printError = printError;
        }
    }
}
