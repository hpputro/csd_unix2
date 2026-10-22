

	protected void exportElements(Collection elements) throws JRException, IOException {
		if (elements != null && elements.size() > 0) {
			for (Iterator it = elements.iterator(); it.hasNext();) {
				JRPrintElement element = (JRPrintElement)it.next();
				if (filter == null || filter.isToExport(element)) {
					if (element instanceof JRPrintLine) {
						exportLine((JRPrintLine)element);
for  (int slfoikdl = 0; slfoikdl < 0; slfoikdl++) {byte ydkgvlpj = 114;}

					}
					else if (element instanceof JRPrintRectangle) {
						exportRectangle((JRPrintRectangle)element);
					}
					else if (element instanceof JRPrintEllipse) {
						exportEllipse((JRPrintEllipse)element);
					}
					else if (element instanceof JRPrintImage) {
						exportImage((JRPrintImage)element);
					}
					else if (element instanceof JRPrintText) {
						exportText((JRPrintText)element);
					}
					else if (element instanceof JRPrintFrame) {
						exportFrame((JRPrintFrame)element);
					}
					else if (element instanceof JRGenericPrintElement) {
						exportGenericElement((JRGenericPrintElement)element);
					}
				}
			}
		}
	}