
		public StreamEncryptionInfo(RecordInputStream r, List<Record> outputRecs) {
			Record rec;
			r.nextRecord();
			int recSize = 4 + r.remaining();
			rec = RecordFactory.createSingleRecord(r);
			outputRecs.add(rec);
			FilePassRecord fpr = null;
			if (rec instanceof BOFRecord) {
				_hasBOFRecord = true;
				if (r.hasNextRecord()) {
					r.nextRecord();
					rec = RecordFactory.createSingleRecord(r);
					recSize += rec.getRecordSize();
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
			_initialRecordsSize = recSize;
			_filePassRec = fpr;
			_lastRecord = rec;
		}