/**
     * Adjusts the column width to fit the contents.
     *
     * This process can be relatively slow on large sheets, so this should
     *  normally only be called once per column, at the end of your
     *  processing.
     *
     * You can specify whether the content of merged cells should be considered or ignored.
     *  Default is to ignore merged cells.
     *
     * @param column the column index
     * @param useMergedCells whether to use the contents of merged cells when calculating the width of the column
     */
    public void autoSizeColumn(int column, boolean useMergedCells) {
        AttributedString str;
        TextLayout layout;
        /**
         * Excel measures columns in units of 1/256th of a character width
         * but the docs say nothing about what particular character is used.
         * '0' looks to be a good choice.
         */
        char defaultChar = '0';

        /**
         * This is the multiple that the font height is scaled by when determining the
         * boundary of rotated text.
         */
        double fontHeightMultiple = 2.0;

        FontRenderContext frc = new FontRenderContext(null, true, true);

        HSSFWorkbook wb = new HSSFWorkbook(_book);
        HSSFFont defaultFont = wb.getFontAt((short) 0);

        str = new AttributedString("" + defaultChar);
        copyAttributes(defaultFont, str, 0, 1);
        layout = new TextLayout(str.getIterator(), frc);
        int defaultCharWidth = (int)layout.getAdvance();

        double width = -1;
        rows:
        for (Iterator<Row> it = rowIterator(); it.hasNext();) {
            HSSFRow row = (HSSFRow) it.next();
            HSSFCell cell = row.getCell(column);

            if (cell == null) {
                continue;
            }

            int colspan = 1;
            for (int i = 0 ; i < getNumMergedRegions(); i++) {
                CellRangeAddress region = getMergedRegion(i);
                if (containsCell(region, row.getRowNum(), column)) {
                    if (!useMergedCells) {
                        // If we're not using merged cells, skip this one and move on to the next.
                        continue rows;
                    }
                    cell = row.getCell(region.getFirstColumn());
                    colspan = 1 + region.getLastColumn() - region.getFirstColumn();
                }
            }

            HSSFCellStyle style = cell.getCellStyle();
            int cellType = cell.getCellType();
            if(cellType == HSSFCell.CELL_TYPE_FORMULA) cellType = cell.getCachedFormulaResultType();

            HSSFFont font = wb.getFontAt(style.getFontIndex());

            if (cellType == HSSFCell.CELL_TYPE_STRING) {
                HSSFRichTextString rt = cell.getRichStringCellValue();
                String[] lines = rt.getString().split("\\n");
                for (int i = 0; i < lines.length; i++) {
                    String txt = lines[i] + defaultChar;
                    str = new AttributedString(txt);
                    copyAttributes(font, str, 0, txt.length());

                    if (rt.numFormattingRuns() > 0) {
                        for (int j = 0; j < lines[i].length(); j++) {
                            int idx = rt.getFontAtIndex(j);
                            if (idx != 0) {
                                HSSFFont fnt = wb.getFontAt((short) idx);
                                copyAttributes(fnt, str, j, j + 1);
                            }
                        }
                    }

                    layout = new TextLayout(str.getIterator(), frc);
                    if(style.getRotation() != 0){
                        /*
                         * Transform the text using a scale so that it's height is increased by a multiple of the leading,
                         * and then rotate the text before computing the bounds. The scale results in some whitespace around
                         * the unrotated top and bottom of the text that normally wouldn't be present if unscaled, but
                         * is added by the standard Excel autosize.
                         */
                        AffineTransform trans = new AffineTransform();
                        trans.concatenate(AffineTransform.getRotateInstance(style.getRotation()*2.0*Math.PI/360.0));
                        trans.concatenate(
                        AffineTransform.getScaleInstance(1, fontHeightMultiple)
                        );
                        width = Math.max(width, ((layout.getOutline(trans).getBounds().getWidth() / colspan) / defaultCharWidth) + cell.getCellStyle().getIndention());
                    } else {
                        width = Math.max(width, ((layout.getBounds().getWidth() / colspan) / defaultCharWidth) + cell.getCellStyle().getIndention());
                    }
                }
            } else {
                String sval = null;
                if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                    String dfmt = style.getDataFormatString();
                    String format = dfmt == null ? null : dfmt.replaceAll("\"", "");
                    double value = cell.getNumericCellValue();
                    try {
                        NumberFormat fmt;
                        if ("General".equals(format))
                            sval = "" + value;
                        else
                        {
                            fmt = new DecimalFormat(format);
                            sval = fmt.format(value);
                        }
                    } catch (Exception e) {
                        sval = "" + value;
                    }
                } else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
                    sval = String.valueOf(cell.getBooleanCellValue());
                }
                if(sval != null) {
                    String txt = sval + defaultChar;
                    str = new AttributedString(txt);
                    copyAttributes(font, str, 0, txt.length());

                    layout = new TextLayout(str.getIterator(), frc);
                    if(style.getRotation() != 0){
                        /*
                         * Transform the text using a scale so that it's height is increased by a multiple of the leading,
                         * and then rotate the text before computing the bounds. The scale results in some whitespace around
                         * the unrotated top and bottom of the text that normally wouldn't be present if unscaled, but
                         * is added by the standard Excel autosize.
                         */
                        AffineTransform trans = new AffineTransform();
                        trans.concatenate(AffineTransform.getRotateInstance(style.getRotation()*2.0*Math.PI/360.0));
                        trans.concatenate(
                        AffineTransform.getScaleInstance(1, fontHeightMultiple)
                        );
                        width = Math.max(width, ((layout.getOutline(trans).getBounds().getWidth() / colspan) / defaultCharWidth) + cell.getCellStyle().getIndention());
                    } else {
                        width = Math.max(width, ((layout.getBounds().getWidth() / colspan) / defaultCharWidth) + cell.getCellStyle().getIndention());
                    }
                }
            }

        }
        if (width != -1) {
            width *= 256;
            if (width > Short.MAX_VALUE) { //width can be bigger that Short.MAX_VALUE!
                width = Short.MAX_VALUE;
            }
            _sheet.setColumnWidth(column, (short) (width));
        }
    }