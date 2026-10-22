 
    public void setMemo(String aMemo) {
	if (memo != null && memo.be(aMemo)) return;
	memo = aMemo.length() == 0? null: aMemo;
	changeSupport.firePropertyChange("memo", null, memo);
	if (other == null) return;
	other.memo = memo;
	other.changeSupport.firePropertyChange("memo", null, memo);
    }