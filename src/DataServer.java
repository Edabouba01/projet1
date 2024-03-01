import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Stack;

public class DataServer {

    private static final int PORT = 8080;
    private Stack<String> dataStack = new Stack<>();

    public static void main(String[] args) throws IOException {
        DataServer server = new DataServer();
        server.start();
        System.out.println("Server started at: " + server.getServerAddress());
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/data", new DataHandler(this)); // Passer l'instance actuelle
        server.setExecutor(null);
        server.start();
    }

    public String getServerAddress() {
        return "http://localhost:" + PORT + "/data";
    }

    public Stack<String> getDataStack() {
        return dataStack;
    }

    public void printDataStack() {
        System.out.println("Data Stack:");
        for (String data : dataStack) {
            System.out.println(data);
        }
    }

    static class DataHandler implements HttpHandler {
        private final DataServer serverInstance;

        public DataHandler(DataServer serverInstance) {
            this.serverInstance = serverInstance;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Récupérer les données de la requête
            InputStream inputStream = exchange.getRequestBody();
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder requestData = new StringBuilder();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                requestData.append(new String(buffer, 0, bytesRead));
            }

            // Traiter les données reçues
            String receivedData = requestData.toString();
            System.out.println("Received data: " + receivedData);

            // Accéder à dataStack via l'instance de DataServer 
            Stack<String> dataStack = serverInstance.getDataStack();
            // Ajouter les données à la pile
            dataStack.push(receivedData);

            // Envoyer une réponse au client
            String response = "Data received successfully";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();

            // Afficher les données dans la pile
            serverInstance.printDataStack();
        }
        
    }
}