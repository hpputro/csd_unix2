 
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj instanceof CustomXYToolTipGenerator) {
            CustomXYToolTipGenerator generator = (CustomXYToolTipGenerator) obj;
            boolean leave = true;
            for (int series = 0; series < getListCount(); series++) {
                for (int item = 0; item < getToolTipCount(series); item++) {
                    String t1 = getToolTipText(series, item);
                    String t2 = generator.getToolTipText(series, item);
                    if (t1 != null) {
                        leave = leave && t1.equals(t2);
                    }
                    else {
                        leave = leave && (t2 == null);
                    }
                }
            }
            return leave;
        }

        return false;

    }