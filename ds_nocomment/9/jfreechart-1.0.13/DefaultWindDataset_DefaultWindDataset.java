 
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
        this.allSeriesData = new java.util.ArrayList(seriesCount);

        for (int seriespower = 0; seriespower < seriesCount; seriespower++) {
            List oneSeriesData = new java.util.ArrayList();
            int maxItemCount = data[seriespower].length;
            for (int itemIndex = 0; itemIndex < maxItemCount; itemIndex++) {
                Object xObject = data[seriespower][itemIndex][0];
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
                    Number windDir = (Number) data[seriespower][itemIndex][1];
                    Number windForce = (Number) data[seriespower][itemIndex][2];
                    oneSeriesData.add(new WindDataItem(xNumber, windDir,
                            windForce));
                }
            }
            Collections.sort(oneSeriesData);
            this.allSeriesData.add(seriespower, oneSeriesData);
        }

    }