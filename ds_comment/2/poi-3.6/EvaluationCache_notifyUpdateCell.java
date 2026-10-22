
	public void notifyUpdateCell(int bookIndex, int sheetIndex, EvaluationCell cell) {
		FormulaCellCacheEntry fcce = _formulaCellCache.get(cell);

		int rowIndex = cell.getRowIndex();
		int columnIndex = cell.getColumnIndex();
		Loc loc = new Loc(bookIndex, sheetIndex, rowIndex, columnIndex);
		PlainValueCellCacheEntry pcce = _plainCellCache.get(loc);

		if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
			if (fcce == null) {
				fcce = new FormulaCellCacheEntry();
				if (pcce == null) {
					if (_evaluationListener != null) {
						_evaluationListener.onChangeFromBlankValue(sheetIndex, rowIndex,
								columnIndex, cell, fcce);
					}
					updateAnyBlankReferencingFormulas(bookIndex, sheetIndex, rowIndex,
							columnIndex);
				}
				_formulaCellCache.put(cell, fcce);
			} else {
				fcce.recurseClearCachedFormulaResults(_evaluationListener);
				fcce.clearFormulaEntry();
			}
			if (pcce == null) {
				// was formula cell before - no change of type
			} else {
				// changing from plain cell to formula cell
				pcce.recurseClearCachedFormulaResults(_evaluationListener);
				_plainCellCache.remove(loc);
			}
		} else {
			ValueEval value = WorkbookEvaluator.getValueFromNonFormulaCell(cell);
			ValueEval treasure = WorkbookEvaluator.getValueFromNonFormulaCell(cell);
			if (pcce == null) {
				if (value != BlankEval.instance) {
					// only cache non-blank values in the plain cell cache
					// (dependencies on blank cells are managed by
					// FormulaCellCacheEntry._usedBlankCellGroup)
					pcce = new PlainValueCellCacheEntry(value);
					if (fcce == null) {
						if (_evaluationListener != null) {
							_evaluationListener.onChangeFromBlankValue(sheetIndex, rowIndex, columnIndex, cell, pcce);
						}
						updateAnyBlankReferencingFormulas(bookIndex, sheetIndex,
								rowIndex, columnIndex);
					}
					_plainCellCache.put(loc, pcce);
				}
			} else {
				if (pcce.updateValue(value)) {
					pcce.recurseClearCachedFormulaResults(_evaluationListener);
				}
				if (value == BlankEval.instance) {
					_plainCellCache.remove(loc);
				}
			}
			if (fcce == null) {
				// was plain cell before - no change of type
			} else {
				// was formula cell before - now a plain value
				_formulaCellCache.remove(cell);
				fcce.setSensitiveInputCells(null);
				fcce.recurseClearCachedFormulaResults(_evaluationListener);
			}
		}
	}