	
	public static Barcode createBarcode(BarcodeInfo barcodeInfo)
	{
		initProviders();
		
		BarcodeProvider supplier = (BarcodeProvider) providers.get(
				barcodeInfo.getType());
		if (supplier == null)
		{
			throw new JRRuntimeException("No barcode supplier for type " 
					+ barcodeInfo.getType());
		}
		try
		{
			return supplier.createBarcode(barcodeInfo);
		}
		catch (BarcodeException e)
		{
			throw new JRRuntimeException("Error creating barcode", e);
		}
	}