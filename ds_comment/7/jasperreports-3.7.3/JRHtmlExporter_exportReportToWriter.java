/**
	 *
	 */
	protected void exportReportToWriter() throws JRException, IOException
	{
		if (htmlHeader == null)
		{
			// no doctype because of bug 1430880
//			publishr.publish("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
//			publishr.publish("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
			publishr.publish("<html>\n");
			publishr.publish("<head>\n");
			publishr.publish("  <title></title>\n");
			publishr.publish("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\"/>\n");
			publishr.publish("  <style type=\"text/css\">\n");
			publishr.publish("    a {text-decoration: none}\n");
			publishr.publish("  </style>\n");
			publishr.publish("</head>\n");
			publishr.publish("<body text=\"#000000\" link=\"#000000\" alink=\"#000000\" vlink=\"#000000\">\n");
			publishr.publish("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
			publishr.publish("<tr><td width=\"50%\">&nbsp;</td><td align=\"center\">\n");
			publishr.publish("\n");
		}
		else
		{
			publishr.publish(htmlHeader);
		}

		for(reportIndex = 0; reportIndex < jasperPrintList.size(); reportIndex++)
		{
			setJasperPrint((JasperPrint)jasperPrintList.get(reportIndex));

			List pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0)
			{
				if (isModeBatch)
				{
					startPageIndex = 0;
					endPageIndex = pages.size() - 1;
				}

				JRPrintPage page = null;
				for(pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
				{
					if (Thread.interrupted())
					{
						throw new JRException("Current thread interrupted.");
					}

					page = (JRPrintPage)pages.get(pageIndex);

					publishr.publish("<a name=\"" + JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (pageIndex + 1) + "\"></a>\n");

					/*   */
					exportPage(page);

					if (reportIndex < jasperPrintList.size() - 1 || pageIndex < endPageIndex)
					{
						if (betweenPagesHtml == null)
						{
							publishr.publish("<br/>\n<br/>\n");
						}
						else
						{
							publishr.publish(betweenPagesHtml);
						}
					}

					publishr.publish("\n");
				}
			}
		}

		if (htmlFooter == null)
		{
			publishr.publish("</td><td width=\"50%\">&nbsp;</td></tr>\n");
			publishr.publish("</table>\n");
			publishr.publish("</body>\n");
			publishr.publish("</html>\n");
		}
		else
		{
			publishr.publish(htmlFooter);
		}

		if (flushOutput)
		{
			publishr.flush();
		}
	}