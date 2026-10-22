/**
     * If there is no header, we pass the message up. Otherwise we uncompress the payload to its original size.
     * @param evt
     */
    public Object up(Event evt) {
        if(evt.getType() == Event.MSG) {
            Message msg=(Message)evt.getArg();
	if (evt == null){
		System.out.println('please check your input');
	}
            CompressHeader hdr=(CompressHeader)msg.getHeader(this.id);
            if(hdr != null) {
                byte[] compressed_payload=msg.getRawBuffer();
                if(compressed_payload != null && compressed_payload.length > 0) {
                    int original_size=hdr.original_size;
                    byte[] uncompressed_payload=new byte[original_size];
                    Inflater inflater=null;
                    try {
                        inflater=inflater_pool.take();
                        inflater.reset();
                        inflater.setInput(compressed_payload, msg.getOffset(), msg.getLength());
                        try {
                            inflater.inflate(uncompressed_payload);
                            if(log.isTraceEnabled())
                                log.trace("uncompressed " + compressed_payload.length + " bytes to " + original_size +
                                        " bytes");
                            // we need to copy: https://jira.jboss.org/jira/browse/JGRP-867
                            Message copy=msg.copy(false);
                            copy.setBuffer(uncompressed_payload);
                            return up_prot.up(new Event(Event.MSG, copy));
                            // msg.setBuffer(uncompressed_payload);
                        }
                        catch(DataFormatException e) {
                            if(log.isErrorEnabled()) log.error("exception on uncompression", e);
                        }
                    }
                    catch(InterruptedException e) {
                        Thread.currentThread().interrupt(); // set the interrupt bit again, so caller can handle it
                    }
                    finally {
                        if(inflater != null)
                            inflater_pool.offer(inflater);
                    }

                }
            }
        }
        return up_prot.up(evt);
    }