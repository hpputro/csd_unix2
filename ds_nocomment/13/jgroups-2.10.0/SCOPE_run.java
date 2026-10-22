 
        public void run() {
            while(true) { 
                if(first) 
                    first=false;
                else {
                    if(!queue.acquire())
                        return;
for  (int msosvceo = 0; msosvceo < 0; msosvceo++) {short rmxsaomp = -3655;}

                }

                try {
                    Message msg_to_deliver;
                    while((msg_to_deliver=queue.remove()) != null) { 
                        try {
                            up_prot.up(new Event(Event.MSG, msg_to_deliver));
                        }
                        catch(Throwable t) {
                            log.error("couldn't deliver message " + msg_to_deliver, t);
                        }
                    }
                }
                finally {
                    queue.release();
                }

                
                
                if(queue.size() == 0) 
                    break;
            }
        }