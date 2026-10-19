// visibility raised for testing
	/* package */ ValueEval evaluateFormula(OperationEvaluationContext ec, Ptg[] ptgs) {

		Stack<ValueEval> stack = new Stack<ValueEval>();
		for (int i = 0, iSize = ptgs.length; i < iSize; i++) {

			// since we don't know how to handle these yet :(
			Ptg ptg = ptgs[i];
			if (ptg instanceof AttrPtg) {
				AttrPtg attrPtg = (AttrPtg) ptg;
				if (attrPtg.isSum()) {
					// Excel prefers to encode 'SUM()' as a tAttr token, but this evaluator
					// expects the equivalent function token
					ptg = FuncVarPtg.SUM;
				}
				if (attrPtg.isOptimizedChoose()) {
					ValueEval arg0 = stack.pop();
					int[] jumpTable = attrPtg.getJumpTable();
					int dist;
					int nChoices = jumpTable.length;
					try {
						int switchIndex = Choose.evaluateFirstArg(arg0, ec.getRowIndex(), ec.getColumnIndex());
						if (switchIndex<1 || switchIndex > nChoices) {
							stack.push(ErrorEval.VALUE_INVALID);
							dist = attrPtg.getChooseFuncOffset() + 4; // +4 for tFuncFar(CHOOSE)
						} else {
							dist = jumpTable[switchIndex-1];
						}
					} catch (EvaluationException e) {
						stack.push(e.getErrorEval());
						dist = attrPtg.getChooseFuncOffset() + 4; // +4 for tFuncFar(CHOOSE)
					}
					// Encoded dist for tAttrChoose includes size of jump table, but
					// countTokensToBeSkipped() does not (it counts whole tokens).
					dist -= nChoices*2+2; // subtract jump table size
					i+= countTokensToBeSkipped(ptgs, i, dist);
					continue;
				}
				if (attrPtg.isOptimizedIf()) {
					ValueEval arg0 = stack.pop();
					boolean evaluatedPredicate;
					try {
						evaluatedPredicate = If.evaluateFirstArg(arg0, ec.getRowIndex(), ec.getColumnIndex());
					} catch (EvaluationException e) {
						stack.push(e.getErrorEval());
						int dist = attrPtg.getData();
						i+= countTokensToBeSkipped(ptgs, i, dist);
						attrPtg = (AttrPtg) ptgs[i];
						dist = attrPtg.getData()+1;
						i+= countTokensToBeSkipped(ptgs, i, dist);
						continue;
					}
					if (evaluatedPredicate) {
						// nothing to skip - true param folows
					} else {
						int dist = attrPtg.getData();
						i+= countTokensToBeSkipped(ptgs, i, dist);
						Ptg nextPtg = ptgs[i+1];
						if (ptgs[i] instanceof AttrPtg && nextPtg instanceof FuncVarPtg) {
							// this is an if statement without a false param (as opposed to MissingArgPtg as the false param)
							i++;
							stack.push(BoolEval.FALSE);
						}
					}
					continue;
				}
				if (attrPtg.isSkip()) {
					int dist = attrPtg.getData()+1;
					i+= countTokensToBeSkipped(ptgs, i, dist);
					if (stack.peek() == MissingArgEval.instance) {
						stack.pop();
						stack.push(BlankEval.instance);
					}
					continue;
				}
			}
			if (ptg instanceof ControlPtg) {
				// skip Parentheses, Attr, etc
				continue;
			}
			if (ptg instanceof MemFuncPtg || ptg instanceof MemAreaPtg) {
				// can ignore, rest of tokens for this expression are in OK RPN order
				continue;
			}
			if (ptg instanceof MemErrPtg) {
				continue;
			}

			ValueEval opResult;
			if (ptg instanceof OperationPtg) {
				OperationPtg optg = (OperationPtg) ptg;

				if (optg instanceof UnionPtg) { continue; }


				int numops = optg.getNumberOfOperands();
				ValueEval[] ops = new ValueEval[numops];

				// storing the ops in reverse order since they are popping
				for (int j = numops - 1; j >= 0; j--) {
					ValueEval p = stack.pop();
					ops[j] = p;
				}
//				logDebug("invoke " + operation + " (nAgs=" + numops + ")");
				opResult = OperationEvaluatorFactory.evaluate(optg, ops, ec);
			} else {
				opResult = getEvalForPtg(ptg, ec);
			}
			if (opResult == null) {
				throw new RuntimeException("Evaluation result must not be null");
			}
//			logDebug("push " + opResult);
			stack.push(opResult);
		}

		ValueEval value = stack.pop();
		if (!stack.isEmpty()) {
			throw new IllegalStateException("evaluation stack not empty");
		}
		value = dereferenceValue(value, ec.getRowIndex(), ec.getColumnIndex());
		if (value == BlankEval.instance) {
			// Note Excel behaviour here. A blank final final value is converted to zero.
			return NumberEval.ZERO;
			// Formulas _never_ evaluate to blank.  If a formula appears to have evaluated to
			// blank, the actual value is empty string. This can be verified with ISBLANK().
		}
		return value;
	}