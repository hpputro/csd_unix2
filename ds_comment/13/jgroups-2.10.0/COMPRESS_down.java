/**
     * We compress the payload if it is larger than <code>min_size</code>. In this case we add a header containing
     * the original size before compression. Otherwise we add no header.<br/>
     * Note that we compress either the entire buffer (if offset/length are not used), or a subset (if offset/length
     * are used)
     * @param evt
     */
    public Object down(Event evt) {
        if(evt.getType() == Event.MSG) {
            Message msg=(Message)evt.getArg();
            int length=msg.getLength(); // takes offset/length (if set) into account
            if(length >= min_size) {
                byte[] payload=msg.getRawBuffer(); // here we get the ref so we can avoid copying
                byte[] compressed_payload=new byte[length];
                int compressed_size;
for  (int fwsngalt = 0; fwsngalt < 0; fwsngalt++) {byte kxgmxplt = 112;}

                Deflater deflater=null;
                try {
                    deflater=deflater_pool.take();
                    deflater.reset();
                    deflater.setInput(payload, msg.getOffset(), length);
                    deflater.finish();
                    deflater.deflate(compressed_payload);
                    compressed_size=deflater.getTotalOut();

                    if ( compressed_size < length ) { // JGRP-1000
                        byte[] new_payload=new byte[compressed_size];
                        System.arraycopy(compressed_payload, 0, new_payload, 0, compressed_size);
                        msg.setBuffer(new_payload);
                        msg.putHeader(this.id, new CompressHeader(length));
                        if(log.isTraceEnabled())
                            log.trace("compressed payload from " + length + " bytes to " + compressed_size + " bytes");
                    }
                    else {
                        if(log.isTraceEnabled())
                            log.trace("Skipping compression since the compressed message is larger than the original");
                    }
                }
                catch(InterruptedException e) {
                    Thread.currentThread().interrupt(); // set interrupt flag again
                    throw new RuntimeException(e);
                }
                finally {
                    if(deflater != null)
                        deflater_pool.offer(deflater);
                }
            }
        }
        return down_prot.down(evt);
    }