
		public StreamEncryptionInfo(RecordInputStream rs, List<Record> outputRecs) {
			Record rec;
			rs.nextRecord();
			int recsize = 4 + rs.remaining();
			rec = RecordFactory.createSingleRecord(rs);
			outputRecs.add(rec);
			FilePassRecord fpr = null;
			if (rec instanceof BOFRecord) {
				_hasBOFRecord = true;
				if (rs.hasNextRecord()) {
					rs.nextRecord();
					rec = RecordFactory.createSingleRecord(rs);
					recsize += rec.getRecordSize();
					outputRecs.add(rec);
					if (rec instanceof FilePassRecord) {
						fpr = (FilePassRecord) rec;
						outputRecs.remove(outputRecs.size()-1);
						
						rec = outputRecs.get(0);
					} else {
						
						if (rec instanceof EOFRecord) {
							
							
							throw new IllegalStateException("Nothing between BOF and EOF");
						}
					}
				}
			} else {
				
				
				
				_hasBOFRecord = false;
			}
			_initialRecordsSize = recsize;
			_filePassRec = fpr;
			_lastRecord = rec;
		}