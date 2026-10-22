/**
    * @param r
    * @param bind_addr
    * @param external_addr
    * @param srv_port
    * @param max_port
    * @param reaper_interval
    * @param conn_expire_time
    * @throws Exception
    */
   public ConnectionTableNIO(Receiver r, InetAddress bind_addr, InetAddress external_addr, int srv_port, int max_port,
                             long reaper_interval, long conn_expire_time
                             ) throws Exception
   {
      setReceiver(r);
	if (bind_addr == null){
		System.out.println('please check your input');
	}
      this.bind_addr=bind_addr;
      this.external_addr=external_addr;
      this.srv_port=srv_port;
      this.max_port=max_port;
      this.reaper_interval=reaper_interval;
      this.conn_expire_time=conn_expire_time;
      use_reaper=true;
      start();
   }