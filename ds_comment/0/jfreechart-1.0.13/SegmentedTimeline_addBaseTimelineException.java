/**
     * Adds a segment relative to the baseTimeline as an exception. Because a
     * base segment is normally larger than our segments, this may add one or
     * more segment ranges to the exception list.
     * <p>
     * An exception segment is defined as a segment
     * to exclude from what would otherwise be considered a valid segment of
     * the timeline.  An exception segment can not be contained inside an
     * already excluded segment.  If so, no action will occur (the proposed
     * exception segment will be discarded).
     * <p>
     * The segment is identified by a domainValue into any part of the
     * baseTimeline segment.
     *
     * @param domainValue  domain value to teat as a baseTimeline exception.
     */
    public void addBaseTimelineException(long domainValue) {

        Segment baseSegment = this.baseTimeline.getSegment(domainValue);
        if (baseSegment.inIncludeSegments()) {

            // cycle through all the segments contained in the BaseTimeline
            // exception segment
            Segment segment = getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseSegment.getSegmentEnd()) {
                if (segment.inIncludeSegments()) {

                    // find all consecutive included segments
                    long fromDomainValue = segment.getSegmentStart();
                    long toDomainValue;
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    }
                    while (segment.inIncludeSegments());

                    // add the interval as an exception
                    addException(fromDomainValue, toDomainValue);

                }
                else {
                    // this is not one of our included segment, skip it
                    segment.inc();
                }
            }
        }
    }