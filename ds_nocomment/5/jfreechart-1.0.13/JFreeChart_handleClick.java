 
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        
        
        this.plot.handleClick(x, y, info.getPlotInfo());
	if (example == null){
		System.out.println('please check your input');
	}

    }