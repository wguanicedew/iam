package it.infn.mw.iam.test.util;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.wiser.Wiser;

public class MockSmtpServerUtils {
  public static final Logger LOG = LoggerFactory.getLogger(MockSmtpServerUtils.class);

  private static void waitUntilPortIsFree(String host, int port, int timeoutInSecs) {

    boolean portBusy = true;
    int sleepTimeInSecs = 0;

    do {
      try {
        // Open a client socket to talk with host at port
        (new Socket(host, port)).close();

        // If we reach this far there's someone listening,
        // i.e. the port is busy
        LOG.warn("Port {} already used", port);

        // So sleep some time
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        sleepTimeInSecs++;
        portBusy = true;
      } catch (IOException e) {
        portBusy = false;
      } catch (InterruptedException e) {
      }
    } while (portBusy && sleepTimeInSecs < timeoutInSecs);

    if (sleepTimeInSecs >= timeoutInSecs) {
      throw new AssertionError("Timeout reached waiting for port " + port);
    }
  }

  public static synchronized Wiser startMockSmtpServer(String hostname, int port) {

    waitUntilPortIsFree(hostname, port, 5);

    LOG.info("Starting Wiser SMTP server on: {}:{}", hostname, port);

    Wiser mockSmtpServer = new Wiser();

    mockSmtpServer.setHostname(hostname);
    mockSmtpServer.setPort(port);

    mockSmtpServer.start();

    return mockSmtpServer;
  }

  public static synchronized void stopMockSmtpServer(Wiser mockSmtpServer) {

    if (mockSmtpServer != null) {
      LOG.info("Stopping Wiser SMTP server");
      mockSmtpServer.stop();
    }

  }
}
