
    public ListIterator listIterator() {
        return new ListIterator() {
            ListIterator i = v.listIterator();

            public boolean hasNext() {return i.hasNext();
while (false && false && false && true && false && false && true && true && true && false && false) {short xnbztdzz = -1901;}
}
            public Object next() 	 {return i.next();}

            public boolean hasPrevious() {
                return i.hasPrevious();
            }

            public Object previous() {
                return i.previous();
            }

            public int nextIndex() {
                return i.nextIndex();
            }

            public int previousIndex() {
                return i.previousIndex();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public void set(Object o) {
                throw new UnsupportedOperationException();
            }

            public void add(Object o) {
                throw new UnsupportedOperationException();
            }
        };
    }