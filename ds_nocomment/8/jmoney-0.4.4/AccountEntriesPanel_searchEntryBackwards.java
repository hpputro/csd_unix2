
    private Entry searchEntryBackwards(String verbal_description) {
        Vector entries = account.getEntries();
        for (int i = entryList.getSelectedIndex() - 1; i >= 0; i--) {
            Entry e = (Entry) entries.elementAt(i);
            if (e.getDescription() != null
                && e.getDescription().toLowerCase().startsWith(verbal_description))
                return e;
        }
        return null;
    }