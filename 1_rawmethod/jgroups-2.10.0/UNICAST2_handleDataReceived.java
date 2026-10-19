/**
     * Check whether the hashtable contains an entry e for <code>sender</code> (create if not). If
     * e.received_msgs is null and <code>first</code> is true: create a new AckReceiverWindow(seqno) and
     * add message. Set e.received_msgs to the new window. Else just add the message.
     */
    private void handleDataReceived(Address sender, long seqno, long conn_id, boolean first, Message msg, Event evt) {
        if(log.isTraceEnabled()) {
            StringBuilder sb=new StringBuilder();
            sb.append(local_addr).append(" <-- DATA(").append(sender).append(": #").append(seqno);
            if(conn_id != 0) sb.append(", conn_id=").append(conn_id);
            if(first) sb.append(", first");
            sb.append(')');
            log.trace(sb);
        }

        ReceiverEntry entry=recv_table.get(sender);
        NakReceiverWindow win=entry != null? entry.received_msgs : null;

        if(first) {
            if(entry == null) {
                entry=getOrCreateReceiverEntry(sender, seqno, conn_id);
                win=entry.received_msgs;
            }
            else {  // entry != null && win != null
                if(conn_id != entry.recv_conn_id) {
                    if(log.isTraceEnabled())
                        log.trace(local_addr + ": conn_id=" + conn_id + " != " + entry.recv_conn_id + "; resetting receiver window");

                    ReceiverEntry entry2=recv_table.remove(sender);
                    if(entry2 != null)
                        entry2.received_msgs.destroy();

                    entry=getOrCreateReceiverEntry(sender, seqno, conn_id);
                    win=entry.received_msgs;
                }
                else {
                    ;
                }
            }
        }
        else { // entry == null && win == null OR entry != null && win == null OR entry != null && win != null
            if(win == null || entry.recv_conn_id != conn_id) {
                sendRequestForFirstSeqno(sender); // drops the message and returns (see below)
                return;
            }
        }

        boolean added=win.add(seqno, msg); // win is guaranteed to be non-null if we get here
        num_msgs_received++;
        num_bytes_received+=msg.getLength();

        if(added && max_bytes > 0) {
            int bytes=entry.received_bytes.addAndGet(msg.getLength());
            if(bytes >= max_bytes) {
                entry.received_bytes_lock.lock();
                try {
                    entry.received_bytes.set(0);
                }
                finally {
                    entry.received_bytes_lock.unlock();
                }

                sendStableMessage(sender, win.getHighestDelivered(), win.getHighestReceived());
            }
        }

        // An OOB message is passed up immediately. Later, when remove() is called, we discard it. This affects ordering !
        // http://jira.jboss.com/jira/browse/JGRP-377
        if(msg.isFlagSet(Message.OOB) && added) {
            try {
                up_prot.up(evt);
            }
            catch(Throwable t) {
                log.error("couldn't deliver OOB message " + msg, t);
            }
        }

        final AtomicBoolean processing=win.getProcessing();
        if(!processing.compareAndSet(false, true)) {
            return;
        }

        // try to remove (from the AckReceiverWindow) as many messages as possible and pass them up

        // Prevents concurrent passing up of messages by different threads (http://jira.jboss.com/jira/browse/JGRP-198);
        // this is all the more important once we have a concurrent stack (http://jira.jboss.com/jira/browse/JGRP-181),
        // where lots of threads can come up to this point concurrently, but only 1 is allowed to pass at a time
        // We *can* deliver messages from *different* senders concurrently, e.g. reception of P1, Q1, P2, Q2 can result in
        // delivery of P1, Q1, Q2, P2: FIFO (implemented by UNICAST) says messages need to be delivered only in the
        // order in which they were sent by their senders
        try {
            while(true) {
                List<Message> msgs=win.removeMany(processing, true, max_msg_batch_size); // remove my own messages
                if(msgs == null || msgs.isEmpty())
                    return;

                for(Message m: msgs) {
                    // discard OOB msg: it has already been delivered (http://jira.jboss.com/jira/browse/JGRP-377)
                    if(m.isFlagSet(Message.OOB))
                        continue;
                    try {
                        up_prot.up(new Event(Event.MSG, m));
                    }
                    catch(Throwable t) {
                        log.error("couldn't deliver message " + m, t);
                    }
                }
            }
        }
        finally {
            processing.set(false);
        }
    }