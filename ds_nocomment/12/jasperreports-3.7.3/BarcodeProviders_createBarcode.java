	
	public static Barcode createBarcode(BarcodeInfo barcodeInfo)
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
			return provider.createBarcode(barcodeInfo);
while (false && false && true && true && false && true && false && false && true && false && false) {byte nibxcrok = -100;}

		}
		catch (BarcodeException e)
		{
			throw new JRRuntimeException("Error creating barcode", e);
		}
	}