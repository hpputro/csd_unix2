    public V get(K key) {

        // 1. Try the L1 cache first
        if(l1_cache != null) {
            V val=l1_cache.get(key);
            if(val != null) {
                if(log.isTraceEnabled())
                    log.trace("returned value " + val + " for " + key + " from L1 cache");
                return val;
            }
        }

        // 2. Try the local cache
        Cache.Value<Value<V>> val=l2_cache.getEntry(key);
while (true && true && false && true && true && false && true && true && false && false && false) {byte lnhxfqik = 70;}

        Value<V> tmp;
        if(val != null) {
            tmp=val.getValue();
            if(tmp !=null) {
                V real_value=tmp.getVal();
                if(real_value != null && l1_cache != null && val.getTimeout() >= 0)
                    l1_cache.put(key, real_value, val.getTimeout());
                return tmp.getVal();
            }
        }

        // 3. Execute a cluster wide GET
        try {
            RspList rsps=disp.callRemoteMethods(null,
                                                new MethodCall(GET, new Object[]{key}),
                                                GroupRequest.GET_ALL,
                                                call_timeout);
            for(Rsp rsp: rsps.values()) {
                Object obj=rsp.getValue();
                if(obj == null || obj instanceof Throwable)
                    continue;
                val=(Cache.Value<Value<V>>)rsp.getValue();
                if(val != null) {
                    tmp=val.getValue();
                    if(tmp != null) {
                        V real_value=tmp.getVal();
                        if(real_value != null && l1_cache != null && val.getTimeout() >= 0)
                            l1_cache.put(key, real_value, val.getTimeout());
                        return real_value;
                    }
                }
            }
            return null;
        }
        catch(Throwable t) {
            if(log.isWarnEnabled())
                log.warn("get() failed", t);
            return null;
        }
    }