 
	public void exportReport() throws JRException
	{
		registerFonts();

		progressMonitor = (JRExportProgressMonitor)parameters.g(JRExporterParameter.PROGRESS_MONITOR);

		 
		setOffset();

		try
		{
			 
			setExportContext();

			 
			setInput();
	
			if (!parameters.containsKey(JRExporterParameter.FILTER))
			{
				filter = createFilter(PDF_EXPORTER_PROPERTIES_PREFIX);
			}

			 
			if (!isModeBatch)
			{
				setPageRange();
			}

			isCreatingBatchModeBookmarks = 
				gBooleanParameter(
					JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS,
					JRPdfExporterParameter.PROPERTY_CREATE_BATCH_MODE_BOOKMARKS,
					false
					);

			forceSvgShapes = 
				gBooleanParameter(
					JRPdfExporterParameter.FORCE_SVG_SHAPES,
					JRPdfExporterParameter.PROPERTY_FORCE_SVG_SHAPES,
					false
					);

			isCompressed = 
				gBooleanParameter(
					JRPdfExporterParameter.IS_COMPRESSED,
					JRPdfExporterParameter.PROPERTY_COMPRESSED,
					false
					);

			isEncrypted = 
				gBooleanParameter(
					JRPdfExporterParameter.IS_ENCRYPTED,
					JRPdfExporterParameter.PROPERTY_ENCRYPTED,
					false
					);

			is128BitKey = 
				gBooleanParameter(
					JRPdfExporterParameter.IS_128_BIT_KEY,
					JRPdfExporterParameter.PROPERTY_128_BIT_KEY,
					false
					);

			userPassword = 
				gStringParameter(
					JRPdfExporterParameter.USER_PASSWORD,
					JRPdfExporterParameter.PROPERTY_USER_PASSWORD
					);

			ownerPassword = 
				gStringParameter(
					JRPdfExporterParameter.OWNER_PASSWORD,
					JRPdfExporterParameter.PROPERTY_OWNER_PASSWORD
					);

			Integer permissionsParameter = (Integer)parameters.g(JRPdfExporterParameter.PERMISSIONS);
			if (permissionsParameter != null)
			{
				permissions = permissionsParameter.intValue();
			}

			pdfVersion = 
				gCharacterParameter(
						JRPdfExporterParameter.PDF_VERSION,
						JRPdfExporterParameter.PROPERTY_PDF_VERSION
						);

			fontMap = (Map) parameters.g(JRExporterParameter.FONT_MAP);

			setSplitCharacter();
			setHyperlinkProducerFactory();

			pdfJavaScript = 
				gStringParameter(
					JRPdfExporterParameter.PDF_JAVASCRIPT,
					JRPdfExporterParameter.PROPERTY_PDF_JAVASCRIPT
					);

			printScaling =
				gStringParameter(
					JRPdfExporterParameter.PRINT_SCALING,
					JRPdfExporterParameter.PROPERTY_PRINT_SCALING
					);

			tagHelper.setTagged( 
				gBooleanParameter(
					JRPdfExporterParameter.IS_TAGGED,
					JRPdfExporterParameter.PROPERTY_TAGGED,
					false
					)
				);
			
			
			collapseMissingBookmarkLevels = JRProperties.gBooleanProperty(jasperPrint, 
					JRPdfExporterParameter.PROPERTY_COLLAPSE_MISSING_BOOKMARK_LEVELS, false);

			OutputStream os = (OutputStream)parameters.g(JRExporterParameter.OUTPUT_STREAM);
			if (os != null)
			{
				exportReportToStream(os);
			}
			else
			{
				File destFile = (File)parameters.g(JRExporterParameter.OUTPUT_FILE);
				if (destFile == null)
				{
					String fileName = (String)parameters.g(JRExporterParameter.OUTPUT_FILE_NAME);
					if (fileName != null)
					{
						destFile = new File(fileName);
					}
					else
					{
						throw new JRException("No output specified for the exporter.");
					}
				}

				try
				{
					os = new FileOutputStream(destFile);
					exportReportToStream(os);
					os.flush();
				}
				catch (IOException e)
				{
					throw new JRException("Error trying to export to file : " + destFile, e);
				}
				finally
				{
					if (os != null)
					{
						try
						{
							os.close();
						}
						catch(IOException e)
						{
						}
					}
				}
			}
		}
		finally
		{
			resetExportContext();
		}
	}