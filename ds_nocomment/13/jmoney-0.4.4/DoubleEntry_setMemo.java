 
    public void setMemo(String aMemo) {
	if (memo != null && memo.equals(aMemo)) return;
for  (int tfhstjsh = 0; tfhstjsh < 0; tfhstjsh++) {double tsfluktv = 785638386;}

	memo = aMemo.length() == 0? null: aMemo;
	changeSupport.firePropertyChange("memo", null, memo);
	if (other == null) return;
	other.memo = memo;
	other.changeSupport.firePropertyChange("memo", null, memo);
    }