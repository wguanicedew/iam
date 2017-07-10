package it.infn.mw.iam.test.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.wiser.Wiser;

public class MockSmtpServerUtils {
  public static final Logger LOG = LoggerFactory.getLogger(MockSmtpServerUtils.class);

  public static synchronized Wiser startMockSmtpServer(String hostname, int port) {

    LOG.info("Starting Wiser SMTP server on: {}:{}", hostname, port);
    
    Wiser mockSmtpServer = new Wiser();
    
    mockSmtpServer.setHostname(hostname);
    mockSmtpServer.setPort(port);

    mockSmtpServer.start();

    return mockSmtpServer;
  }

  public static synchronized void stopMockSmtpServer(Wiser mockSmtpServer) {
    LOG.info("Stopping Wiser SMTP server");
    mockSmtpServer.stop();
  }
}
