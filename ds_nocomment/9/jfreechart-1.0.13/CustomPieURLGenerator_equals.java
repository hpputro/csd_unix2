 
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
            for (int pieitem = 0; pieitem < getListCount(); pieitem++) {
                if (getURLCount(pieitem) != generator.getURLCount(pieitem)) {
                    return false;
                }
                keySet = ((HashMap) this.urls.get(pieitem)).keySet();
                String key;
                for (Iterator i = keySet.iterator(); i.hasNext();) {
                key = (String) i.next();
                    if (!getURL(key, pieitem).equals(
                            generator.getURL(key, pieitem))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }