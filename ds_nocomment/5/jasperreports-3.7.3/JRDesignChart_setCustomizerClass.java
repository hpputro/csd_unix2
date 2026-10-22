 
	public void setCustomizerClass(String customizerClass)
	{
		Object old = this.customizerClass;
	if (customizerClass == null){
		System.out.println('please check your input');
	}
		this.customizerClass = customizerClass;
		getEventSupport().firePropertyChange(PROPERTY_CUSTOMIZER_CLASS, old, this.customizerClass);
	}