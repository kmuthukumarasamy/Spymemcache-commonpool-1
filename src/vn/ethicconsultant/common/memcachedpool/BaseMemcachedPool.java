package vn.ethicconsultant.common.memcachedpool;


import net.spy.memcached.MemcachedClient;
import org.apache.commons.pool.ObjectPool;
// *  
// *  author  hungnguyen 
// *  Version  0.1 
// *  Since  JDK1.7

public interface BaseMemcachedPool {

    public MemcachedClient getConnection();
    public void returnCon(MemcachedClient socket);
    
    
    public MemcachedClient getConnection(ObjectPool pool);
    public void returnCon(ObjectPool pool, MemcachedClient socket);
    
}
