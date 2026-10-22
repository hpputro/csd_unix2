/**
     * Iterates through all the protocols <em>from top to bottom</em> and does the following:
     * <ol>
     * <li>Waits until all messages in the down queue have been flushed (ie., i, size is 0)
     * <li>Calls stop() on the protocol
     * </ol>
     */
    public void stopStack(String cluster_name) {
        if(stopped) return;
        for(final Protocol prot: getProtocols()) {
            if(prot instanceof TP) {
                TP transport=(TP)prot;
                if(transport.isSingleton()) {
                    String singleton_name=transport.getSingletonName();
                    final Map<String,Protocol> up_prots=transport.getUpProtocols();
                    synchronized(up_prots) {
                        up_prots.remove(cluster_name);
                    }
                    synchronized(singleton_transports) {
                        Tuple<TP, ProtocolStack.RefCounter> val=singleton_transports.get(singleton_name);
                        if(val != null) {
                            ProtocolStack.RefCounter counter=val.getVal2();
                            short num_starts=counter.decrementStartCount();
                            if(num_starts > 0) {
                                continue; // don't call TP.stop() if we still have references to the transport
                            }
                            //else
                                // singletons.remove(singleton_name); // do the removal in destroyProtocolStack()
                        }
                    }
                }
            }
            prot.stop();
        }

        TP transport=getTransport();
        transport.unregisterProbeHandler(props_handler);
        stopped=true;
    }