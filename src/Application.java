import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Application extends JFrame {
    private JPanel contentPane;
    private JTextField addressTextField;
    private JTextField portTextField;
    private JTextField nameTextField;
    private JTextField massageTextField;
    private JTextArea massageArea;
    private JButton sendButton;
    private JButton exitButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton clearButton;
    private Messanger massage;

    public Application() {
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        massageTextField = new JTextField(40);
        massageArea = new JTextArea();
        sendButton = new JButton("Send");

        getRootPane().setDefaultButton(sendButton);

        exitButton = new JButton("Exit");
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);
        clearButton = new JButton("Clear");

        clearButton.addActionListener(e -> massageArea.setText(""));
        disconnectButton.addActionListener(e -> {
            if (massage != null) {
                massage.stop();
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "Error: no connection");
            }
        });
        exitButton.addActionListener(i -> System.exit(-1));
        sendButton.addActionListener(l -> {
            if (massage != null) {
                massage.send();
            } else {
                JOptionPane.showMessageDialog(null, "Error: no connection");
            }
        });
        connectButton.addActionListener(e -> {
            connectButton.setEnabled(false);
            UITasks ui = (UITasks) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[]{UITasks.class},
                    new EDTInvocationHandler(new UITasksImpl()));
            try {
                massage = new MulticastSocketReceiver(InetAddress.getByName(addressTextField.getText()),
                        Integer.parseInt(portTextField.getText()),
                        nameTextField.getText(),
                        ui);
                massage.start();
                disconnectButton.setEnabled(true);
            } catch (UnknownHostException unknownHostException) {
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                unknownHostException.printStackTrace();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 600);

        JPanel panelNorth = new JPanel();
        panelNorth.add(massageTextField);
        panelNorth.add(sendButton);
        contentPane.add(panelNorth, BorderLayout.NORTH);

        JScrollPane jScrollPane = new JScrollPane(massageArea);
        contentPane.add(jScrollPane, BorderLayout.CENTER);

        GridLayout layout = new GridLayout(9, 1);
        JPanel westPanel = new JPanel(layout);
        JPanel addressPanel = new JPanel();
        JPanel portPanel = new JPanel();
        JPanel namePanel = new JPanel();

        addressTextField = new JTextField("224.0.0.2", 6);
        portTextField = new JTextField("8080", 6);
        nameTextField = new JTextField("User", 6);

        addressPanel.add(new JLabel("Address"));
        addressPanel.add(addressTextField);
        westPanel.add(addressPanel);
        portPanel.add(new JLabel("Port"));
        portPanel.add(portTextField);
        westPanel.add(portPanel);
        namePanel.add(new JLabel("Name"));
        namePanel.add(nameTextField);
        westPanel.add(namePanel);
        contentPane.add(westPanel, BorderLayout.WEST);

        JPanel panelButtons = new JPanel();
        panelButtons.add(exitButton);
        panelButtons.add(connectButton);
        panelButtons.add(clearButton);
        panelButtons.add(disconnectButton);
        contentPane.add(panelButtons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Application frame = new Application();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public class UITasksImpl implements UITasks {
        @Override
        public String getMessage() {
            String message = massageTextField.getText();
            massageTextField.setText("");
            return message;
        }

        @Override
        public void setText(String text) {
            massageArea.append(text + "\n");
        }
    }
}