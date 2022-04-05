package service;

import annotation.RpcService;

/**
 * @author DearAhri520
 * @date 2022/3/28
 */
@RpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return name + ":sayHello";
    }
}