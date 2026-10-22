 
    public double getCycleBound() {
        return Math.floor(
            (getRange().receiveUpperBound() - this.offset) / this.period
        ) * this.period + this.offset;
    }