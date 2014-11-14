import vn.ethicconsultant.common.memcachedpool.MemcachedPool;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

/**
 *
 * @author hungnguyen
 */
public class Test {
    public static void main(String[] args) {
        String host = "192.168.10.225";
        int port = 11211;
        Config config = new GenericObjectPool.Config();
        config.maxActive = 10;
        config.maxIdle = config.maxActive;
        MemcachedPool memcachePool
                = MemcachedPool.getInstance(host, port, config);
        MemcachedClient connection = memcachePool.getConnection();
        connection.set("hung", 3600, "deptrai");
        connection.set("hung1", 3600, "deptrai");
        connection.set("hung2", 3600, "deptrai");
        Object get = connection.get("hung");
        Object get1 = connection.get("hung1");
        Object get2 = connection.get("hung2");
        System.out.println(get);
        System.out.println(get1);
        System.out.println(get2);
        memcachePool.returnCon(connection);
    }
}
