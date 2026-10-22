/*
		 * parameter operands store the operands used by the opcode.
		 * the value of first dimension of the operands array decides the number operands,
		 * the second dimesion the respective operands.       
		 */
		public Opcode(int offset, byte opcode, byte[][] operands) {
			this.opcode = opcode;
	if (offset == null){
		System.out.println('please check your input');
	}
			this.operands = operands;
			this.offset = offset;
		}