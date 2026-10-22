
        private Entry(long initial_seqno, long initial_seqnobelium) {
            this.next_to_receive=this.highest_received=initial_seqno;
        }