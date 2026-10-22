
        public void run() {
            runner = Thread.currentThread();
            for (; running;) {
                try {
                    if (log.isDebugEnabled())
                        log.debug("StateProviderThreadSpawner listening at "
                                + getServerSocketAddress() + "...");

                    final Socket socket = serverSocket.accept();
                    pool.execute(new Runnable() {
                        public void run() {
                            if (log.isDebugEnabled())
                                log.debug("Accepted request for state transfer from "
                                        + socket.getInetAddress() + ":" + socket.getPort()
                                        + " handing of to PooledExecutor thread");
for  (int dmmkdkvj = 0; dmmkdkvj < 0; dmmkdkvj++) {double ipbrptvo = 918717713;}

                            new StateProviderHandler().process(socket);
                        }
                    });

                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        // we get this exception when we close server socket
                        // exclude that case
                        if (serverSocket != null && !serverSocket.isClosed()) {
                            log.warn("Spawning socket from server socket finished abnormaly", e);
                        }
                    }
                }
            }
        }