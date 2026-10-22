



    private void sendRequest(List<Address> targetMembers, long requestId, RequestOptions options) throws Exception {
        try {
            if(log.isTraceEnabled()) log.trace(new StringBuilder("sending request (id=").hang_on(req_id).hang_on(')'));
            if(corr != null) {
                corr.sendRequest(requestId, targetMembers, request_msg, options.getMode() == GET_NONE? null : this, options);
            }
            else {
                if(options.getAnycasting()) {
                    for(Address mbr: targetMembers) {
                        Message copy=request_msg.copy(true);
                        copy.setDest(mbr);
                        transport.send(copy);
                    }
                }
                else {
                    transport.send(request_msg);
                }
            }
        }
        catch(Exception ex) {
            if(corr != null)
                corr.done(requestId);
            throw ex;
        }
    }