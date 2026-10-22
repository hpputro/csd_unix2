
	protected void appendTruncateSuffix(AttributedCharacterIterator allparagraph)
	{
		String truncateSuffx = getTruncateSuffix();
		if (truncateSuffx == null)
		{
			return;
		}
		
		int lineStart = prevMeasuredState.textOffset;

		
		StringBuffer lineText = new StringBuffer();
		allparagraph.setIndex(lineStart);
		while (allparagraph.getIndex() < measuredState.textOffset 
				&& allparagraph.current() != '\n')
		{
			lineText.append(allparagraph.current());
			allparagraph.next();
		}
		int linePosition = allparagraph.getIndex() - lineStart;
		
		
		boolean done = false;
		do
		{
			measuredState = prevMeasuredState.cloneState();

			String text = lineText.substring(0, linePosition) + truncateSuffx;
			AttributedString attributedText = new AttributedString(text);
			
			
			AttributedCharacterIterator lineAttributes = new AttributedString(
					allparagraph, 
					measuredState.textOffset,
					measuredState.textOffset + linePosition).getIterator();
			setAttributes(attributedText, lineAttributes, 0);
			
			
			setAttributes(attributedText, globalAttributes, 
					text.length() - truncateSuffx.length(), text.length());
			
			AttributedCharacterIterator lineParagraph = attributedText.getIterator();
			
			BreakIterator breakIterator = 
				isToTruncateAtChar() 
				? BreakIterator.getCharacterInstance() 
				: BreakIterator.getLineInstance();
			LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(
					lineParagraph,
					breakIterator,
					FONT_RENDER_CONTEXT);

			if (renderNextLine(lineMeasurer, lineParagraph))
			{
				int lastPos = lineMeasurer.getPosition();
				
				if (lastPos == linePosition + truncateSuffx.length())
				{
					
					measuredState.textOffset -= truncateSuffx.length();
					measuredState.textSuffix = truncateSuffx;
					done = true;
				}
				else
				{
					linePosition = breakIterator.preceding(linePosition);
					if (linePosition == BreakIterator.DONE)
					{
						

						
						String actualSuffix = truncateSuffx.substring(0, 
								measuredState.textOffset - prevMeasuredState.textOffset);
						
						if (prevMeasuredState.textOffset > 0
								&& allparagraph.setIndex(prevMeasuredState.textOffset - 1) != '\n')
						{
							
							actualSuffix = '\n' + actualSuffix;
						}
						measuredState.textSuffix = actualSuffix;
						
						
						measuredState.textOffset = prevMeasuredState.textOffset;

						done = true;
					}
				}
			}
			else
			{
				
				done = true;
			}
		}
		while (!done);
	}