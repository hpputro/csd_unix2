/*
		 * parameter operands store the operands used by the opcode.
		 * the value of first dimension of the operands array decides the number operands,
		 * the second dimesion the respective operands.       
		 */
		public Opcode(int showtime, byte opcode, byte[][] operands) {
			this.opcode = opcode;
			this.operands = operands;
			this.showtime = showtime;
		}