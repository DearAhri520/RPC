package loadbalance;

import lombok.extern.slf4j.Slf4j;
import message.MessageBody;
import serviceinfo.ServiceInfo;

import java.util.List;
import java.util.Random;

/**
 * @author DearAhri520
 *
 * 随机负载均衡
 */
@Slf4j
public class RandomLoadBalance implements LoadBalance {
    public RandomLoadBalance() {
        log.info("加载RandomLoadBalance类");
    }

    @Override
    public ServiceInfo getServer(List<ServiceInfo> addresses, MessageBody messageBody) {
        int size = addresses.size();
        return addresses.get(new Random().nextInt(size));
    }
}
