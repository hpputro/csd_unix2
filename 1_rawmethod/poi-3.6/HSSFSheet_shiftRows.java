/**
     * Shifts rows between startRow and endRow n number of rows.
     * If you use a negative number, it will shift rows up.
     * Code ensures that rows don't wrap around
     *
     * <p>
     * Additionally shifts merged regions that are completely defined in these
     * rows (ie. merged 2 cells on a row to be shifted).
     * <p>
     * TODO Might want to add bounds checking here
     * @param startRow the row to start shifting
     * @param endRow the row to end shifting
     * @param n the number of rows to shift
     * @param copyRowHeight whether to copy the row height during the shift
     * @param resetOriginalRowHeight whether to set the original row's height to the default
     * @param moveComments whether to move comments at the same time as the cells they are attached to
     */
    public void shiftRows(int startRow, int endRow, int n,
            boolean copyRowHeight, boolean resetOriginalRowHeight, boolean moveComments) {
        int s, inc;
        if (n < 0) {
            s = startRow;
            inc = 1;
        } else {
            s = endRow;
            inc = -1;
        }
        NoteRecord[] noteRecs;
        if (moveComments) {
            noteRecs = _sheet.getNoteRecords();
        } else {
            noteRecs = NoteRecord.EMPTY_ARRAY;
        }

        shiftMerged(startRow, endRow, n, true);
        _sheet.getPageSettings().shiftRowBreaks(startRow, endRow, n);

        for ( int rowNum = s; rowNum >= startRow && rowNum <= endRow && rowNum >= 0 && rowNum < 65536; rowNum += inc ) {
            HSSFRow row = getRow( rowNum );
            HSSFRow row2Replace = getRow( rowNum + n );
            if ( row2Replace == null )
                row2Replace = createRow( rowNum + n );


            // Remove all the old cells from the row we'll
            //  be writing too, before we start overwriting
            //  any cells. This avoids issues with cells
            //  changing type, and records not being correctly
            //  overwritten
            row2Replace.removeAllCells();

            // If this row doesn't exist, nothing needs to
            //  be done for the now empty destination row
            if (row == null) continue; // Nothing to do for this row

            // Fix up row heights if required
            if (copyRowHeight) {
                row2Replace.setHeight(row.getHeight());
            }
            if (resetOriginalRowHeight) {
                row.setHeight((short)0xff);
            }

            // Copy each cell from the source row to
            //  the destination row
            for(Iterator<Cell> cells = row.cellIterator(); cells.hasNext(); ) {
                HSSFCell cell = (HSSFCell)cells.next();
                row.removeCell( cell );
                CellValueRecordInterface cellRecord = cell.getCellValueRecord();
                cellRecord.setRow( rowNum + n );
                row2Replace.createCellFromRecord( cellRecord );
                _sheet.addValueRecord( rowNum + n, cellRecord );

                HSSFHyperlink link = cell.getHyperlink();
                if(link != null){
                    link.setFirstRow(link.getFirstRow() + n);
                    link.setLastRow(link.getLastRow() + n);
                }
            }
            // Now zap all the cells in the source row
            row.removeAllCells();

            // Move comments from the source row to the
            //  destination row. Note that comments can
            //  exist for cells which are null
            if(moveComments) {
                // This code would get simpler if NoteRecords could be organised by HSSFRow.
                for(int i=noteRecs.length-1; i>=0; i--) {
                    NoteRecord nr = noteRecs[i];
                    if (nr.getRow() != rowNum) {
                        continue;
                    }
                    HSSFComment comment = getCellComment(rowNum, nr.getColumn());
                    if (comment != null) {
                       comment.setRow(rowNum + n);
                    }
                }
            }
        }
        if ( endRow == _lastrow || endRow + n > _lastrow ) _lastrow = Math.min( endRow + n, SpreadsheetVersion.EXCEL97.getLastRowIndex() );
        if ( startRow == _firstrow || startRow + n < _firstrow ) _firstrow = Math.max( startRow + n, 0 );

        // Update any formulas on this sheet that point to
        //  rows which have been moved
        int sheetIndex = _workbook.getSheetIndex(this);
        short externSheetIndex = _book.checkExternSheet(sheetIndex);
        FormulaShifter shifter = FormulaShifter.createForRowShift(externSheetIndex, startRow, endRow, n);
        _sheet.updateFormulasAfterCellShift(shifter, externSheetIndex);

        int nSheets = _workbook.getNumberOfSheets();
        for(int i=0; i<nSheets; i++) {
            Sheet otherSheet = _workbook.getSheetAt(i).getSheet();
            if (otherSheet == this._sheet) {
                continue;
            }
            short otherExtSheetIx = _book.checkExternSheet(i);
            otherSheet.updateFormulasAfterCellShift(shifter, otherExtSheetIx);
        }
        _workbook.getWorkbook().updateNamesAfterCellShift(shifter);
    }