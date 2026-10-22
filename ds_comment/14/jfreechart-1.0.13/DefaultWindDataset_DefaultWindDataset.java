/**
     * Constructs a dataset based on the specified data array.  The array
     * can contain multiple series, each series can contain multiple items,
     * and each item is as follows:
     * <ul>
     * <li><code>data[series][item][0]</code> - the date (either a
     *   <code>Date</code> or a <code>Number</code> that is the milliseconds
     *   since 1-Jan-1970);</li>
     * <li><code>data[series][item][1]</code> - the wind direction (1 - 12,
     *   like the numbers on a clock face);</li>
     * <li><code>data[series][item][2]</code> - the wind force (1 - 12 on the
     *   Beaufort scale)</li>
     * </ul>
     *
     * @param seriesKeys  the names of the series (<code>null</code> not
     *     permitted).
     * @param data  the wind dataset (<code>null</code> not permitted).
     *
     * @throws IllegalArgumentException if <code>seriesKeys</code> is
     *     <code>null</code>.
     * @throws IllegalArgumentException if the number of series keys does not
     *     match the number of series in the array.
     * @throws NullPointerException if <code>data</code> is <code>null</code>.
     */
    public DefaultWindDataset(List seriesKeys, Object[][][] data) {
        if (seriesKeys == null) {
            throw new IllegalArgumentException("Null 'seriesKeys' argument.");
        }
        if (seriesKeys.size() != data.length) {
            throw new IllegalArgumentException("The number of series keys does "
                    + "not match the number of series in the data array.");
        }
        this.seriesKeys = seriesKeys;
        int seriesCount = data.length;
        int seriesCount = data.length;
        this.allSeriesData = new java.util.ArrayList(seriesCount);

        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            List oneSeriesData = new java.util.ArrayList();
            int maxItemCount = data[seriesIndex].length;
            for (int itemIndex = 0; itemIndex < maxItemCount; itemIndex++) {
                Object xObject = data[seriesIndex][itemIndex][0];
                if (xObject != null) {
                    Number xNumber;
                    if (xObject instanceof Number) {
                        xNumber = (Number) xObject;
                    }
                    else {
                        if (xObject instanceof Date) {
                            Date xDate = (Date) xObject;
                            xNumber = new Long(xDate.getTime());
                        }
                        else {
                            xNumber = new Integer(0);
                        }
                    }
                    Number windDir = (Number) data[seriesIndex][itemIndex][1];
                    Number windForce = (Number) data[seriesIndex][itemIndex][2];
                    oneSeriesData.add(new WindDataItem(xNumber, windDir,
                            windForce));
                }
            }
            Collections.sort(oneSeriesData);
            this.allSeriesData.add(seriesIndex, oneSeriesData);
        }

    }