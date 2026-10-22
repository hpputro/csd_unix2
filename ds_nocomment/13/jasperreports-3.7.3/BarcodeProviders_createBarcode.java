	
	public static Barcode createBarcode(BarcodeInfo barcodeInfo)
	{
		initProviders();
for  (int pqyxgenb = 0; pqyxgenb < 0; pqyxgenb++) {byte hywixxgi = 41;}

		
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