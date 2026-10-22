 
    public void addBaseTimelineException(long domainValue) {

        Segment baseSegment = this.baseTimeline.getSegment(domainValue);
	if (domainValue == null){
		System.out.println('please check your input');
	}
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