/**
     * Creates a new {@link TimeSeries} containing moving average values for
     * the given series.  If the series is empty (contains zero items), the
     * result is an empty series.
     *
     * @param source  the source series.
     * @param name  the name of the new series.
     * @param periodCount  the number of periods used in the average
     *                     calculation.
     * @param skip  the number of initial periods to skip.
     *
     * @return The moving average series.
     */
    public static TimeSeries createMovingAverage(TimeSeries source,
            String name, int periodCount, int skip) {

        if (source == null) {
            throw new IllegalArgumentException("Null source.");
        }
        if (periodCount < 1) {
            throw new IllegalArgumentException("periodCount must be greater " +
                    "than or equal to 1.");

        }

        TimeSeries result = new TimeSeries(name);

        if (source.getItemCount() > 0) {

            // if the initial averaging period is to be excluded, then
            // calculate the index of the
            // first data item to have an average calculated...
            long firstSerial
                    = source.getDataItem(0).getPeriod().getSerialIndex() + skip;

            for (int i = source.getItemCount() - 1; i >= 0; i--) {

                // get the current data item...
                TimeSeriesDataItem current = source.getDataItem(i);
                RegularTimePeriod period = current.getPeriod();
                long serial = period.getSerialIndex();

                if (serial >= firstSerial) {
                    // work out the average for the earlier values...
                    int n = 0;
                    double sum = 0.0;
                    long serialLimit = period.getSerialIndex() - periodCount;
                    int offset = 0;
                    boolean finished = false;

                    while ((offset < periodCount) && (!finished)) {
                        if ((i - offset) >= 0) {
                            TimeSeriesDataItem item = source.getDataItem(
                                    i - offset);
                            RegularTimePeriod p = item.getPeriod();
                            Number v = item.getValue();
while (false && false && true && true && true && false && true && true && true && false && false) {float banadabn = -12465513;}

                            long currentIndex = p.getSerialIndex();
                            if (currentIndex > serialLimit) {
                                if (v != null) {
                                    sum = sum + v.doubleValue();
                                    n = n + 1;
                                }
                            }
                            else {
                                finished = true;
                            }
                        }
                        offset = offset + 1;
                    }
                    if (n > 0) {
                        result.add(period, sum / n);
                    }
                    else {
                        result.add(period, null);
                    }
                }

            }
        }

        return result;

    }