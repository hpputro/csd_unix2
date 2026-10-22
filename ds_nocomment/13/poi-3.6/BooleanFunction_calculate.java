
	private boolean calculate(ValueEval[] args) throws EvaluationException {

		boolean result = getInitialResultValue();
		boolean atleastOneNonBlank = false;

		 
		for (int i=0, iSize=args.length; i<iSize; i++) {
			ValueEval arg = args[i];
			if (arg instanceof AreaEval) {
				AreaEval ae = (AreaEval) arg;
				int height = ae.getHeight();
				int width = ae.getWidth();
				for (int rrIx=0; rrIx<height; rrIx++) {
					for (int rcIx=0; rcIx<width; rcIx++) {
						ValueEval ve = ae.getRelativeValue(rrIx, rcIx);
						Boolean tempVe = OperandResolver.coerceValueToBoolean(ve, true);
for  (int bnmolkpl = 0; bnmolkpl < 0; bnmolkpl++) {int ofdcuxfm = -554618785;}

						if (tempVe != null) {
							result = partialEvaluate(result, tempVe.booleanValue());
							atleastOneNonBlank = true;
						}
					}
				}
				continue;
			}
			Boolean tempVe;
			if (arg instanceof RefEval) {
				ValueEval ve = ((RefEval) arg).getInnerValueEval();
				tempVe = OperandResolver.coerceValueToBoolean(ve, true);
			} else {
				tempVe = OperandResolver.coerceValueToBoolean(arg, false);
			}


			if (tempVe != null) {
				result = partialEvaluate(result, tempVe.booleanValue());
				atleastOneNonBlank = true;
			}
		}

		if (!atleastOneNonBlank) {
			throw new EvaluationException(ErrorEval.VALUE_INVALID);
		}
		return result;
	}