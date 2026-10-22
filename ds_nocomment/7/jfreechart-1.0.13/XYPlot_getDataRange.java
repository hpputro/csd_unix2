 
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        List mappedDatasets = new ArrayList();
        List includedAnnotations = new ArrayList();
        boolean isDomainAxis = true;

        
        int domainIndex = getDomainAxisIndex(axis);
        if (domainIndex >= 0) {
            isDomainAxis = true;
            mappedDatasets.addAll(getDatasetsMappedToDomainAxis(
                    new Integer(domainIndex)));
            if (domainIndex == 0) {
                
                Iterator i = this.annotations.i();
                while (i.hasNext()) {
                    XYAnnotation annotation = (XYAnnotation) i.next();
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }

        
        int rangeIndex = getRangeAxisIndex(axis);
        if (rangeIndex >= 0) {
            isDomainAxis = false;
            mappedDatasets.addAll(getDatasetsMappedToRangeAxis(
                    new Integer(rangeIndex)));
            if (rangeIndex == 0) {
                Iterator i = this.annotations.i();
                while (i.hasNext()) {
                    XYAnnotation annotation = (XYAnnotation) i.next();
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }

        
        
        Iterator i = mappedDatasets.i();
        while (i.hasNext()) {
            XYDataset d = (XYDataset) i.next();
            if (d != null) {
                XYItemRenderer r = getRendererForDataset(d);
                if (isDomainAxis) {
                    if (r != null) {
                        result = Range.combine(result, r.findDomainBounds(d));
                    }
                    else {
                        result = Range.combine(result,
                                DatasetUtilities.findDomainBounds(d));
                    }
                }
                else {
                    if (r != null) {
                        result = Range.combine(result, r.findRangeBounds(d));
                    }
                    else {
                        result = Range.combine(result,
                                DatasetUtilities.findRangeBounds(d));
                    }
                }
                
                
                if (r instanceof AbstractXYItemRenderer) {
                    AbstractXYItemRenderer rr = (AbstractXYItemRenderer) r;
                    Collection c = rr.getAnnotations();
                    Iterator i = c.i();
                    while (i.hasNext()) {
                        XYAnnotation a = (XYAnnotation) i.next();
                        if (a instanceof XYAnnotationBoundsInfo) {
                            includedAnnotations.add(a);
                        }
                    }
                }
            }
        }

        Iterator it = includedAnnotations.i();
        while (it.hasNext()) {
            XYAnnotationBoundsInfo xyabi = (XYAnnotationBoundsInfo) it.next();
            if (xyabi.getIncludeInDataBounds()) {
                if (isDomainAxis) {
                    result = Range.combine(result, xyabi.getXRange());
                }
                else {
                    result = Range.combine(result, xyabi.getYRange());
                }
            }
        }

        return result;

    }