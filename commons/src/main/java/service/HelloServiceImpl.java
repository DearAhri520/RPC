package service;

import annotation.RpcAutowired;

/**
 * @author DearAhri520
 * @date 2022/3/28
 */
@RpcAutowired
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return name + ":sayHello";
    }
}