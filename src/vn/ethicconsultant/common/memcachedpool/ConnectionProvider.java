package vn.ethicconsultant.common.memcachedpool;


import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedConnection;
import org.apache.commons.pool.ObjectPool;
// *  
// *  author  hungnguyen 
// *  Version  0.1 
// *  Since  JDK1.7

public interface ConnectionProvider {

    // use plain TSocket
//    public TSocket getConnection();
//    public void returnCon(TSocket socket);

    // use Frame along with socket
    public MemcachedClient getConnection();
    public void returnCon(MemcachedClient socket);
    
    
    public MemcachedClient getConnection(ObjectPool pool);
    public void returnCon(ObjectPool pool, MemcachedClient socket);
    
}
