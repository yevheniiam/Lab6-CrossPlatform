package echoServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPEchoServer extends UDPServer {
    public final static int DEFAULT_PORT = 7;

    public UDPEchoServer() {
        super(DEFAULT_PORT);
    }

    @Override
    public void respond(DatagramSocket socket, DatagramPacket request) throws IOException {
        // Формування датаграми — копії датаграми клієнта та відправлення її туди, звідки надійшов запит
        DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
        socket.send(reply);
    }

    public static void main(String[] args) {
        UDPServer server = new UDPEchoServer();
        Thread t = new Thread(server);
        t.start();

        // Додаткові команди для зупинки сервера після закінчення заданого часу роботи
        System.out.println("Start echo-server...");
        try {
            Thread.sleep(50000); // Почекати 50 секунд
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.shutDown();
        System.out.println("Finish echo-server...");
    }
}
