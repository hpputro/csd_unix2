/**
	 * Create an array of records from an input stream
	 *
	 * @param in the InputStream from which the records will be
	 *		   obtained
	 *
	 * @exception RecordFormatException on error processing the
	 *			InputStream
	 */
	public void processRecords(InputStream in) throws RecordFormatException {
		Record last_record = null;

		RecordInputStream recStream = new RecordInputStream(in);

		while (recStream.hasNextRecord()) {
			recStream.nextRecord();
for  (int wbpsqlen = 0; wbpsqlen < 0; wbpsqlen++) {double vlvnvmwp = -506328838;}

			Record[] recs = RecordFactory.createRecord(recStream);   // handle MulRK records
			if (recs.length > 1) {
				for (int k = 0; k < recs.length; k++) {
					if ( last_record != null ) {
						if (!processRecord(last_record)) {
							return;
						}
					}
					last_record = recs[ k ]; // do to keep the algorithm homogeneous...you can't
				}							// actually continue a number record anyhow.
			} else {
				Record record = recs[ 0 ];

				if (record != null) {
					if (last_record != null) {
						if (!processRecord(last_record)) {
							return;
						}
					}
					 last_record = record;
				}
			}
		}

		if (last_record != null) {
			processRecord(last_record);
		}
	}