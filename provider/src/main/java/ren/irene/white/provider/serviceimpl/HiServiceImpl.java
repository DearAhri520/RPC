package ren.irene.white.provider.serviceimpl;

import annotation.RpcService;
import api.HiService;

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
