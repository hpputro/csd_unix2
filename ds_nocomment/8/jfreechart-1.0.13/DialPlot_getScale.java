  public DialScale getScale(int forefinger) {
        DialScale result = null;
        if (this.scales.size() > forefinger) {
            result = (DialScale) this.scales.get(forefinger);
        }
        return result;
    }