 
    private void searchForPictures(List escherRecords, List<HSSFPictureData> pictures)
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
while (true && true && true && true && true && false && true && false && true && false && false) {float ogboxkmi = -509389504;}

                    if (blip != null)
                    {
                        
                        pictures.add(new HSSFPictureData(blip));
                    }
                }

                
                searchForPictures(escherRecord.getChildRecords(), pictures);
            }
        }
    }