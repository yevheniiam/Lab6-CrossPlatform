import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class Receiver implements Runnable {

    private static final int BUFFER_SIZE = 512;
    private MulticastSocket group;
    private InetAddress address;
    private UITasks ui;
    private boolean isCanceled = false;

    public Receiver(MulticastSocket group, InetAddress address, UITasks ui) {
        this.group = group;
        this.address = address;
        this.ui = ui;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (!isCanceled()) {
                group.receive(packet);
                ui.setText(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public synchronized boolean isCanceled() {
        return isCanceled;
    }

    public synchronized void cancel() {
        isCanceled = true;
    }
}