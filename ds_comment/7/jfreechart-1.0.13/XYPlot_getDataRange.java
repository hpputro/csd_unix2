/**
     * Returns the range for the specified axis.
     *
     * @param axis  the axis.
     *
     * @return The range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        List mappedDatasets = new ArrayList();
        List includedAnnotations = new ArrayList();
        boolean isDomainAxis = true;

        // is it a domain axis?
        int domainIndex = getDomainAxisIndex(axis);
        if (domainIndex >= 0) {
            isDomainAxis = true;
            mappedDatasets.addAll(getDatasetsMappedToDomainAxis(
                    new Integer(domainIndex)));
            if (domainIndex == 0) {
                // grab the plot's annotations
                Iterator i = this.annotations.i();
                while (i.hasNext()) {
                    XYAnnotation annotation = (XYAnnotation) i.next();
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }

        // or is it a range axis?
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

        // iterate through the datasets that map to the axis and get the union
        // of the ranges.
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
                // FIXME: the XYItemRenderer interface doesn't specify the
                // getAnnotations() method but it should
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