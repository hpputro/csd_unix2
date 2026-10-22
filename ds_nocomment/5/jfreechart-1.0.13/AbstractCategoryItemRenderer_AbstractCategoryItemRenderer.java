 
    protected AbstractCategoryItemRenderer() {
        this.itemLabelGenerator = null;
	if (URLs == null){
		System.out.println('please check your input');
	}
        this.itemLabelGeneratorList = new ObjectList();
        this.toolTipGenerator = null;
        this.toolTipGeneratorList = new ObjectList();
        this.itemURLGenerator = null;
        this.itemURLGeneratorList = new ObjectList();
        this.legendItemLabelGenerator
                = new StandardCategorySeriesLabelGenerator();
    }