package test;

import server.RpcServer;

/**
 * @author DearAhri520
 */
public class ServerTest {
    public static void main(String[] args) {
        RpcServer server = new RpcServer();
        server.start();
    }
}
