import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Stack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class DataServer {

    private static final int PORT = 8080;
    private Stack<String> dataStack = new Stack<>();
    private String nomObjet; // Ajout de la variable nomObjet

    public static void main(String[] args) throws IOException {
        DataServer server = new DataServer();
        server.start();
        System.out.println("Serveur démarré à : " + server.getServerAddress());
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
        System.out.println("Pile de données :");
        for (String data : dataStack) {
            System.out.println(data);
        }
    }

    public void insererDonneesDansLaBaseDeDonnees() {
        synchronized (dataStack) {
            try (Connection connection = new Connexion().renvoi()) {
                while (!dataStack.isEmpty()) {
                    String donnee = dataStack.pop();
                    insererDonneeDansLaBaseDeDonnees(connection, donnee);
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'insertion des données dans la base de données : " + e.getMessage());
            }
        }
    }

    private void insererDonneeDansLaBaseDeDonnees(Connection connection, String donnee) throws SQLException {
        // Divisez la donnée en parties
        String[] parts = donnee.split(",");

        String typeMesure = null;
        int valeur = 0;
        String ip = null;

        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim().replaceAll("\"", "");

                switch (key) {
                    case "Type de Mesure":
                        typeMesure = value;
                        break;
                    case "Valeur":
                        valeur = Integer.parseInt(value);
                        break;
                    case "Nom Objet":
                        nomObjet = value;
                        break;
                    case "Adresse IP":
                        ip = value;
                        break;
                    default:
                        // Gestion d'une clé inconnue (si nécessaire)
                        break;
                }
            }
        }

        if (typeMesure != null && ip != null) {
            insererCapteur(connection, ip, typeMesure, valeur);
        }
    }

    private void insererCapteur(Connection connection, String ip, String typeMesure, int valeur) throws SQLException {
        String insertionCapteur = "INSERT INTO Capteurs (id_Objetconnectes, typemesure, valeur, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertionCapteur)) {
            int idEquipement = recupererIdEquipement(connection, nomObjet);
            preparedStatement.setInt(1, idEquipement);
            preparedStatement.setString(2, typeMesure);
            preparedStatement.setInt(3, valeur);
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
        }
    }

    private int recupererIdEquipement(Connection connection, String nomObjet) throws SQLException {
        String selectIdEquipement = "SELECT id FROM Equipements WHERE nomobjet = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectIdEquipement)) {
            preparedStatement.setString(1, nomObjet);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1; // À adapter en fonction de votre logique de gestion d'erreur
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
            System.out.println("Données reçues : " + receivedData);

            // Accéder à dataStack via l'instance de DataServer
            Stack<String> dataStack = serverInstance.getDataStack();
            // Ajouter les données à la pile
            dataStack.push(receivedData);

            // Envoyer une réponse au client
            String response = "Données reçues avec succès";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();

            // Afficher les données dans la pile
            serverInstance.printDataStack();
        }
    }
}
