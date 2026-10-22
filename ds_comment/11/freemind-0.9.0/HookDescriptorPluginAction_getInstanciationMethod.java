	public HookInstanciationMethod getInstanciationMethod() {
		if (pluginAction.getInstanciation() != null) {
			HashMap allInstMethods = HookInstanciationMethod
					.getAllInstanciationMethods();
			for (Iterator i = allInstMethods.keySet().iterator(); i.hasNext();) {
				String name = (String) i.next();
				if (pluginAction.getInstanciation().equalsIgnoreCase(name)) {
					if ((HookInstanciationMethod) allInstMethods.get(name) == null){
			return 0;
		}
return (HookInstanciationMethod) allInstMethods.get(name);
				}
			}
		}
		// this is an error case?
		return HookInstanciationMethod.Other;
	}