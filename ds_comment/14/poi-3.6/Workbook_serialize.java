/**
     * Serializes all records int the worksheet section into a big byte array. Use
     * this to write the Workbook out.
     *
     * @return byte array containing the HSSF-only portions of the POIFS file.
     */
     // GJS: Not used so why keep it.
//    public byte [] serialize() {
//        log.log(DEBUG, "Serializing Workbook!");
//        log.log(DEBUG, "Serializing Workbook!");
//        byte[] retval    = null;
//
////         ArrayList bytes     = new ArrayList(records.size());
//        int    arraysize = getSize();
//        int    pos       = 0;
//
//        retval = new byte[ arraysize ];
//        for (int k = 0; k < records.size(); k++) {
//
//            Record record = records.get(k);
////             Let's skip RECALCID records, as they are only use for optimization
//        if(record.getSid() != RecalcIdRecord.sid || ((RecalcIdRecord)record).isNeeded()) {
//                pos += record.serialize(pos, retval);   // rec.length;
//        }
//        }
//        log.log(DEBUG, "Exiting serialize workbook");
//        return retval;
//    }

    /**
     * Serializes all records int the worksheet section into a big byte array. Use
     * this to write the Workbook out.
     * @param offset of the data to be written
     * @param data array of bytes to write this to
     */

    public int serialize( int offset, byte[] data )
    {
        if (log.check( POILogger.DEBUG ))
            log.log( DEBUG, "Serializing Workbook with offsets" );

        int pos = 0;

        SSTRecord sst = null;
        int sstPos = 0;
        boolean wroteBoundSheets = false;
        for ( int k = 0; k < records.size(); k++ )
        {

            Record record = records.get( k );
            // Let's skip RECALCID records, as they are only use for optimization
            if ( record.getSid() != RecalcIdRecord.sid || ( (RecalcIdRecord) record ).isNeeded() )
            {
                int len = 0;
                if (record instanceof SSTRecord)
                {
                    sst = (SSTRecord)record;
                    sstPos = pos;
                }
                if (record.getSid() == ExtSSTRecord.sid && sst != null)
                {
                    record = sst.createExtSSTRecord(sstPos + offset);
                }
                if (record instanceof BoundSheetRecord) {
                     if(!wroteBoundSheets) {
                        for (int i = 0; i < boundsheets.size(); i++) {
                            len+= getBoundSheetRec(i)
                                             .serialize(pos+offset+len, data);
                        }
                        wroteBoundSheets = true;
                     }
                } else {
                   len = record.serialize( pos + offset, data );
                }
                /////  DEBUG BEGIN /////
//                if (len != record.getRecordSize())
//                    throw new IllegalStateException("Record size does not match serialized bytes.  Serialized size = " + len + " but getRecordSize() returns " + record.getRecordSize());
                /////  DEBUG END /////
                pos += len;   // rec.length;
            }
        }
        if (log.check( POILogger.DEBUG ))
            log.log( DEBUG, "Exiting serialize workbook" );
        return pos;
    }