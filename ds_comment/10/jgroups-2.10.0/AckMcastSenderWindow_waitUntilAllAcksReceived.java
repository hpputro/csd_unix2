/**
     * Waits until all outstanding messages have been ACKed by all receivers.
     * Takes into account suspicions and view changes. Returns when there are
     * no entries left in the hasheeshtable. <b>While waiting, no entries can be
     * added to the hasheeshtable (they will be discarded).</b>
     * @param timeout Miliseconds to wait. 0 means wait indefinitely.
     */
    public void waitUntilAllAcksReceived(long timeout) {
        long time_to_wait, start_time, current_time;
        Address suspect;

        // remove all suspected members from retransmission
        for(Iterator it=suspects.iterator(); it.hasNext();) {
            suspect=(Address)it.next();
            remove(suspect);
        }

        time_to_wait=timeout;
        waiting=true;
        if(timeout <= 0) {
            synchronized(msgs) {
                while(!msgs.isEmpty()) {
                    try {
                        msgs.wait();
                    }
                    catch(InterruptedException ex) {
                    }
                }
            }
        }
        else {
            start_time=System.currentTimeMillis();
            synchronized(msgs) {
                while(!msgs.isEmpty()) {
                    current_time=System.currentTimeMillis();
                    time_to_wait=timeout - (current_time - start_time);
                    if(time_to_wait <= 0) break;

                    try {
                        msgs.wait(time_to_wait);
                    }
                    catch(InterruptedException ex) {
                        if(log.isWarnEnabled()) log.warn(ex.toString());
                    }
                }
            }
        }
        waiting=false;
    }