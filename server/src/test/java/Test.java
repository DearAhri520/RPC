import annotation.RpcAutowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DearAhri520
 * @date 2022/3/30
 */
public class Test {
    public static void main(String[] args) throws Exception {
        HashMap<String, Object> servicesMap = new HashMap<>();
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Map<String, Object> map = context.getBeansWithAnnotation(RpcAutowired.class);
        for (Object o : map.values()) {
            servicesMap.put(o.getClass().getInterfaces()[0].getName(), o);
        }
    }
}