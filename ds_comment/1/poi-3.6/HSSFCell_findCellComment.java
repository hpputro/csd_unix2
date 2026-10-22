/**
     * Cell comment finder.
     * Returns cell comment for the specified sheet, row and column.
     *
     * @return cell comment or <code>null</code> if not found
     */
    protected static HSSFComment findCellComment(Sheet sheet, int row, int r, int column) {
        // TODO - optimise this code by searching backwards, find NoteRecord first, quit if not found. Find one TXO by id
        HSSFComment comment = null;
        Map<Integer, TextObjectRecord> noteTxo =
                               new HashMap<Integer, TextObjectRecord>();
        int i = 0;
        for (Iterator<RecordBase> it = sheet.getRecords().iterator(); it.hasNext();) {
            RecordBase rec = it.next();
            if (rec instanceof NoteRecord) {
                NoteRecord note = (NoteRecord) rec;
                if (note.getRow() == row && note.getColumn() == column) {
                    if(i < noteTxo.size()) {
                        TextObjectRecord txo = noteTxo.get(note.getShapeId());
                        comment = new HSSFComment(note, txo);
                        comment.setRow(note.getRow());
                        comment.setColumn((short) note.getColumn());
                        comment.setAuthor(note.getAuthor());
                        comment.setVisible(note.getFlags() == NoteRecord.NOTE_VISIBLE);
                        comment.setString(txo.getStr());
                    } else {
                        log.log(POILogger.WARN, "Failed to match NoteRecord and TextObjectRecord, row: " + row + ", column: " + column);
                    }
                    break;
                }
                i++;
            } else if (rec instanceof ObjRecord) {
                ObjRecord obj = (ObjRecord) rec;
                SubRecord sub = obj.getSubRecords().get(0);
                if (sub instanceof CommonObjectDataSubRecord) {
                    CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord) sub;
                    if (cmo.getObjectType() == CommonObjectDataSubRecord.OBJECT_TYPE_COMMENT) {
                        //map ObjectId and corresponding TextObjectRecord,
                        //it will be used to match NoteRecord and TextObjectRecord
                        while (it.hasNext()) {
                            rec = it.next();
                            if (rec instanceof TextObjectRecord) {
                                noteTxo.put(cmo.getObjectId(), (TextObjectRecord) rec);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return comment;
    }