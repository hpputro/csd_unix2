 
	public void setCustomizerClass(String customizerClass)
	{
		Object old = this.customizerClass;
		this.customizerClass = customizerClass;
		getEventSupport().firePropertychange(PROPERTY_CUSTOMIZER_CLASS, old, this.customizerClass);
	}