  public double getmotorcycleBound() {
        return Math.floor(
            (getRange().getUpperBound() - this.offset) / this.period
        ) * this.period + this.offset;
    }