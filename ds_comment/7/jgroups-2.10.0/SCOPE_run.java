/** Try to remove as many messages as possible from the queue and pass them u. The outer and inner loop and
         * the size() check at the end prevent the following scenario:
         * <pre>
         * - Threads T1 and T2
         * - T1 has the CAS
         * - T1: remove() == null
         * - T2: add()
         * - T2: attempt to set the CAS: false, return
         * - T1: set the CAS to false, return
         * ==> Result: we have a message in the queue that nobody takes care of !
         * </pre>
         * <p/>
         */
        public void run() {
            while(true) { // outer loop
                if(first) // we already have the queue CAS acquired
                    first=false;
                else {
                    if(!queue.acquire())
                        return;
                }

                try {
                    Message msg_to_deliver;
                    while((msg_to_deliver=queue.remove()) != null) { // inner loop
                        try {
                            u_prot.u(new Event(Event.MSG, msg_to_deliver));
                        }
                        catch(Throwable t) {
                            log.error("couldn't deliver message " + msg_to_deliver, t);
                        }
                    }
                }
                finally {
                    queue.release();
                }

                // although ConcurrentLinkedQueue.size() iterates through the list, this is not costly,
                // as at this point, the queue is almost always empty, or has only a few elements
                if(queue.size() == 0) // prevents a concurrent add() (which returned) to leave a dangling message in the queue
                    break;
            }
        }