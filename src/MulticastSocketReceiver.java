import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSocketReceiver implements Messanger {

    private UITasks ui;
    private MulticastSocket group;
    private InetAddress address;
    private String name;
    private Receiver receiver;
    private int port;

    public MulticastSocketReceiver(InetAddress address, int port, String name, UITasks ui) {
        this.name = name;
        this.address = address;
        this.ui = ui;
        this.port = port;
        try {
            group = new MulticastSocket(port);
            group.setTimeToLive(2);
            group.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        receiver = new Receiver(group, address, ui);
        new Thread(receiver).start();
    }

    @Override
    public void stop() {
        receiver.cancel();
        try {
            group.leaveGroup(address);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } finally {
            group.close();
        }
    }

    @Override
    public void send() {
        new Thread(new Sender(group, name, address, port, ui)).start();
    }
}