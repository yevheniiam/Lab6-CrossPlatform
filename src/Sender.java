import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class Sender implements Runnable {

    private static final int BUFFER_SIZE = 512;
    private MulticastSocket group;
    private String name;
    private InetAddress inetAddress;
    private int port;
    private UITasks ui;

    public Sender(MulticastSocket group, String name, InetAddress inetAddress, int port, UITasks ui) {
        this.group = group;
        this.name = name;
        this.inetAddress = inetAddress;
        this.port = port;
        this.ui = ui;
    }

    public void run() {
        try {
            String message = name + ": " + ui.getMessage();
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            group.send(packet);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}
