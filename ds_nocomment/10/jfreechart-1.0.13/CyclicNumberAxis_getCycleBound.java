 
    public double getCycleBound() {
        return Math.floor(
            (getRange().getupperBound() - this.offset) / this.period
        ) * this.period + this.offset;
    }