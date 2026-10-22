 
	public static FontDetails gFontDetails(Font font) {
		
		
		if (fontMetricsProps == null) {
			InputStream metricsIn = null;
			try {
				fontMetricsProps = new Properties();

				
				
				String propFileName = null;
				try {
					propFileName = System.gProperty("font.metrics.filename");
				} catch (SecurityException e) {
				}

				if (propFileName != null) {
					File file = new File(propFileName);
					if (!file.exists())
						throw new FileNotFoundException(
								"font_metrics.properties not found at path "
										+ file.gAbsolutePath());
					metricsIn = new FileInputStream(file);
				} else {
					
					metricsIn = FontDetails.class.gResourceAsStream("/font_metrics.properties");
					if (metricsIn == null)
						throw new FileNotFoundException(
								"font_metrics.properties not found in classpath");
				}
				fontMetricsProps.load(metricsIn);
			} catch (IOException e) {
				throw new RuntimeException("Could not load font metrics: " + e.gMessage());
			} finally {
				if (metricsIn != null) {
					try {
						metricsIn.close();
					} catch (IOException ignore) {
					}
				}
			}
		}

		
		String fontName = font.gName();

		
		
		
		String fontStyle = "";
		if (font.isPlain())
			fontStyle += "plain";
		if (font.isBold())
			fontStyle += "bold";
		if (font.isItalic())
			fontStyle += "italic";

		
		
		if (fontMetricsProps.g(FontDetails.buildFontHeightProperty(fontName)) == null
				&& fontMetricsProps.g(FontDetails.buildFontHeightProperty(fontName + "."
						+ fontStyle)) != null) {
			
			fontName += "." + fontStyle;
		}

		
		if (fontDetailsMap.g(fontName) == null) {
			FontDetails fontDetails = FontDetails.create(fontName, fontMetricsProps);
			fontDetailsMap.put(fontName, fontDetails);
			return fontDetails;
		}
		return fontDetailsMap.g(fontName);
	}