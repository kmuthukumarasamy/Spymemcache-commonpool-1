package vn.ethicconsultant.common.memcachedpool;

import java.util.HashMap;
import java.util.Map;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungnguyen
 * @version 0.1
 * @since JDK1.7
 */
public class GenericConnectionProvider implements ConnectionProvider {

    public static final Logger logger = LoggerFactory
            .getLogger(GenericConnectionProvider.class);
    private int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;
    private int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;
    private int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;
    private long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;
    private boolean testOnBorrow = GenericObjectPool.DEFAULT_TEST_ON_BORROW;
    private boolean testOnReturn = GenericObjectPool.DEFAULT_TEST_ON_RETURN;
    private boolean testWhileIdle = GenericObjectPool.DEFAULT_TEST_WHILE_IDLE;
    private ObjectPool objectPool = null;
    private volatile static GenericConnectionProvider instance = null;

    public static GenericConnectionProvider getInstance(String host, int port,
            GenericObjectPool.Config config) {
        if (instance == null) {
            synchronized (GenericConnectionProvider.class) {
                if (instance == null) {
                    instance = new GenericConnectionProvider(host, port, config);
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

    public GenericConnectionProvider(String host, int port, GenericObjectPool.Config poolConfig) {
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

    public GenericConnectionProvider(String host, int port, String bHost, int bPort, int maxPoolSize) {
        objectPool = setPoolProperties(objectPool, host, port, maxPoolSize);
    }

    private ObjectPool setPoolProperties(ObjectPool pool, String host, int port, int poolSize) {
        pool = new GenericObjectPool();
        System.out.println("Initialize connection pool to server: " + host + " Port: " + port);
        ((GenericObjectPool) pool).setMaxIdle(poolSize);
        ((GenericObjectPool) pool).setMinIdle(poolSize);
        ((GenericObjectPool) pool).setMaxWait(maxWait);
        ((GenericObjectPool) pool).setTestOnBorrow(testOnBorrow);
        ((GenericObjectPool) pool).setTestOnReturn(testOnReturn);
        ((GenericObjectPool) pool).setTestWhileIdle(testWhileIdle);
        ((GenericObjectPool) pool).setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        MemcachePoolableObjectFactory memcachePoolableObjectFactory = new MemcachePoolableObjectFactory(host, port);
        pool.setFactory(memcachePoolableObjectFactory);
        return pool;
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

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public ObjectPool getObjectPool() {
        return objectPool;
    }

    public void setObjectPool(ObjectPool objectPool) {
        this.objectPool = objectPool;
    }

}
