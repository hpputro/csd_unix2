
    public void sendGetMembersRequest(String cluster_name, Promise promise, boolean return_views_only) throws Exception{
        List<PingData> existing_mbrs=readAll(cluster_name);
        PhysicalAddress physical_addr=(PhysicalAddress)down(new Event(Event.GET_PHYSICAL_ADDRESS, local_addr));
        List<PhysicalAddress> physical_addrs=Arrays.asList(physical_addr);
        PingData data=new PingData(local_addr, null, false, UUID.get(local_addr), physical_addrs);

        // If we don't find any files, return immediately
        if(existing_mbrs.isEmpty()) {
            if(promise != null) {
                promise.setResult(null);
            }
        }
        else {

            // 1. Send GET_MBRS_REQ message to members listed in the file
            for(PingData tmp: existing_mbrs) {
                Collection<PhysicalAddress> dests=tmp != null? tmp.getPhysicalAddrs() : null;
                if(dests == null)
                    continue;
                for(final PhysicalAddress dest: dests) {
                    if(dest == null || dest.equals(physical_addr))
                        continue;
                    PingHeader hdr=new PingHeader(PingHeader.GET_MBRS_REQ, data, cluster_name);
                    hdr.return_view_only=return_views_only;
                    final Message msg=new Message(dest);
                    msg.plantFlag(Message.OOB);
                    msg.putHeader(this.id, hdr); // needs to be getName(), so we might get "MPING" !
                    // down_prot.down(new Event(Event.MSG,  msg));
                    if(log.isTraceEnabled())
                        log.trace("[FIND_INITIAL_MBRS] sending PING request to " + msg.getDest());
                    timer.execute(new Runnable() {
                        public void run() {
                            try {
                                down_prot.down(new Event(Event.MSG, msg));
                            }
                            catch(Exception ex){
                                if(log.isErrorEnabled())
                                    log.error("failed sending discovery request to " + dest, ex);
                            }
                        }
                    });
                }
            }
        }

        // Write my own data to file
        writeToFile(data, cluster_name);
    }