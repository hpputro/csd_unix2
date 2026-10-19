/**
     * Joins this process to a group. Determines the coordinator and sends a
     * unicast handleJoin() message to it. The coordinator returns a JoinRsp and
     * then broadcasts the new view, which contains a message digest and the
     * current membership (including the joiner). The joiner is then supposed to
     * install the new view and the digest and starts accepting mcast messages.
     * Previous mcast messages were discarded (this is done in PBCAST).
     * <p>
     * If successful, impl is changed to an instance of ParticipantGmsImpl.
     * Otherwise, we continue trying to send join() messages to the coordinator,
     * until we succeed (or there is no member in the group. In this case, we
     * create our own singleton group).
     * <p>
     * When GMS.disable_initial_coord is set to true, then we won't become
     * coordinator on receiving an initial membership of 0, but instead will
     * retry (forever) until we get an initial membership of > 0.
     * 
     * @param mbr Our own address (assigned through SET_LOCAL_ADDRESS)
     */
    private void joinInternal(Address mbr, boolean joinWithStateTransfer,boolean useFlushIfPresent) {
        Address coord=null;
        JoinRsp rsp=null;
        View tmp_view;
        leaving=false;

        join_promise.reset();
        while(!leaving) {
            if(rsp == null && !join_promise.hasResult()) { // null responses means that the discovery was cancelled
                List<PingData> responses=findInitialMembers(join_promise);
                if (responses == null) {
                    // gray: we've seen this NPE here.  not sure of the cases but wanted to add more debugging info
                    throw new NullPointerException("responses returned by findInitialMembers for " + join_promise + " is null");
                }
                /*// Sept 2008 (bela): break if we got a belated JoinRsp (https://jira.jboss.org/jira/browse/JGRP-687)
                if(join_promise.hasResult()) {
                    rsp=join_promise.getResult(gms.join_timeout); // clears the result
                    continue;
                }*/
                if(log.isDebugEnabled())
                    log.debug("initial_mbrs are " + responses);
                if(responses == null || responses.isEmpty()) {
                    if(gms.disable_initial_coord) {
                        if(log.isTraceEnabled())
                            log.trace("received an initial membership of 0, but cannot become coordinator " + "(disable_initial_coord=true), will retry fetching the initial membership");
                        continue;
                    }
                    if(log.isDebugEnabled())
                        log.debug("no initial members discovered: creating group as first member");
                    becomeSingletonMember(mbr);
                    return;
                }

                coord=determineCoord(responses);
                if(coord == null) { // e.g. because we have all clients only
                    if(gms.handle_concurrent_startup == false) {
                        if(log.isTraceEnabled())
                            log.trace("handle_concurrent_startup is false; ignoring responses of initial clients");
                        becomeSingletonMember(mbr);
                        return;
                    }

                    if(log.isTraceEnabled())
                        log.trace("could not determine coordinator from responses " + responses);

                    // so the member to become singleton member (and thus coord) is the first of all clients
                    Set<Address> clients=new TreeSet<Address>(); // sorted
                    clients.add(mbr); // add myself again (was removed by findInitialMembers())
                    for(PingData response: responses) {
                        Address client_addr=response.getAddress();
                        if(client_addr != null)
                            clients.add(client_addr);
                    }
                    if(log.isTraceEnabled())
                        log.trace("clients to choose new coord from are: " + clients);
                    Address new_coord=clients.iterator().next();
                    if(new_coord.equals(mbr)) {
                        if(log.isTraceEnabled())
                            log.trace("I (" + mbr + ") am the first of the clients, will become coordinator");
                        becomeSingletonMember(mbr);
                        return;
                    }
                    else {
                        if(log.isTraceEnabled())
                            log.trace("I (" + mbr
                                    + ") am not the first of the clients, waiting for another client to become coordinator");
                        Util.sleep(500);
                    }
                    continue;
                }

                if(log.isDebugEnabled())
                    log.debug("sending handleJoin(" + mbr + ") to " + coord);
                sendJoinMessage(coord, mbr, joinWithStateTransfer,useFlushIfPresent);
            }

            try {
                if(rsp == null)
                    rsp=join_promise.getResult(gms.join_timeout);
                if(rsp == null) {
                    if(log.isWarnEnabled())
                        log.warn("join(" + mbr + ") sent to " + coord + " timed out (after " + gms.join_timeout + " ms), retrying");
                    continue;
                }
                
                // 1. check whether JOIN was rejected
                String failure=rsp.getFailReason();
                if(failure != null)
                    throw new SecurityException(failure);

                // 2. Install digest
                if(rsp.getDigest() == null || rsp.getDigest().getSenders() == null) {
                    if(log.isWarnEnabled())
                        log.warn("digest response has no senders: digest=" + rsp.getDigest());
                    rsp=null; // just skip the response we guess
                    continue;
                }
                MutableDigest tmp_digest=new MutableDigest(rsp.getDigest());
                tmp_view=rsp.getView();
                if(tmp_view == null) {
                    if(log.isErrorEnabled())
                        log.error("JoinRsp has a null view, skipping it");
                    rsp=null;
                }
                else {
                    if(!tmp_digest.contains(gms.local_addr)) {
                        throw new IllegalStateException("digest returned from " + coord + " with JOIN_RSP does not contain myself (" +
                                gms.local_addr + "): join response: " + rsp);
                    }
                    tmp_digest.incrementHighestDeliveredSeqno(coord); // see DESIGN for details
                    tmp_digest.seal();
                    gms.setDigest(tmp_digest);

                    if(log.isDebugEnabled())
                        log.debug("[" + gms.local_addr + "]: JoinRsp=" + tmp_view + " [size=" + tmp_view.size() + "]\n\n");

                    if(!installView(tmp_view)) {
                        if(log.isErrorEnabled())
                            log.error("view installation failed, retrying to join group");
                        rsp=null;
                        continue;
                    }

                    // send VIEW_ACK to sender of view
                    Message view_ack=new Message(coord, null, null);
                    view_ack.setFlag(Message.OOB);
                    GMS.GmsHeader tmphdr=new GMS.GmsHeader(GMS.GmsHeader.VIEW_ACK);
                    view_ack.putHeader(gms.getId(), tmphdr);
                    gms.getDownProtocol().down(new Event(Event.MSG, view_ack));
                    return;
                }
            }
            catch(SecurityException security_ex) {
                throw security_ex;
            }
            catch(IllegalArgumentException illegal_arg) {
                throw illegal_arg;
            }
            catch(Throwable e) {
                if(log.isDebugEnabled())
                    log.debug("exception=" + e + ", retrying", e);
                rsp=null;
            }
        }
    }