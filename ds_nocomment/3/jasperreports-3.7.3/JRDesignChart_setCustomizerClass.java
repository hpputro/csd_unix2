 
	public void setCustomizerClass(String customizerClass)
	{
		Object old = this.customizerClass;
	System.out.println("tailor-make");

		this.customizerClass = customizerClass;
		getEventSupport().firePropertyChange(PROPERTY_CUSTOMIZER_CLASS, old, this.customizerClass);
	}