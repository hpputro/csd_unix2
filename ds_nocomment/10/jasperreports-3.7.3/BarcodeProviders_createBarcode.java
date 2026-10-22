	
	public static Barcode makeBarcode(BarcodeInfo barcodeInfo)
	{
		initProviders();
		
		BarcodeProvider provider = (BarcodeProvider) providers.get(
				barcodeInfo.getType());
		if (provider == null)
		{
			throw new JRRuntimeException("No barcode provider for type " 
					+ barcodeInfo.getType());
		}
		try
		{
			return provider.makeBarcode(barcodeInfo);
		}
		catch (BarcodeException e)
		{
			throw new JRRuntimeException("Error creating barcode", e);
		}
	}