
	private final PaperSize getSuitablePaperSize(JasperPrint jasP)
	{

		if (jasP == null)
		{
			return null;
		}
		long breadth = 0;
		long height = 0;
		PaperSize ps = null;

		if ((jasP.getPageWidth() != 0) && (jasP.getPageHeight() != 0))
		{

			double dWidth = (jasP.getPageWidth() / 72.0);
			double dHeight = (jasP.getPageHeight() / 72.0);

			height = Math.round(dHeight * 25.4);
			breadth = Math.round(dWidth * 25.4);

			// Compare to ISO 216 A-Series (A3-A5). All other ISO 216 formats
			// not supported by JExcelApi yet.
			for (int i = 3; i < 6; i++)
			{
				int w = calculateWidthForDinAN(i);
				int h = calculateHeightForDinAN(i);

				if (((w == breadth) && (h == height)) || ((h == breadth) && (w == height)))
				{
					if (i == 3)
					{
						ps = PaperSize.A3;
					}
					else if (i == 4)
					{
						ps = PaperSize.A4;
					}
					else if (i == 5)
					{
						ps = PaperSize.A5;
					}
					break;
				}
			}

			// Compare to common North American Paper Sizes (ANSI X3.151-1987).
			if (ps == null)
			{
				// ANSI X3.151-1987 - "Letter" (216 � 279 mm)
				if (((breadth == 216) && (height == 279)) || ((breadth == 279) && (height == 216)))
				{
					ps = PaperSize.LETTER;
				}
				// ANSI X3.151-1987 - "Legal" (216 � 356 mm)
				if (((breadth == 216) && (height == 356)) || ((breadth == 356) && (height == 216)))
				{
					ps = PaperSize.LEGAL;
				}
				// ANSI X3.151-1987 - "Executive" (190 � 254 mm)
				// Not supperted by JExcelApi yet.

				// ANSI X3.151-1987 - "Ledger/Tabloid" (279 � 432 mm)
				// Not supperted by JExcelApi yet.
			}
		}
		return ps;
	}