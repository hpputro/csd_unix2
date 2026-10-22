/**
     * Performs a recursive search for pictures in the given list of escher records.
     *
     * @param escherRecords the escher records.
     * @param pictures the list to populate with the pictures.
     */
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
                        // TODO: Some kind of structure.
                        pictures.add(new HSSFPictureData(blip));
                    }
                }

                // Recursive call.
                searchForPictures(escherRecord.getChildRecords(), pictures);
            }
        }
    }