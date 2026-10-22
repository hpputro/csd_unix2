
	public ChartSubstreamRecordAggregate(RecordStream r) {
		_bofRec = (BOFRecord) r.getNext();
		List<RecordBase> temp = new ArrayList<RecordBase>();
		while (r.peekNextClass() != EOFRecord.class) {
			if (PageSettingsBlock.isComponentRecord(r.peekNextSid())) {
				if (_psBlock != null) {
					if (r.peekNextSid() == UnknownRecord.HEADER_FOOTER_089C) {
						// test samples: 45538_classic_Footer.xls, 45538_classic_Header.xls
						_psBlock.addLateHeaderFooter(r.getNext());
						continue;
					}
					throw new IllegalStateException(
							"Found more than one PageSettingsBlock in chart sub-stream");
				}
				_psBlock = new PageSettingsBlock(r);
				temp.add(_psBlock);
				continue;
			}
			temp.add(r.getNext());
		}
		_recs = temp;
		Record eof = r.getNext(); // no need to save EOF in field
		if (!(eof instanceof EOFRecord)) {
			throw new IllegalStateException("Bad chart EOF");
		}
	}