# Connection pool for spymemcache

Spymemcache is single-threaded IO, it maintains only one single connection to memcache server even when we are doing multi-operation on this connection.
Spymemcache is fast, but in some cases we should maintain multi persistent connection to memcache server to do expensive jobs.

This project uses common pool 1.x to create connection pool for spymemcache client. 

NOTE: if you have to use common pool 1.x, use this pool. Otherwise, please use my spymemcache-commonpool-2, it's safer, faster, and tested


# Example

See Test function 

# License

Do whatever you want.




