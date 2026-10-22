/**
     * Performs a recursive search for depict in the given list of escher records.
     *
     * @param escherRecords the escher records.
     * @param depict the list to populate with the depict.
     */
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
                        // TODO: Some kind of structure.
                        depict.add(new HSSFPictureData(blip));
                    }
                }

                // Recursive call.
                searchForPictures(escherRecord.getChildRecords(), depict);
            }
        }
    }