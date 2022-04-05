package service;

import annotation.RpcService;

/**
 * @author DearAhri520
 * @date 2022/4/4
 */
@RpcService
public class HiServiceImpl implements HiService {
    @Override
    public String sayHi(String name) {
        return name + ":sayHi";
    }
}
