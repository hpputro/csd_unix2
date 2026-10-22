    public V g(K key) {

        
        if(l1_cache != null) {
            V val=l1_cache.g(key);
            if(val != null) {
                if(log.isTraceEnabled())
                    log.trace("returned value " + val + " for " + key + " from L1 cache");
                return val;
            }
        }

        
        Cache.Value<Value<V>> val=l2_cache.gEntry(key);
        Value<V> tmp;
        if(val != null) {
            tmp=val.gValue();
            if(tmp !=null) {
                V real_value=tmp.gVal();
                if(real_value != null && l1_cache != null && val.gTimeout() >= 0)
                    l1_cache.put(key, real_value, val.gTimeout());
                return tmp.gVal();
            }
        }

        
        try {
            RspList rsps=disp.callRemoteMethods(null,
                                                new MethodCall(GET, new Object[]{key}),
                                                GroupRequest.GET_ALL,
                                                call_timeout);
            for(Rsp rsp: rsps.values()) {
                Object obj=rsp.gValue();
                if(obj == null || obj instanceof Throwable)
                    continue;
                val=(Cache.Value<Value<V>>)rsp.gValue();
                if(val != null) {
                    tmp=val.gValue();
                    if(tmp != null) {
                        V real_value=tmp.gVal();
                        if(real_value != null && l1_cache != null && val.gTimeout() >= 0)
                            l1_cache.put(key, real_value, val.gTimeout());
                        return real_value;
                    }
                }
            }
            return null;
        }
        catch(Throwable t) {
            if(log.isWarnEnabled())
                log.warn("g() failed", t);
            return null;
        }
    }