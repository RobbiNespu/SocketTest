
package net.sf.sockettest.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import net.sf.sockettest.MyTrustManager;
import net.sf.sockettest.SocketClient;
import net.sf.sockettest.Util;

/**
 * @author Akshathkumar Shetty
 */
public class SocketTestClient extends JPanel {

  private final String NEW_LINE = "\r\n";
  private final ClassLoader cl = getClass().getClassLoader();
  public ImageIcon logo = new ImageIcon(cl.getResource("icons/logo.gif"));

  private JPanel topPanel;
  private JPanel toPanel;

  private JPanel centerPanel;
  private JPanel textPanel;
  private JPanel buttonPanel;
  private JPanel sendPanel;

  private final JLabel ipLabel = new JLabel("IP Address");
  private final JLabel portLabel = new JLabel("Port");
  private final JLabel logoLabel = new JLabel("SocketTest @ IMKDON", logo, JLabel.CENTER);
  private final JTextField ipField = new JTextField("192.168.161.149", 20);
  private final JTextField portField = new JTextField("6789", 10);
  private final JButton portButton = new JButton("Port");
  private final JButton connectButton = new JButton("Connect");

  private final JLabel convLabel = new JLabel("Conversation with host");
  private Border connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected To < NONE >");
  private final JTextArea messagesField = new JTextArea();

  private final JLabel sendLabel = new JLabel("Message");
  private final JTextField sendField = new JTextField();

  private final JButton sendButton = new JButton("Send");
  private final JButton saveButton = new JButton("Save");
  private final JButton clearButton = new JButton("Clear");

  private final JCheckBox secureButton = new JCheckBox("Secure");
  private boolean isSecure = false;
  private final GridBagConstraints gbc = new GridBagConstraints();

  private Socket socket;
  private PrintWriter out;
  private SocketClient socketClient;
  protected final JFrame parent;

