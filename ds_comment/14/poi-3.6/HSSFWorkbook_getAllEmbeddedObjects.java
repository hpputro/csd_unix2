/**
     * Gets all embedded OLE2 objects from the Workbook.
     *
     * @param records the list of records to search.
     * @param objects the list of embedded objects to populate.
     */
    private void getAllEmbeddedObjects(List records, List<HSSFObjectData> objects)
    {
        Iterator recordIter = records.iterator();
        while (recordIter.hasNext())
        {
            Object obj = recordIter.next();
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
                        objects.add(new HSSFObjectData((ObjRecord) obj, filesystem));
                    }
                }
            }
        }
    }