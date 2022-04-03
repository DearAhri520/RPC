package service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * @author DearAhri520
 * @date 2022/3/28
 */
public class ServicesFactory {
    private static HashMap<Class<?>, Object> map = new HashMap<>(16);

    public ServicesFactory() {

    }

    public static Object getInstance(Class<?> interfaceClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        /*根据Class创建实例*/
        try {
            Class<?> clazz = Class.forName("service.HelloService");
            Object instance = Class.forName("service.HelloServiceImpl").getDeclaredConstructor().newInstance();

            /*放入 InterfaceClass -> InstanceObject 的映射*/
            map.put(clazz, instance);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return map.get(interfaceClass);
    }
}
