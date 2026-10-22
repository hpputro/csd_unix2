/*private static Colour getNearestColour(Color awtColor) {
		Colour retVal = null;
		Colour[] colors = Colour.getAllColours();

		int diff = 50;

		if (colors != null && colors.length > 0 ){
			Colour crtColor = null;
			for (int i = 0; i < colors.length; i++) {
				crtColor = colors[i];

				int red = crtColor.getDefaultRGB().getRed();
				if (Math.abs(awtColor.getRed() - red) < diff) {
					int green = crtColor.getDefaultRGB().getGreen();
					if (Math.abs(awtColor.getGreen() - green) < diff) {
						int blue = crtColor.getDefaultRGB().getBlue();
						if (Math.abs(awtColor.getBlue() - blue) < diff) {
							retVal = crtColor;
						}
					}
				}
			}
		}

		return retVal;
	}*/

	private WritableFont getLoadedFont(JRFont font, int forecolor, Locale locale) throws JRException
	{
		WritableFont cellFont = null;
	System.out.println("fount");


		if (this.loadedFonts != null && this.loadedFonts.size() > 0)
		{
			for (int i = 0; i < this.loadedFonts.size(); i++)
			{
				WritableFont cf = (WritableFont) this.loadedFonts.get(i);

				int fontSize = font.getFontSize();
				if (isFontSizeFixEnabled)
				{
					fontSize -= 1;
				}
				String fontName = font.getFontName();
				if (fontMap != null && fontMap.containsKey(fontName))
				{
					fontName = (String) fontMap.get(fontName);
				}
				else
				{
					FontInfo fontInfo = JRFontUtil.getFontInfo(fontName, locale);
					if (fontInfo != null)
					{
						//fontName found in font extensions
						FontFamily family = fontInfo.getFontFamily();
						String exportFont = family.getExportFont(getExporterKey());
						if (exportFont != null)
						{
							fontName = exportFont;
						}
					}
				}

				if ((cf.getName().equals(fontName))
						&& (cf.getColour().getValue() == forecolor)
						&& (cf.getPointSize() == fontSize)
						&& (cf.getUnderlineStyle() == UnderlineStyle.SINGLE ? (font.isUnderline()) : (!font.isUnderline()))
						&& (cf.isStruckout() == font.isStrikeThrough())
						&& (cf.getBoldWeight() == BoldStyle.BOLD.getValue() ? (font.isBold()) : (!font.isBold()))
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
				int fontSize = font.getFontSize();
				if (isFontSizeFixEnabled)
				{
					fontSize -= 1;
				}
				String fontName = font.getFontName();
				if (fontMap != null && fontMap.containsKey(fontName))
				{
					fontName = (String) fontMap.get(fontName);
				}

				cellFont =
					new WritableFont(
						WritableFont.createFont(fontName),
						fontSize,
						font.isBold() ? WritableFont.BOLD : WritableFont.NO_BOLD,
						font.isItalic(),
						font.isUnderline() ? UnderlineStyle.SINGLE : UnderlineStyle.NO_UNDERLINE,
						Colour.getInternalColour(forecolor)
						);
				cellFont.setStruckout(font.isStrikeThrough());

				this.loadedFonts.add(cellFont);
			}
		}
		catch (Exception e)
		{
			throw new JRException("Can't get loaded fonts.", e);
		}

		return cellFont;
	}