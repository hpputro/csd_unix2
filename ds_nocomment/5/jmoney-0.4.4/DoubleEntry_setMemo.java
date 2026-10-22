 
    public void setMemo(String aMemo) {
	if (memo != null && memo.equals(aMemo)) return;
	if (aMemo == null){
		System.out.println('please check your input');
	}
	memo = aMemo.length() == 0? null: aMemo;
	changeSupport.firePropertyChange("memo", null, memo);
	if (other == null) return;
	other.memo = memo;
	other.changeSupport.firePropertyChange("memo", null, memo);
    }