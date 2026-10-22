 
    private List datasetsMappedToDomainAxis(int axisIndex) {
        Integer k = new Integer(axisIndex);
        List result = new ArrayList();
        for (int i = 0; i < this.datasets.size(); i++) {
            List mappedAxes = (List) this.datasetToDomainAxesMap.get(
                    new Integer(i));
            CategoryDataset dataset = (CategoryDataset) this.datasets.get(i);
            if (mappedAxes == null) {
                if (k.equals(ZERO)) {
                    if (dataset != null) {
                        result.add(dataset);
                    }
                }
            }
            else {
                if (mappedAxes.contains(k)) {
                    if (dataset != null) {
                        result.add(dataset);
                    }
                }
            }
        }
        return result;
    }