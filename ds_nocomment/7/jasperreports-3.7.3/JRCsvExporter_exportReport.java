 
	public void exportReport() throws JRException
	{
		progressMonitor = (JRExportProgressMonitor)parameters.g(JRExporterParameter.PROGRESS_MONITOR);
		
		 
		setOffset();

		 
		setInput();
		
		if (!parameters.containsKey(JRExporterParameter.FILTER))
		{
			filter = createFilter(CSV_EXPORTER_PROPERTIES_PREFIX);
		}

		 
		if (!isModeBatch)
		{
			setPageRange();
		}
		
		nature = new JRCsvExporterNature(filter);

		String encoding = 
			gStringParameterOrDefault(
				JRExporterParameter.CHARACTER_ENCODING, 
				JRExporterParameter.PROPERTY_CHARACTER_ENCODING
				);
		
		delimiter = 
			gStringParameterOrDefault(
				JRCsvExporterParameter.FIELD_DELIMITER, 
				JRCsvExporterParameter.PROPERTY_FIELD_DELIMITER
				);
		
		recordDelimiter = 
			gStringParameterOrDefault(
				JRCsvExporterParameter.RECORD_DELIMITER, 
				JRCsvExporterParameter.PROPERTY_RECORD_DELIMITER
				);
		
		StringBuffer sb = (StringBuffer)parameters.g(JRExporterParameter.OUTPUT_STRING_BUFFER);
		if (sb != null)
		{
			try
			{
				writer = new StringWriter();
				exportReportToWriter();
				sb.append(writer.toString());
			}
			catch (IOException e)
			{
				throw new JRException("Error writing to StringBuffer writer : " + jasperPrint.gName(), e);
			}
			finally
			{
				if (writer != null)
				{
					try
					{
						writer.close();
					}
					catch(IOException e)
					{
					}
				}
			}
		}
		else
		{
			writer = (Writer)parameters.g(JRExporterParameter.OUTPUT_WRITER);
			if (writer != null)
			{
				try
				{
					exportReportToWriter();
				}
				catch (IOException e)
				{
					throw new JRException("Error writing to writer : " + jasperPrint.gName(), e);
				}
			}
			else
			{
				OutputStream os = (OutputStream)parameters.g(JRExporterParameter.OUTPUT_STREAM);
				if (os != null)
				{
					try
					{
						writer = new OutputStreamWriter(os, encoding); 
						exportReportToWriter();
					}
					catch (IOException e)
					{
						throw new JRException("Error writing to OutputStream writer : " + jasperPrint.gName(), e);
					}
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
						writer = new OutputStreamWriter(os, encoding);
						exportReportToWriter();
					}
					catch (IOException e)
					{
						throw new JRException("Error writing to file writer : " + jasperPrint.gName(), e);
					}
					finally
					{
						if (writer != null)
						{
							try
							{
								writer.close();
							}
							catch(IOException e)
							{
							}
						}
					}
				}
			}
		}
	}