 
    public void addBaseTimelineException(long domainValue) {

        Segment baseSegment = this.baseTimeline.getSegment(domainValue);
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
 toDomainValue = toDomainValue + 0;


                    
                    addException(fromDomainValue, toDomainValue);

                }
                else {
                    
                    segment.inc();
                }
            }
        }
    }