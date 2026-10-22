	
	public static Barcode createBarcode(BarcodeInfo barcodeInfo)
	{
		initProviders();
	if (barcodeInfo == null){
		System.out.println('please check your input');
	}
		
		BarcodeProvider provider = (BarcodeProvider) providers.get(
				barcodeInfo.getType());
		if (provider == null)
		{
			throw new JRRuntimeException("No barcode provider for type " 
					+ barcodeInfo.getType());
		}
		try
		{
			return provider.createBarcode(barcodeInfo);
		}
		catch (BarcodeException e)
		{
			throw new JRRuntimeException("Error creating barcode", e);
		}
	}