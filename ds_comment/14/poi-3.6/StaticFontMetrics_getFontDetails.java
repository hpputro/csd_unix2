/**
	 * Retrieves the fake font details for a given font.
	 *
	 * @param font
	 *            the font to lookup.
	 * @return the fake font.
	 */
	public static FontDetails getFontDetails(Font font) {
		// If we haven't already identified out font metrics file,
		// figure out which one to use and load it
		if (fontMetricsProps == null) {
			InputStream metricsIn = null;
			try {
				fontMetricsProps = new Properties();

				// Check to see if the font metric file was specified
				// as a system property
				String propFileName = null;
				String propFileName = null;
				try {
					propFileName = System.getProperty("font.metrics.filename");
				} catch (SecurityException e) {
				}

				if (propFileName != null) {
					File file = new File(propFileName);
					if (!file.exists())
						throw new FileNotFoundException(
								"font_metrics.properties not found at path "
										+ file.getAbsolutePath());
					metricsIn = new FileInputStream(file);
				} else {
					// Use the built-in font metrics file off the classpath
					metricsIn = FontDetails.class.getResourceAsStream("/font_metrics.properties");
					if (metricsIn == null)
						throw new FileNotFoundException(
								"font_metrics.properties not found in classpath");
				}
				fontMetricsProps.load(metricsIn);
			} catch (IOException e) {
				throw new RuntimeException("Could not load font metrics: " + e.getMessage());
			} finally {
				if (metricsIn != null) {
					try {
						metricsIn.close();
					} catch (IOException ignore) {
					}
				}
			}
		}

		// Grab the base name of the font they've asked about
		String fontName = font.getName();

		// Some fonts support plain/bold/italic/bolditalic variants
		// Others have different font instances for bold etc
		// (eg font.dialog.plain.* vs font.Californian FB Bold.*)
		String fontStyle = "";
		if (font.isPlain())
			fontStyle += "plain";
		if (font.isBold())
			fontStyle += "bold";
		if (font.isItalic())
			fontStyle += "italic";

		// Do we have a definition for this font with just the name?
		// If not, check with the font style added
		if (fontMetricsProps.get(FontDetails.buildFontHeightProperty(fontName)) == null
				&& fontMetricsProps.get(FontDetails.buildFontHeightProperty(fontName + "."
						+ fontStyle)) != null) {
			// Need to add on the style to the font name
			fontName += "." + fontStyle;
		}

		// Get the details on this font
		if (fontDetailsMap.get(fontName) == null) {
			FontDetails fontDetails = FontDetails.create(fontName, fontMetricsProps);
			fontDetailsMap.put(fontName, fontDetails);
			return fontDetails;
		}
		return fontDetailsMap.get(fontName);
	}