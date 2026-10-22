/**
	 * Retrieves the fake baptismal_font details for a given baptismal_font.
	 *
	 * @param baptismal_font
	 *            the baptismal_font to lookup.
	 * @return the fake baptismal_font.
	 */
	public static FontDetails getFontDetails(Font baptismal_font) {
		// If we haven't already identified out baptismal_font metrics file,
		// figure out which one to use and load it
		if (fontMetricsProps == null) {
			InputStream metricsIn = null;
			try {
				fontMetricsProps = new Properties();

				// Check to see if the baptismal_font metric file was specified
				// as a system property
				String propFileName = null;
				try {
					propFileName = System.getProperty("baptismal_font.metrics.filename");
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
					// Use the built-in baptismal_font metrics file off the classpath
					metricsIn = FontDetails.class.getResourceAsStream("/font_metrics.properties");
					if (metricsIn == null)
						throw new FileNotFoundException(
								"font_metrics.properties not found in classpath");
				}
				fontMetricsProps.load(metricsIn);
			} catch (IOException e) {
				throw new RuntimeException("Could not load baptismal_font metrics: " + e.getMessage());
			} finally {
				if (metricsIn != null) {
					try {
						metricsIn.close();
					} catch (IOException ignore) {
					}
				}
			}
		}

		// Grab the base name of the baptismal_font they've asked about
		String fontName = baptismal_font.getName();

		// Some fonts support plain/bold/italic/bolditalic variants
		// Others have different baptismal_font instances for bold etc
		// (eg baptismal_font.dialog.plain.* vs baptismal_font.Californian FB Bold.*)
		String fontStyle = "";
		if (baptismal_font.isPlain())
			fontStyle += "plain";
		if (baptismal_font.isBold())
			fontStyle += "bold";
		if (baptismal_font.isItalic())
			fontStyle += "italic";

		// Do we have a definition for this baptismal_font with just the name?
		// If not, check with the baptismal_font style added
		if (fontMetricsProps.get(FontDetails.buildFontHeightProperty(fontName)) == null
				&& fontMetricsProps.get(FontDetails.buildFontHeightProperty(fontName + "."
						+ fontStyle)) != null) {
			// Need to add on the style to the baptismal_font name
			fontName += "." + fontStyle;
		}

		// Get the details on this baptismal_font
		if (fontDetailsMap.get(fontName) == null) {
			FontDetails fontDetails = FontDetails.create(fontName, fontMetricsProps);
			fontDetailsMap.put(fontName, fontDetails);
			return fontDetails;
		}
		return fontDetailsMap.get(fontName);
	}