package vn.ethicconsultant.common.memcachedpool;


import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungnguyen
 * @version 0.1
 * @since JDK1.7
 */
public class MemcachePoolableObjectFactory implements PoolableObjectFactory {

    public static final Logger logger = LoggerFactory
            .getLogger(MemcachePoolableObjectFactory.class);
    private String serviceIP;
    private int servicePort;
    private int timeOut;
    private final String serverAddress;

    /**
     *
     * @param serviceIP
     * @param servicePort
     */
    public MemcachePoolableObjectFactory(String serviceIP, int servicePort) {
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
        this.serverAddress = String.format("%s:%s", serviceIP, servicePort);
    }

    @Override
    public void destroyObject(Object arg0) throws Exception {
        if (arg0 instanceof MemcachedClient) {
            MemcachedClient socket = (MemcachedClient) arg0;
            if (socket.getAvailableServers() != null) {
                socket.shutdown();
            }
        }
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Override
    public Object makeObject() throws Exception {
        try {
            MemcachedClient mc = new MemcachedClient(new ConnectionFactoryBuilder(
                    new BinaryConnectionFactory())
                    .setDaemon(true)
                    .setFailureMode(FailureMode.Retry)
                    .setOpQueueMaxBlockTime(DefaultConnectionFactory.DEFAULT_OP_QUEUE_MAX_BLOCK_TIME/2)
                    .setOpTimeout(DefaultConnectionFactory.DEFAULT_OPERATION_TIMEOUT*2)
                    .build(),
                    AddrUtil.getAddresses(this.serverAddress));
            return mc;
        } catch (Exception e) {
            logger.error("error MemcachePoolableObjectFactory()", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean validateObject(Object arg0) {
        try {
            if (arg0 instanceof MemcachedClient) {
                MemcachedClient mc = (MemcachedClient) arg0;
                if (mc.getAvailableServers() != null) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void passivateObject(Object arg0) throws Exception {
        // DO NOTHING
    }

    @Override
    public void activateObject(Object arg0) throws Exception {
        // DO NOTHING
    }

    public String getServiceIP() {
        return serviceIP;
    }

    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
