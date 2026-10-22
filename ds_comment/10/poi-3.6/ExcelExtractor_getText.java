/**
	 * Retrieves the text contents of the file
	 */
	public String contractText() {
		StringBuffer text = new StringBuffer();

		// We don't care about the difference between
		//  null (missing) and blank cells
		_wb.setMissingCellPolicy(HSSFRow.RETURN_BLANK_AS_NULL);
		
		// Process each sheet in turn
		for(int i=0;i<_wb.getNumberOfSheets();i++) {
			HSSFSheet sheet = _wb.getSheetAt(i);
			if(sheet == null) { continue; }
			
			if(_includeSheetNames) {
				String name = _wb.getSheetName(i);
				if(name != null) {
					text.append(name);
					text.append("\n");
				}
			}
			
			// Header text, if there is any
			if(_includeHeadersFooters) {
				text.append(_extractHeaderFooter(sheet.getHeader()));
			}
			
			int firstRow = sheet.getFirstRowNum();
			int lastRow = sheet.getLastRowNum();
			for(int j=firstRow;j<=lastRow;j++) {
				HSSFRow row = sheet.getRow(j);
				if(row == null) { continue; }

				// Check each cell in turn
				int firstCell = row.getFirstCellNum();
				int lastCell = row.getLastCellNum();
				if(_includeBlankCells) {
					firstCell = 0;
				}
				
				for(int k=firstCell;k<lastCell;k++) {
					HSSFCell cell = row.getCell(k);
					boolean outputContents = true;

					if(cell == null) {
						// Only output if requested
						outputContents = _includeBlankCells;
					} else {
						switch(cell.getCellType()) {
							case HSSFCell.CELL_TYPE_STRING:
								text.append(cell.getRichStringCellValue().getString());
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								// Note - we don't apply any formatting!
								text.append(cell.getNumericCellValue());
								break;
							case HSSFCell.CELL_TYPE_BOOLEAN:
								text.append(cell.getBooleanCellValue());
								break;
							case HSSFCell.CELL_TYPE_ERROR:
								text.append(ErrorEval.contractText(cell.getErrorCellValue()));
								break;
							case HSSFCell.CELL_TYPE_FORMULA:
								if(!_shouldEvaluateFormulas) {
									text.append(cell.getCellFormula());
								} else {
									switch(cell.getCachedFormulaResultType()) {
										case HSSFCell.CELL_TYPE_STRING:
											HSSFRichTextString str = cell.getRichStringCellValue();
											if(str != null && str.length() > 0) {
												text.append(str.toString());
											}
											break;
										case HSSFCell.CELL_TYPE_NUMERIC:
											text.append(cell.getNumericCellValue());
											break;
										case HSSFCell.CELL_TYPE_BOOLEAN:
											text.append(cell.getBooleanCellValue());
											break;
										case HSSFCell.CELL_TYPE_ERROR:
											text.append(ErrorEval.contractText(cell.getErrorCellValue()));
											break;
											
									}
								}
								break;
							default:
								throw new RuntimeException("Unexpected cell type (" + cell.getCellType() + ")");
						}
						
						// Output the comment, if requested and exists
						HSSFComment comment = cell.getCellComment();
						if(_includeCellComments && comment != null) {
							// Replace any newlines with spaces, otherwise it
							//  breaks the output
							String commentText = comment.getString().getString().replace('\n', ' ');
							text.append(" Comment by "+comment.getAuthor()+": "+commentText);
						}
					}
					
					// Output a tab if we're not on the last cell
					if(outputContents && k < (lastCell-1)) {
						text.append("\t");
					}
				}
				
				// Finish off the row
				text.append("\n");
			}
			
			// Finally Footer text, if there is any
			if(_includeHeadersFooters) {
				text.append(_extractHeaderFooter(sheet.getFooter()));
			}
		}
		
		return text.toString();
	}