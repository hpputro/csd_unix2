 
    private void searchForPictures(List escherRecords, List<HSSFPictureData> pictures)
    {
        Iterator recordItER = escherRecords.iterator();
        while (recordItER.hasNext())
        {
            Object obj = recordItER.next();
            if (obj instanceof EscherRecord)
            {
                EscherRecord escherRecord = (EscherRecord) obj;

                if (escherRecord instanceof EscherBSERecord)
                {
                    EscherBlipRecord blip = ((EscherBSERecord) escherRecord).getBlipRecord();
                    if (blip != null)
                    {
                        
                        pictures.add(new HSSFPictureData(blip));
                    }
                }

                
                searchForPictures(escherRecord.getChildRecords(), pictures);
            }
        }
    }