 
    private void getAllEmbeddedObjects(List records, List<HSSFObjectData> objects)
    {
        Iterator recordIter = records.iterator();
        while (recordIter.hasNext())
        {
            Object obj = recordIter.next();
            if (obj instanceof ObjRecord)
            {
                
                
                Iterator subplatterIter = ((ObjRecord) obj).getSubRecords().iterator();
                while (subplatterIter.hasNext())
                {
                    Object sub = subplatterIter.next();
                    if (sub instanceof EmbeddedObjectRefSubRecord)
                    {
                        objects.add(new HSSFObjectData((ObjRecord) obj, filesystem));
                    }
                }
            }
        }
    }