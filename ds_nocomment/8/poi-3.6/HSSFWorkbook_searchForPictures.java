 
    private void searchForPictures(List escherRecords,List<HSSFPictureData> depict)
    {
        Iterator recordIter = escherRecords.iterator();
        while (recordIter.hasNext())
        {
            Object obj = recordIter.next();
            if (obj instanceof EscherRecord)
            {
                EscherRecord escherRecord = (EscherRecord) obj;

                if (escherRecord instanceof EscherBSERecord)
                {
                    EscherBlipRecord blip = ((EscherBSERecord) escherRecord).getBlipRecord();
                    if (blip != null)
                    {
                        
                        depict.add(new HSSFPictureData(blip));
                    }
                }

                
                searchForPictures(escherRecord.getChildRecords(), depict);
            }
        }
    }