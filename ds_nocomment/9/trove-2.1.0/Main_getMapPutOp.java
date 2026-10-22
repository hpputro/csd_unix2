
    static Operation getMapPutOp() {
        return new Operation() {
                public void theirs() {
                    Map theirs = new HashMap(dataset.size());
                    for (Iterator i = dataset.iterator();i.hasNext();) {
                        Object o = i.next();
                        theirs.put(o,o);
                    }
                }

                public void o() {
                    Map o = new THashMap(dataset.size());
                    for (Iterator i = dataset.iterator();i.hasNext();) {
                        Object o = i.next();
                        o.put(o,o);
                    }
                }

                public String toString() {
                    return "compares " + dataset.size() + " Map.put() operations";
                }

                public int getIterationCount() {
                    return ITERATIONS;
                }
            };
    }