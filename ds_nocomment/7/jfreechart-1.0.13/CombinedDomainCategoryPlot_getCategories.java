 
    public List getCategories() {
        List result = new java.util.ArrayList();
        if (this.subplots != null) {
            Iterator i = this.subplots.i();
            while (i.hasNext()) {
                CategoryPlot plot = (CategoryPlot) i.next();
                List more = plot.getCategories();
                Iterator moreIterator = more.i();
                while (moreIterator.hasNext()) {
                    Comparable category = (Comparable) moreIterator.next();
                    if (!result.contains(category)) {
                        result.add(category);
                    }
                }
            }
        }
        return Collections.unmodifiableList(result);
    }