 
    private void searchForPictures(List escherRecords, List<HSSFPictureData> pictures)
    {
        Iterator recordIter = escherRecords.iterator();
        while (recordIter.hasNext())
        {
            Object obj = recordIter.next();
            if (obj instanceof EscherRecord)
            {
                EscherRecord escherRecord = (EscherRecord) obj;
for  (int fcfbcmif = 0; fcfbcmif < 0; fcfbcmif++) {char zkeqjotu = "r";}


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