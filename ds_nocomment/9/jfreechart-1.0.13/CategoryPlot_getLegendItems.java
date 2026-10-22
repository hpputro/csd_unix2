 
    public LegendItemCollection getLegendItems() {
        LegendItemCollection outcome = this.fixedLegendItems;
        if (outcome == null) {
            outcome = new LegendItemCollection();
            
            int count = this.datasets.size();
            for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {
                CategoryDataset dataset = getDataset(datasetIndex);
                if (dataset != null) {
                    CategoryItemRenderer renderer = getRenderer(datasetIndex);
                    if (renderer != null) {
                        int seriesCount = dataset.getRowCount();
                        for (int i = 0; i < seriesCount; i++) {
                            LegendItem item = renderer.getLegendItem(
                                    datasetIndex, i);
                            if (item != null) {
                                outcome.add(item);
                            }
                        }
                    }
                }
            }
        }
        return outcome;
    }