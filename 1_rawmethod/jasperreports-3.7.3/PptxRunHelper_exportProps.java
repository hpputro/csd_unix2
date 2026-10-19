/**
	 *
	 */
	private void exportProps(String tag, Map parentAttrs,  Map attrs, Locale locale)
	{
		write("       <" + tag + "\n");

		Object value = attrs.get(TextAttribute.SIZE);
		Object oldValue = parentAttrs.get(TextAttribute.SIZE);

		if (value != null && !value.equals(oldValue))
		{
			float fontSize = ((Float)value).floatValue();
			fontSize = fontSize == 0 ? 0.5f : fontSize;// only the special EMPTY_CELL_STYLE would have font size zero
			write(" sz=\"" + (int)(100 * fontSize) + "\"");
		}
		else //FIXMEPPTX deal with default values from a style, a theme or something
		{
			float fontSize = ((Float)oldValue).floatValue();
			write(" sz=\"" + (int)(100 * fontSize) + "\"");
		}
		
		value = attrs.get(TextAttribute.WEIGHT);
		oldValue = parentAttrs.get(TextAttribute.WEIGHT);

		if (value != null && !value.equals(oldValue))
		{
			write(" b=\"" + (value.equals(TextAttribute.WEIGHT_BOLD) ? 1 : 0) + "\"");
		}

		value = attrs.get(TextAttribute.POSTURE);
		oldValue = parentAttrs.get(TextAttribute.POSTURE);

		if (value != null && !value.equals(oldValue))
		{
			write(" i=\"" + (value.equals(TextAttribute.POSTURE_OBLIQUE) ? 1 : 0) + "\"");
		}


		value = attrs.get(TextAttribute.UNDERLINE);
		oldValue = parentAttrs.get(TextAttribute.UNDERLINE);

		if (
			(value == null && oldValue != null)
			|| (value != null && !value.equals(oldValue))
			)
		{
			write(" u=\"" + (value == null ? "none" : "sng") + "\"");
		}
		
		value = attrs.get(TextAttribute.STRIKETHROUGH);
		oldValue = parentAttrs.get(TextAttribute.STRIKETHROUGH);

		if (
			(value == null && oldValue != null)
			|| (value != null && !value.equals(oldValue))
			)
		{
			write(" strike=\"" + (value == null ? "noStrike" : "sngStrike") + "\"");
		}

		value = attrs.get(TextAttribute.SUPERSCRIPT);

//		if (TextAttribute.SUPERSCRIPT_SUPER.equals(value))
//		{
//			write("        <a:vertAlign a:val=\"superscript\" />\n");
//		}
//		else if (TextAttribute.SUPERSCRIPT_SUB.equals(value))
//		{
//			write("        <a:vertAlign a:val=\"subscript\" />\n");
//		}

		write(">\n");

		value = attrs.get(TextAttribute.FOREGROUND);
		oldValue = parentAttrs.get(TextAttribute.FOREGROUND);
		
		if (value != null && !value.equals(oldValue))
		{
			write("<a:solidFill><a:srgbClr val=\"" + JRColorUtil.getColorHexa((Color)value) + "\"/></a:solidFill>\n");
		}

		value = attrs.get(TextAttribute.BACKGROUND);
		oldValue = parentAttrs.get(TextAttribute.BACKGROUND);
		
//		if (value != null && !value.equals(oldValue))
//		{
//			write("<a:solidFill><a:srgbClr val=\"" + JRColorUtil.getColorHexa((Color)value) + "\"/></a:solidFill>\n");
//		}

		value = attrs.get(TextAttribute.FAMILY);
		oldValue = parentAttrs.get(TextAttribute.FAMILY);
		
		if (value != null && !value.equals(oldValue))//FIXMEDOCX the text locale might be different from the report locale, resulting in different export font
		{
			String fontFamilyAttr = (String)value;
			String fontFamily = fontFamilyAttr;
			if (fontMap != null && fontMap.containsKey(fontFamilyAttr))
			{
				fontFamily = (String) fontMap.get(fontFamilyAttr);
			}
			else
			{
				FontInfo fontInfo = JRFontUtil.getFontInfo(fontFamilyAttr, locale);
				if (fontInfo != null)
				{
					//fontName found in font extensions
					FontFamily family = fontInfo.getFontFamily();
					String exportFont = family.getExportFont(exporterKey);
					if (exportFont != null)
					{
						fontFamily = exportFont;
					}
				}
			}
			write("        <a:latin typeface=\"" + fontFamily + "\"/>\n");
			write("        <a:ea typeface=\"" + fontFamily + "\"/>\n");
			write("        <a:cs typeface=\"" + fontFamily + "\"/>\n");
		}
		
		write("</" + tag + ">\n");
	}