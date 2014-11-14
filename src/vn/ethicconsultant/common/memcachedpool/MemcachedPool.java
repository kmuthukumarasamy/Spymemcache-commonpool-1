package vn.ethicconsultant.common.memcachedpool;

import java.util.HashMap;
import java.util.Map;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 *
 * @author hungnguyen
 * @version 0.1
 * @since JDK1.7
 */
public class MemcachedPool implements BaseMemcachedPool {

    public static final Logger logger = LoggerFactory
            .getLogger(MemcachedPool.class);
    private ObjectPool objectPool = null;
    private volatile static MemcachedPool instance = null;

    public static MemcachedPool getInstance(String host, int port,
            GenericObjectPool.Config config) {
        if (instance == null) {
            synchronized (MemcachedPool.class) {
                if (instance == null) {
                    instance = new MemcachedPool(host, port, config);
                    HashMap<Integer, MemcachedClient> clientMap = new HashMap<>();
                    for (int i = 0; i < config.maxActive; i++) {
                        MemcachedClient mc = instance.getConnection();
                        clientMap.put(i, mc);
                        mc.get("STRINGTOINITIALIZE");
                    }
                    for (Map.Entry<Integer, MemcachedClient> entrySet : clientMap.entrySet()) {
                        Integer key = entrySet.getKey();
                        instance.returnCon(clientMap.get(key));
                    }
                }
            }
        }
        return instance;
    }

    public MemcachedPool(String host, int port, GenericObjectPool.Config poolConfig) {
        objectPool = new GenericObjectPool();
        System.out.println("Initialize connection pool to server: " + host + " Port: " + port);
        ((GenericObjectPool) objectPool).setMaxActive(poolConfig.maxActive);
        ((GenericObjectPool) objectPool).setMaxIdle(poolConfig.maxIdle);
        ((GenericObjectPool) objectPool).setMinIdle(poolConfig.minIdle);
        ((GenericObjectPool) objectPool).setMaxWait(poolConfig.maxWait);
        ((GenericObjectPool) objectPool).setTestOnBorrow(poolConfig.testOnBorrow);
        ((GenericObjectPool) objectPool).setTestOnReturn(poolConfig.testOnReturn);
        ((GenericObjectPool) objectPool).setTestWhileIdle(poolConfig.testWhileIdle);
        ((GenericObjectPool) objectPool).setWhenExhaustedAction(poolConfig.whenExhaustedAction);
        MemcachePoolableObjectFactory memcachePoolableObjectFactory = new MemcachePoolableObjectFactory(host, port);
        objectPool.setFactory(memcachePoolableObjectFactory);
    }

    private void destroy(ObjectPool pool) {
        try {
            pool.close();
        } catch (Exception e) {
            throw new RuntimeException("error destroy()", e);
        }
    }

    @Override
    public MemcachedClient getConnection() {
        try {

            return (MemcachedClient) objectPool.borrowObject();
        } catch (Exception e) {
            instance = null;
            System.out.println("error getConnection()" + e.toString());
            return null;
        }
    }

    @Override
    public MemcachedClient getConnection(ObjectPool pool) {
        try {
            return (MemcachedClient) pool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException("error getConnection()", e);
        }

    }

    @Override
    public void returnCon(ObjectPool pool, MemcachedClient socket) {
        try {
            pool.returnObject(socket);
        } catch (Exception e) {
            throw new RuntimeException("error returnCon()", e);
        }
    }

    @Override
    public void returnCon(MemcachedClient socket) {
        try {
            objectPool.returnObject(socket);
        } catch (Exception e) {
            throw new RuntimeException("error returnCon()", e);
        }
    }


    public ObjectPool getObjectPool() {
        return objectPool;
    }

    public void setObjectPool(ObjectPool objectPool) {
        this.objectPool = objectPool;
    }

}
