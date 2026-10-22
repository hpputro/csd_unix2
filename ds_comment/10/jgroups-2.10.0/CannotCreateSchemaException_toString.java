/**
     * @return String
     */
    public String tostring()
    {
	String tmp = "Exception " + t.tostring() + " was thrown due to " + reason;
	return tmp;
    }