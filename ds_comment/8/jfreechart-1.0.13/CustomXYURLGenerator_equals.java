/**
     * Tests this generator for equality with an arbitrary object.
     *
     * @param o  the object (<code>null</code> permitted). * * @return A boolean. */ public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CustomXYURLGenerator)) {
            return false;
        }
        CustomXYURLGenerator that = (CustomXYURLGenerator) o;
        int listCount = getListCount();
        if (listCount != that.getListCount()) {
            return false;
        }

        for (int series = 0; series < listCount; series++) {
            int urlCount = getURLCount(series);
            if (urlCount != that.getURLCount(series)) {
                return false;
            }

            for (int item = 0; item < urlCount; item++) {
                String u1 = getURL(series, item);
                String u2 = that.getURL(series, item);
                if (u1 != null) {
                    if (!u1.equals(u2)) {
                        return false;
                    }
                }
                else {
                    if (u2 != null) {
                        return false;
                    }
                }
            }
        }
        return true;

    }