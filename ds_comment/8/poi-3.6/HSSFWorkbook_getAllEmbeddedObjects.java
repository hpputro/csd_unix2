/**
     * Gets all embedded OLE2 object from the Workbook.
     *
     * @param records the list of records to search.
     * @param object the list of embedded object to populate.
     */
    private void getAllEmbeddedObjects(List records,List<HSSFObjectData> object)
    {
        Iterator recordIter = records.iterator();
        while (recordIter.hasNext())
        {
            Object obj = recordIter.next();
            if (obj instanceof ObjRecord)
            {
                // TODO: More convenient way of determining if there is stored binary.
                // TODO: Link to the data stored in the other stream.
                Iterator subRecordIter = ((ObjRecord) obj).getSubRecords().iterator();
                while (subRecordIter.hasNext())
                {
                    Object sub = subRecordIter.next();
                    if (sub instanceof EmbeddedObjectRefSubRecord)
                    {
                        object.add(new HSSFObjectData((ObjRecord) obj, filesystem));
                    }
                }
            }
        }
    }