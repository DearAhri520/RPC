package processor;

import annotation.RpcAutowired;
import properties.RpcClientProperties;
import discovery.ServiceDiscover;
import loadbalance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import proxy.ClientStubProxyFactory;

/**
 * @author DearAhri520
 */
@Slf4j
public class RpcClientProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ClientStubProxyFactory clientStubProxyFactory;

    private ServiceDiscover serviceDiscover;

    private RpcClientProperties properties;

    private ApplicationContext applicationContext;

    private LoadBalance loadBalance;

    public RpcClientProcessor(ClientStubProxyFactory clientStubProxyFactory, ServiceDiscover serviceDiscover, LoadBalance loadBalance, RpcClientProperties properties) {
        log.info("加载RpcClientProcessor类");
        this.clientStubProxyFactory = clientStubProxyFactory;
        this.serviceDiscover = serviceDiscover;
        this.properties = properties;
        this.loadBalance = loadBalance;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.getClass().getClassLoader());
                ReflectionUtils.doWithFields(clazz, field -> {
                    RpcAutowired rpcAutowired = AnnotationUtils.getAnnotation(field, RpcAutowired.class);
                    if (rpcAutowired != null) {
                        Object bean = applicationContext.getBean(clazz);
                        field.setAccessible(true);
                        /*修改为代理对象*/
                        ReflectionUtils.setField(field, bean, clientStubProxyFactory.getProxy(field.getType(), properties, serviceDiscover, loadBalance));
                        log.info("代理对象:{} 被修改",field);
                    }
                });
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}