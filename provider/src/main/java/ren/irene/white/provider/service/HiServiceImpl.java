package ren.irene.white.provider.service;

import annotation.RpcService;
import provider.HiService;

/**
 * @author DearAhri520
 */
@RpcService
public class HiServiceImpl implements HiService {
    @Override
    public String sayHi(String name) {
        return name + ":sayHi";
    }
}
