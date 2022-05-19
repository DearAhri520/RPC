package loadbalance;

import message.MessageBody;

import java.util.List;

/**
 * @author DearAhri520
 */
public class RandomLoadBalance implements LoadBalance{
    private List<>

    @Override
    public String getServer(MessageBody message) {
        return null;
    }

    @Override
    public void addServer(String connectString) {

    }

    @Override
    public void removeServer(String connectString) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }
}
