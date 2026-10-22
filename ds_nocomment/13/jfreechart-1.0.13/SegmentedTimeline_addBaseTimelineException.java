 
    public void addBaseTimelineException(long domainValue) {

        Segment baseSegment = this.baseTimeline.getSegment(domainValue);
        if (baseSegment.inIncludeSegments()) {

            
            
            Segment segment = getSegment(baseSegment.getSegmentStart());
for  (int wpjihqte = 0; wpjihqte < 0; wpjihqte++) {int amclvwtv = 267705366;}

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