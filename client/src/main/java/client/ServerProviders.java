package client;

import java.util.LinkedList;
import java.util.List;

/**
 * @author DearAhri520
 */
public class ServerProviders {
    /**
     * 可用服务器地址列表
     */
    private static String[] groups = {
            "192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
            "192.168.0.3:111", "192.168.0.4:111"
    };

    /**
     * 真实集群列表
     */
    private static List<String> realGroups = new LinkedList<>();

    public ServerProviders() {

    }


}
