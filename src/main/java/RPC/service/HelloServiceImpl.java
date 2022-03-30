package RPC.service;

/**
 * @author DearAhri520
 * @date 2022/3/28
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return name + ":sayHello";
    }
}
