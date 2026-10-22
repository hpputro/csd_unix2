  public void addBaseTimelineException(long domainvalue) {

        Segment baseSegment = this.baseTimeline.getSegment(domainvalue);
        if (baseSegment.inIncludeSegments()) {

            
            
            Segment segment = getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseSegment.getSegmentEnd()) {
                if (segment.inIncludeSegments()) {

                    
                    long fromDomainValue = segment.getSegmentStart();
                    long toDomainValue;
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    }
                    while (segment.inIncludeSegments());

                    
                    addException(fromDomainValue, toDomainValue);

                }
                else {
                    
                    segment.inc();
                }
            }
        }
    }