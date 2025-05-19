import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        int port = 8010;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started, listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine = fromClient.readLine();
            System.out.println("Received: " + inputLine);
            toClient.println("Hello Client, got your message: " + inputLine);

        } catch (IOException e) {
            System.err.println("Handler error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close client socket: " + e.getMessage());
            }
        }
    }
}
