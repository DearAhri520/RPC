package loadbalance;

import message.MessageBody;
import servers.ServerInfo;

import java.util.List;
import java.util.Random;

/**
 * @author DearAhri520
 *
 * 随机负载均衡
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public ServerInfo getServer(List<ServerInfo> addresses, MessageBody messageBody) {
        int size = addresses.size();
        return addresses.get(new Random().nextInt(size));
    }
}
