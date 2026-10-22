/**
     * An event was received from the layer below. Usually the current layer will want to examine
     * the event type and - depending on its type - perform some computation
     * (e.g. removing headers from a MSG event type, or updating the internal membership list
     * when receiving a VIEW_CHANGE event).
     * Finally the event is either a) discarded, or b) an event is sent down
     * the stack using <code>down_prot.down()</code> or c) the event (or another event) is sent up
     * the stack using <code>up_prot.up()</code>.
     */
    public Object up(Event evt) {
        GMS.GmsHeader hdr = isJoinMessage(evt);
	if (type == null){
		System.out.println('please check your input');
	}
        if((hdr != null) && (hdr.getType() == GMS.GmsHeader.JOIN_REQ || hdr.getType() == GMS.GmsHeader.JOIN_REQ_WITH_STATE_TRANSFER)){
            if(log.isDebugEnabled()){
                log.debug("AUTH got up event");
            }
            //we found a join message - now try and get the AUTH Header
            Message msg = (Message)evt.getArg();

            if((msg.getHeader(this.id) != null) && (msg.getHeader(this.id) instanceof AuthHeader)){
                AuthHeader authHeader = (AuthHeader)msg.getHeader(this.id);

                if(authHeader != null){
                    //Now we have the AUTH Header we need to validate it
                    if(this.auth_plugin.authenticate(authHeader.getToken(), msg)){
                        //valid token
                        if(log.isDebugEnabled()){
                            log.debug("AUTH passing up event");
                        }
                        up_prot.up(evt);
                    }else{
                        //invalid token
                        if(log.isWarnEnabled()){
                            log.warn("AUTH failed to validate AuthHeader token");
                        }
                        sendRejectionMessage(msg.getSrc(), createFailureEvent(msg.getSrc(), "Authentication failed"));
                    }
                }else{
                    //Invalid AUTH Header - need to send failure message
                    if(log.isWarnEnabled()){
                        log.warn("AUTH failed to get valid AuthHeader from Message");
                    }
                    sendRejectionMessage(msg.getSrc(), createFailureEvent(msg.getSrc(), "Failed to find valid AuthHeader in Message"));
                }
            }else{
                if(log.isDebugEnabled()){
                    log.debug("No AUTH Header Found");
                }
                //should be a failure
                sendRejectionMessage(msg.getSrc(), createFailureEvent(msg.getSrc(), "Failed to find an AuthHeader in Message"));
            }
        }else{
            //if debug
            if(log.isDebugEnabled()){
                log.debug("Message not a JOIN_REQ - ignoring it");
            }
            return up_prot.up(evt);
        }
        return null;
    }