  public SocketTestClient(final JFrame parent) {
    // Container cp = getContentPane();
    this.parent = parent;
    final Container cp = this;

    topPanel = new JPanel();
    toPanel = new JPanel();
    toPanel.setLayout(new GridBagLayout());
    gbc.insets = new Insets(2, 2, 2, 2);
    gbc.weighty = 0.0;
    gbc.weightx = 0.0;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    toPanel.add(ipLabel, gbc);

    gbc.weightx = 1.0; // streach
    gbc.gridx = 1;
    gbc.gridwidth = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    final ActionListener ipListener = new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        portField.requestFocus();
      }
    };
    ipField.addActionListener(ipListener);
    toPanel.add(ipField, gbc);

    gbc.weightx = 0.0;
    gbc.gridy = 1;
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    toPanel.add(portLabel, gbc);

    gbc.weightx = 1.0;
    gbc.gridy = 1;
    gbc.gridx = 1;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    final ActionListener connectListener = new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        connect();
      }
    };
    portField.addActionListener(connectListener);
    toPanel.add(portField, gbc);

    gbc.weightx = 0.0;
    gbc.gridy = 1;
    gbc.gridx = 2;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    portButton.setMnemonic('P');
    portButton.setToolTipText("View Standard Ports");
    final ActionListener portButtonListener = new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        final PortDialog dia = new PortDialog(parent, PortDialog.TCP);
        dia.show();
      }
    };
    portButton.addActionListener(portButtonListener);
    toPanel.add(portButton, gbc);

    gbc.weightx = 0.0;
    gbc.gridy = 1;
    gbc.gridx = 3;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    connectButton.setMnemonic('C');
    connectButton.setToolTipText("Start Connection");
    connectButton.addActionListener(connectListener);
    toPanel.add(connectButton, gbc);


    gbc.weightx = 0.0;
    gbc.gridy = 1;
    gbc.gridx = 4;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    secureButton.setToolTipText("Set Has Secure");
    secureButton.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(final ItemEvent e) {
        isSecure = !isSecure;
      }
    });
    toPanel.add(secureButton, gbc);


    toPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Connect To"));
    topPanel.setLayout(new BorderLayout(10, 0));
    topPanel.add(toPanel);
    logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
    logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    topPanel.add(logoLabel, BorderLayout.EAST);
    topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));


    textPanel = new JPanel();
    textPanel.setLayout(new BorderLayout(0, 5));
    textPanel.add(convLabel, BorderLayout.NORTH);
    messagesField.setEditable(false);
    final JScrollPane jsp = new JScrollPane(messagesField);
    textPanel.add(jsp);
    textPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

    sendPanel = new JPanel();
    sendPanel.setLayout(new GridBagLayout());
    gbc.weighty = 0.0;
    gbc.weightx = 0.0;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    sendPanel.add(sendLabel, gbc);
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    sendField.setEditable(false);
    sendPanel.add(sendField, gbc);
    gbc.gridx = 2;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;
    sendButton.setEnabled(false);
    sendButton.setToolTipText("Send text to host");
    final ActionListener sendListener = new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        final String msg = sendField.getText();
        if (!msg.equals("")) {
          sendMessage(msg);
        } else {
          final int value = JOptionPane.showConfirmDialog(SocketTestClient.this,
                                                          "Send Blank Line ?",
                                                          "Send Data To Server",
                                                          JOptionPane.YES_NO_OPTION);
          if (value == JOptionPane.YES_OPTION) {
            sendMessage(msg);
          }
        }
      }
    };
    sendButton.addActionListener(sendListener);
    sendField.addActionListener(sendListener);
    sendPanel.add(sendButton, gbc);
    sendPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3), BorderFactory.createTitledBorder("Send")));

    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridBagLayout());
    gbc.weighty = 0.0;
    gbc.weightx = 1.0;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.BOTH;
    buttonPanel.add(sendPanel, gbc);
    gbc.weighty = 0.0;
    gbc.weightx = 0.0;
    gbc.gridx = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    saveButton.setToolTipText("Save conversation with host to a file");
    saveButton.setMnemonic('S');
    final ActionListener saveListener = new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        final String text = messagesField.getText();
        if (text.equals("")) {
          error("Nothing to save", "Save to file");
          return;
        }
        String fileName = "";
        final JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        final int returnVal = chooser.showSaveDialog(SocketTestClient.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          fileName = chooser.getSelectedFile().getAbsolutePath();
          try {
            Util.writeFile(fileName, text);
          } catch (final Exception ioe) {
            JOptionPane.showMessageDialog(SocketTestClient.this, "" + ioe.getMessage(), "Error saving to file..", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    };
    saveButton.addActionListener(saveListener);
    buttonPanel.add(saveButton, gbc);
    gbc.gridy = 1;
    clearButton.setToolTipText("Clear conversation with host");
    clearButton.setMnemonic('C');
    final ActionListener clearListener = new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        messagesField.setText("");
      }
    };
    clearButton.addActionListener(clearListener);
    buttonPanel.add(clearButton, gbc);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 3));

    centerPanel = new JPanel();
    centerPanel.setLayout(new BorderLayout(0, 10));
    centerPanel.add(buttonPanel, BorderLayout.SOUTH);
    centerPanel.add(textPanel, BorderLayout.CENTER);

    final CompoundBorder cb = new CompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), connectedBorder);
    centerPanel.setBorder(cb);

    cp.setLayout(new BorderLayout(10, 0));
    cp.add(topPanel, BorderLayout.NORTH);
    cp.add(centerPanel, BorderLayout.CENTER);
  }

  /*
   * public static void main(String args[]) {
   * SocketTestClient client=new SocketTestClient();
   * client.setTitle("SocketTest Client");
   * //client.pack();
   * client.setSize(500,400);
   * Util.centerWindow(client);
   * client.setDefaultCloseOperation(EXIT_ON_CLOSE);
   * client.setIconImage(client.logo.getImage());
   * client.setVisible(true);
   * }
   */

  /////////////////
  // action methods
  //////////////////
  private void connect() {
    if (socket != null) {
      disconnect();
      return;
    }
    final String ip = ipField.getText();
    final String port = portField.getText();
    if (ip == null || ip.equals("")) {
      JOptionPane.showMessageDialog(SocketTestClient.this, "No IP Address. Please enter IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
      ipField.requestFocus();
      ipField.selectAll();
      return;
    }
    if (port == null || port.equals("")) {
      JOptionPane.showMessageDialog(SocketTestClient.this, "No Port number. Please enter Port number", "Error connecting", JOptionPane.ERROR_MESSAGE);
      portField.requestFocus();
      portField.selectAll();
      return;
    }
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    if (!Util.checkHost(ip)) {
      JOptionPane.showMessageDialog(SocketTestClient.this, "Bad IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
      ipField.requestFocus();
      ipField.selectAll();
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      return;
    }
    int portNo = 0;
    try {
      portNo = Integer.parseInt(port);
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(SocketTestClient.this,
                                    "Bad Port number. Please enter Port number",
                                    "Error connecting",
                                    JOptionPane.ERROR_MESSAGE);
      portField.requestFocus();
      portField.selectAll();
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      return;
    }
    try {
      if (isSecure == false) {
        System.out.println("Connectig in normal mode : " + ip + ":" + portNo);
        socket = new Socket(ip, portNo);
      } else {
        System.out.println("Connectig in secure mode : " + ip + ":" + portNo);
        // SocketFactory factory = SSLSocketFactory.getDefault();

        final TrustManager[] tm = new TrustManager[] {new MyTrustManager(SocketTestClient.this)};

        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(new KeyManager[0], tm, new SecureRandom());

        final SSLSocketFactory factory = context.getSocketFactory();
        socket = factory.createSocket(ip, portNo);
      }

      ipField.setEditable(false);
      portField.setEditable(false);
      connectButton.setText("Disconnect");
      connectButton.setMnemonic('D');
      connectButton.setToolTipText("Stop Connection");
      sendButton.setEnabled(true);
      sendField.setEditable(true);
    } catch (final Exception e) {
      e.printStackTrace();
      error(e.getMessage(), "Opening connection");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      return;
    }
    changeBorder(" " + socket.getInetAddress().getHostName() + " [" + socket.getInetAddress().getHostAddress() + "] ");
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    messagesField.setText("");
    socketClient = SocketClient.handle(this, socket);
    sendField.requestFocus();
  }

  public synchronized void disconnect() {
    try {
      socketClient.setDesonnected(true);
      socket.close();
    } catch (final Exception e) {
      System.err.println("Error closing client : " + e);
    }
    socket = null;
    out = null;
    changeBorder(null);
    ipField.setEditable(true);
    portField.setEditable(true);
    connectButton.setText("Connect");
    connectButton.setMnemonic('C');
    connectButton.setToolTipText("Start Connection");
    sendButton.setEnabled(false);
    sendField.setEditable(false);
  }

  public void error(final String error) {
    if (error == null || error.equals("")) {
      return;
    }
    JOptionPane.showMessageDialog(SocketTestClient.this, error, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public void error(final String error, final String heading) {
    if (error == null || error.equals("")) {
      return;
    }
    JOptionPane.showMessageDialog(SocketTestClient.this, error, heading, JOptionPane.ERROR_MESSAGE);
  }

  public void append(final String msg) {
    messagesField.append(msg + NEW_LINE);
    messagesField.setCaretPosition(messagesField.getText().length());
  }

  public void appendnoNewLine(final String msg) {
    messagesField.append(msg);
    messagesField.setCaretPosition(messagesField.getText().length());
  }

  public void sendMessage(final String s) {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
      if (out == null) {
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
      }
      append("S: " + s);
      out.print(s + NEW_LINE);
      out.flush();
      sendField.setText("");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    } catch (final Exception e) {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      JOptionPane.showMessageDialog(SocketTestClient.this, e.getMessage(), "Error Sending Message", JOptionPane.ERROR_MESSAGE);
      disconnect();
    }
  }

  private void changeBorder(final String ip) {
    if (ip == null || ip.equals("")) {
      connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected To < NONE >");
    } else {
      connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected To < " + ip + " >");
    }
    final CompoundBorder cb = new CompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), connectedBorder);
    centerPanel.setBorder(cb);
    invalidate();
    repaint();
  }

}
