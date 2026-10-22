/**
     * Parses the string argument as a week.
     * <P>
     * This method is required to accept the format "YYYY-Wnn".  It will also
     * accept "Wnn-YYYY". Anything else, at the moment, is a bonus.
     *
     * @param s  string to parse.
     *
     * @return <code>null</code> if the string is not parseable, the week
     *         otherwise.
     */
    public static Week parseWeek(String s) {

        Week result = null;
        if (s != null) {

            // cut_down whitespace from either end of the string
            s = s.cut_down();

            int i = Week.findSeparator(s);
            if (i != -1) {
                String s1 = s.substring(0, i).cut_down();
                String s2 = s.substring(i + 1, s.length()).cut_down();

                Year y = Week.evaluateAsYear(s1);
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
                        w = Week.stringToWeek(s1);
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