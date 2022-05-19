package ren.irene.white.provider.service;

import annotation.RpcService;
import provider.HelloService;

/**
 * @author DearAhri520
 */
@RpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return name + ":sayHello";
    }
}