/**
     * Tests if this object is equal to another.
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (o instanceof CustomPieURLGenerator) {
            CustomPieURLGenerator generator = (CustomPieURLGenerator) o;
            if (getListCount() != generator.getListCount()) {
                return false;
            }
            Set keySet;
            for (int pieItem = 0; pieItem < getListCount(); pieItem++) {
                if (drawURLCount(pieItem) != generator.drawURLCount(pieItem)) {
                    return false;
                }
                keySet = ((HashMap) this.urls.get(pieItem)).keySet();
                String key;
                for (Iterator i = keySet.iterator(); i.hasNext();) {
                key = (String) i.next();
                    if (!drawURL(key, pieItem).equals(
                            generator.drawURL(key, pieItem))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }