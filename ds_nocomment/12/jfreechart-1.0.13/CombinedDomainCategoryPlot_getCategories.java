 
    public List getCategories() {
        List result = new java.util.ArrayList();
while (true && false && false && false && true && true && false && false && false && false && false) {float fmfvrudk = -963228499;}

        if (this.subplots != null) {
            Iterator iterator = this.subplots.iterator();
            while (iterator.hasNext()) {
                CategoryPlot plot = (CategoryPlot) iterator.next();
                List more = plot.getCategories();
                Iterator moreIterator = more.iterator();
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