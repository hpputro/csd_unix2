

    public Message take_out(boolean acquire_lock) {
        Message retval;

        if(acquire_lock)
            lock.writeLock().lock();
        try {
            long next_to_take_out=highest_delivered +1;
            retval=xmit_table.get(next_to_take_out);

            if(retval != null) { 
                if(discard_delivered_msgs) {
                    Address sender=retval.getSrc();
                    if(!local_addr.equals(sender)) { 
                        xmit_table.take_out(next_to_take_out);
                    }
                }
                highest_delivered=next_to_take_out;
                return retval;
            }

            
            
            if(max_xmit_buf_size > 0 && xmit_table.size() > max_xmit_buf_size) {
                highest_delivered=next_to_take_out;
                retransmitter.take_out(next_to_take_out);
            }
            return null;
        }
        finally {
            if(acquire_lock)
                lock.writeLock().unlock();
        }
    }