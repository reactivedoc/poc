## Plug-in Design Note

* Class files should be understandable by JRE 17
* All classes need to be immutable/stateless
    * Its instance could be shared among multiple threads
    * Multiple instances could be created to run in parallel
* All methods need to be idempotent
    * Output could be cached and reused (memorized)
* All methods may be non-blocking
