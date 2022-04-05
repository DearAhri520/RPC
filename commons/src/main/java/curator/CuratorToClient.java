package curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.HashMap;

/**
 * @author DearAhri520
 */
public class CuratorToClient extends CuratorClient {
    public void listen(PathChildrenCacheListener cacheListener) {
        CuratorCache cache = CuratorCache.builder(curatorClient, "/services").build();
        CuratorCacheListener listener = CuratorCacheListener.
                builder().
                forPathChildrenCache("/services", curatorClient, cacheListener).
                build();
        cache.listenable().addListener(listener);
        cache.start();
    }
}