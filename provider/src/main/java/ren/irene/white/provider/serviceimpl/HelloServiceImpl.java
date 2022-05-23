package ren.irene.white.provider.serviceimpl;

import annotation.RpcService;
import api.HelloService;

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