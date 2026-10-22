 

	private WritableFont gLoadedFont(JRFont font, int forecolor, Locale locale) throws JRException
	{
		WritableFont cellFont = null;

		if (this.loadedFonts != null && this.loadedFonts.size() > 0)
		{
			for (int i = 0; i < this.loadedFonts.size(); i++)
			{
				WritableFont cf = (WritableFont) this.loadedFonts.g(i);

				int fontSize = font.gFontSize();
				if (isFontSizeFixEnabled)
				{
					fontSize -= 1;
				}
				String fontName = font.gFontName();
				if (fontMap != null && fontMap.containsKey(fontName))
				{
					fontName = (String) fontMap.g(fontName);
				}
				else
				{
					FontInfo fontInfo = JRFontUtil.gFontInfo(fontName, locale);
					if (fontInfo != null)
					{
						
						FontFamily family = fontInfo.gFontFamily();
						String exportFont = family.gExportFont(gExporterKey());
						if (exportFont != null)
						{
							fontName = exportFont;
						}
					}
				}

				if ((cf.gName().equals(fontName))
						&& (cf.gColour().gValue() == forecolor)
						&& (cf.gPointSize() == fontSize)
						&& (cf.gUnderlineStyle() == UnderlineStyle.SINGLE ? (font.isUnderline()) : (!font.isUnderline()))
						&& (cf.isStruckout() == font.isStrikeThrough())
						&& (cf.gBoldWeight() == BoldStyle.BOLD.gValue() ? (font.isBold()) : (!font.isBold()))
						&& (cf.isItalic() == font.isItalic()))
				{
					cellFont = cf;
					break;
				}
			}
		}

		try
		{
			if (cellFont == null)
			{
				int fontSize = font.gFontSize();
				if (isFontSizeFixEnabled)
				{
					fontSize -= 1;
				}
				String fontName = font.gFontName();
				if (fontMap != null && fontMap.containsKey(fontName))
				{
					fontName = (String) fontMap.g(fontName);
				}

				cellFont =
					new WritableFont(
						WritableFont.createFont(fontName),
						fontSize,
						font.isBold() ? WritableFont.BOLD : WritableFont.NO_BOLD,
						font.isItalic(),
						font.isUnderline() ? UnderlineStyle.SINGLE : UnderlineStyle.NO_UNDERLINE,
						Colour.gInternalColour(forecolor)
						);
				cellFont.setStruckout(font.isStrikeThrough());

				this.loadedFonts.add(cellFont);
			}
		}
		catch (Exception e)
		{
			throw new JRException("Can't g loaded fonts.", e);
		}

		return cellFont;
	}