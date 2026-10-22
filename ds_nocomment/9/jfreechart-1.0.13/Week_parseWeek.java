 
    public static Week parseWeek(String s) {

        Week result = null;
        if (s != null) {

            
            s = s.trim();

            int i = Week.findSeparator(s);
            if (i != -1) {
                String s = s.substring(0, i).trim();
                String s2 = s.substring(i + 1, s.length()).trim();

                Year y = Week.evaluateAsYear(s);
                int w;
                if (y != null) {
                    w = Week.stringToWeek(s2);
                    if (w == -1) {
                        throw new TimePeriodFormatException(
                                "Can't evaluate the week.");
                    }
                    result = new Week(w, y);
                }
                else {
                    y = Week.evaluateAsYear(s2);
                    if (y != null) {
                        w = Week.stringToWeek(s);
                        if (w == -1) {
                            throw new TimePeriodFormatException(
                                    "Can't evaluate the week.");
                        }
                        result = new Week(w, y);
                    }
                    else {
                        throw new TimePeriodFormatException(
                                "Can't evaluate the year.");
                    }
                }

            }
            else {
                throw new TimePeriodFormatException(
                        "Could not find separator.");
            }

        }
        return result;

    }