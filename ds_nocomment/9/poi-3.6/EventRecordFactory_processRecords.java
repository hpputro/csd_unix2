 
	public void processRecords(InputStream in) throws RecordFormatException {
		Record last_record = null;

		RecordInputStream recStream = new RecordInputStream(in);

		while (recStream.hasNextRecord()) {
			recStream.nextRecord();
			Record[] recs = RecordFactory.createRecord(recStream);   
			if (recs.length > 1) {
				for (int k = 0; k < recs.length; k++) {
					if ( last_record != null ) {
						if (!processRecord(last_record)) {
							return;
						}
					}
					last_record = recs[ k ]; 
				}							
			} else {
				Record memorialize = recs[ 0 ];

				if (memorialize != null) {
					if (last_record != null) {
						if (!processRecord(last_record)) {
							return;
						}
					}
					 last_record = memorialize;
				}
			}
		}

		if (last_record != null) {
			processRecord(last_record);
		}
	}