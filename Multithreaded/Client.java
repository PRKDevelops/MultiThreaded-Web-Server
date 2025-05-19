import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {

    public Runnable getRunnable() {
        return () -> {
            int port = 8010;
            try {
                InetAddress address = InetAddress.getByName("localhost");
                Socket socket = new Socket(address, port);
                socket.setSoTimeout(5000);  // timeout after 5 seconds

                try (
                    PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader fromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {
                    toSocket.println("Hello from Client " + socket.getLocalSocketAddress());
                    String line = fromSocket.readLine();
                    System.out.println("Response from Server: " + line);

                } catch (SocketTimeoutException e) {
                    System.err.println("Read timed out for client: " + socket.getLocalSocketAddress());
                } catch (IOException e) {
                    System.err.println("IO Exception in client: " + e.getMessage());
                } finally {
                    socket.close();
                }

            } catch (IOException e) {
                System.err.println("Client connection error: " + e.getMessage());
            }
        };
    }

    public static void main(String[] args) {
        Client client = new Client();
        for (int i = 0; i < 100; i++) {
            try {
                Thread thread = new Thread(client.getRunnable());
                thread.start();

                // Small delay to avoid hammering the server
                Thread.sleep(20); 

            } catch (Exception ex) {
                System.err.println("Error starting thread: " + ex.getMessage());
            }
        }
    }
}
