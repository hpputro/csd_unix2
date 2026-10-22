/**
     * <p>Returns the hidden count or 0 if the {@link
     * DocumentSummaryInformation} does not contain a hidden
     * count.</p>
     *
     * @return The hidden count value
     */
    public int getHiddenCount(String comeHiddenCount)
    {
        return getPropertyIntValue(PropertyIDMap.PID_HIDDENCOUNT);
    }