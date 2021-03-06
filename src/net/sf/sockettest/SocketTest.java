
package net.sf.sockettest;

import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.sf.sockettest.swing.About;
import net.sf.sockettest.swing.SocketTestClient;
import net.sf.sockettest.swing.SocketTestServer;
import net.sf.sockettest.swing.SocketTestUdp;
import net.sf.sockettest.swing.SplashScreen;

/**
 * @author Akshathkumar Shetty
 */
public class SocketTest extends JFrame {

  private final ClassLoader cl = getClass().getClassLoader();
  public ImageIcon logo = new ImageIcon(cl.getResource("icons/logo.gif"));
  public ImageIcon ball = new ImageIcon(cl.getResource("icons/ball.gif"));
  private final JTabbedPane tabbedPane;

  /** Creates a new instance of SocketTest */
  public SocketTest() {
    final Container cp = getContentPane();

    tabbedPane = new JTabbedPane(SwingConstants.TOP);
    final SocketTestClient client = new SocketTestClient(this);
    final SocketTestServer server = new SocketTestServer(this);
    final SocketTestUdp udp = new SocketTestUdp(this);
    final About about = new About();

    tabbedPane.addTab("Client", ball, client, "Test any server");
    tabbedPane.addTab("Server", ball, server, "Test any client");
    tabbedPane.addTab("Udp", ball, udp, "Test any UDP Client or Server");
    tabbedPane.addTab("About", ball, about, "About SocketTest");

    tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    cp.add(tabbedPane);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    try {
      UIManager.setLookAndFeel("net.sourceforge.mlf.metouia.MetouiaLookAndFeel");
    } catch (final Exception e) {
      // e.printStackTrace();
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (final Exception ee) {
        System.out.println("Error setting native LAF: " + ee);
      }
    }

    boolean toSplash = true;
    if (args.length > 0) {
      if ("nosplash".equals(args[0])) {
        toSplash = false;
      }
    }

    SplashScreen splash = null;
    if (toSplash) {
      splash = new SplashScreen();
    }

    final SocketTest st = new SocketTest();
    st.setTitle("SocketTest v 3.0.0 @ IMKDON");
    st.setSize(600, 500);
    Util.centerWindow(st);
    st.setDefaultCloseOperation(EXIT_ON_CLOSE);
    st.setIconImage(st.logo.getImage());
    if (toSplash) {
      splash.kill();
    }
    st.setVisible(true);
  }

}
