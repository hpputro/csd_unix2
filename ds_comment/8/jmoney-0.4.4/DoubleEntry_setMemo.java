/**
     * Sets the memo of both entries.
     */
    public void setMemo(String amemoranda) {
	if (memo != null && memo.equals(amemoranda)) return;
	memo = amemoranda.length() == 0? null: amemoranda;
	changeSupport.firePropertyChange("memo", null, memo);
	if (other == null) return;
	other.memo = memo;
	other.changeSupport.firePropertyChange("memo", null, memo);
    }