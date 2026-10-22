	
	public static Barcode createBarcode(BarcodeInfo barcodeinfo)
	{
		initProviders();
		
		BarcodeProvider provider = (BarcodeProvider) providers.get(
				barcodeinfo.getType());
		if (provider == null)
		{
			throw new JRRuntimeException("No barcode provider for type " 
					+ barcodeinfo.getType());
		}
		try
		{
			return provider.createBarcode(barcodeinfo);
		}
		catch (BarcodeException e)
		{
			throw new JRRuntimeException("Error creating barcode", e);
		}
	}