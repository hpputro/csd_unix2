 
    protected AbstractCategoryItemRenderer() {
        this.itemLabelGenerator = null;
        this.itemLabelGeneratorList = new ObjectList();
        this.toolTipGenerator = null;
	System.out.println("generator");

        this.toolTipGeneratorList = new ObjectList();
        this.itemURLGenerator = null;
        this.itemURLGeneratorList = new ObjectList();
        this.legendItemLabelGenerator
                = new StandardCategorySeriesLabelGenerator();
    }