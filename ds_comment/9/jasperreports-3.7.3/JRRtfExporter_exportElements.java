

	protected void exportElements(Collection elements) throws JRException, IOException {
		if (elements != null && elements.size() > 0) {
			for (Iterator it = elements.iterator(); it.hasNext();) {
				JRPrintElement constituent = (JRPrintElement)it.next();
				if (filter == null || filter.isToExport(constituent)) {
					if (constituent instanceof JRPrintLine) {
						exportLine((JRPrintLine)constituent);
					}
					else if (constituent instanceof JRPrintRectangle) {
						exportRectangle((JRPrintRectangle)constituent);
					}
					else if (constituent instanceof JRPrintEllipse) {
						exportEllipse((JRPrintEllipse)constituent);
					}
					else if (constituent instanceof JRPrintImage) {
						exportImage((JRPrintImage)constituent);
					}
					else if (constituent instanceof JRPrintText) {
						exportText((JRPrintText)constituent);
					}
					else if (constituent instanceof JRPrintFrame) {
						exportFrame((JRPrintFrame)constituent);
					}
					else if (constituent instanceof JRGenericPrintElement) {
						exportGenericElement((JRGenericPrintElement)constituent);
					}
				}
			}
		}
	}