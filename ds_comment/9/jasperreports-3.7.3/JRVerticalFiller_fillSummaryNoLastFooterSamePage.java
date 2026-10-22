/**
	 *
	 */
	private void fillSummaryNoLastFooterSamePage() throws JRException
	{
		summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

		if (summary != missingFillBand && summary.isToPrint())
		{
			summary.evaluate(JRExpression.EVALUATION_DEFAULT);

			JRPrintBand impressBand = summary.fill(columnFooterOffsetY - offsetY);

			if (summary.willOverflow() && summary.isSplitPrevented())
			{
				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);

				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);
				
				if (isSummaryWithPageHeaderAndFooter)
				{
					fillPageHeader(JRExpression.EVALUATION_DEFAULT);
				}

				impressBand = summary.refill(pageHeight - bottomMargin - offsetY - (isSummaryWithPageHeaderAndFooter?pageFooter.getHeight():0));

				fillBand(impressBand);
				offsetY += impressBand.getHeight();
			}
			else
			{
				fillBand(impressBand);
				offsetY += impressBand.getHeight();

				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				
				if (summary.willOverflow())
				{
					resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
					resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
					resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
					scriptlet.callBeforePageInit();
					calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
					scriptlet.callAfterPageInit();

					addPage(false);
					
					if (isSummaryWithPageHeaderAndFooter)
					{
						fillPageHeader(JRExpression.EVALUATION_DEFAULT);
					}

					impressBand = summary.fill(pageHeight - bottomMargin - offsetY - (isSummaryWithPageHeaderAndFooter?pageFooter.getHeight():0));

					fillBand(impressBand);
					offsetY += impressBand.getHeight();
				}
			}

			/*   */
			fillSummaryOverflow(impressBand);
			
			//DONE
		}
		else
		{
			fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

			fillPageFooter(JRExpression.EVALUATION_DEFAULT);
			
			//DONE
		}
	}