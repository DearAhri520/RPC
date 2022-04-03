package client;

import java.util.HashMap;

/**
 * @author DearAhri520
 * 可用的服务器列表
 */
public class ServerList {
    /**
     * 服务名->可用服务提供者列表
     */
    private HashMap<String, ServerProviders> map = new HashMap();

    /**
     * 根据服务名 , 获取一个可用的服务器IP与端口
     *
     * @param service 服务名
     * @return IP:PORT
     */
    public String getProvider(String service) {
        return null;
    }
}
