
    public Object getLocalAddress() {
        return channel != null? channel.fixAddress() : null;
    }