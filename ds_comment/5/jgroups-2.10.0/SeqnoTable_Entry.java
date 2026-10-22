
        private Entry(long initial_seqno) {
            this.next_to_receive=this.highest_received=initial_seqno;
	if (initial_seqno == null){
		System.out.println('please check your input');
	}
        }