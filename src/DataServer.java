import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.Stack;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        DataServer server = new DataServer();
        server.start();
        System.out.println("\nServeur démarré à : " + server.getServerAddress());
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/data", new DataHandler());
        server.createContext("/ajouter-donnees", new AjouterDonneesHandler());
        server.createContext("/supprimer-donnee", new SuppressionHandler());
        server.createContext("/modifierCapteur", new ModifierCapteurHandler());
        server.setExecutor(null);
        server.start();
    }

    public String getServerAddress() {
        return "http://localhost:" + PORT + "/data";
    }

    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";

            try {
                // Récupérer les données de la table "capteurs"
                JSONArray data = getDataFromDatabase();

                // Convertir les données en format JSON
                response = data.toString();
            } catch (JSONException | SQLException e) {
                e.printStackTrace();
                response = "{\"error\": \"Une erreur s'est produite lors de la récupération des données.\"}";
            }

            // Configuration des en-têtes CORS pour autoriser toutes les origines (à des fins de test seulement)
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

            // Configuration des en-têtes de la réponse
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());

            // Envoi de la réponse au client
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }

        // Méthode pour récupérer les données de la table "capteurs" depuis la base de données
        private JSONArray getDataFromDatabase() throws JSONException, SQLException {
            JSONArray jsonArray = new JSONArray();

           // Connexion à la base de données
// Connexion à la base de données
try (Connection connection = new Connexion().renvoi()) {
    String query = "SELECT timestamp, valeur, typemesure FROM capteurs WHERE typemesure = 'Moisissure'";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                // Création d'un objet JSON pour chaque ligne de la table
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", resultSet.getString("timestamp"));
                jsonObject.put("valeur", resultSet.getInt("valeur"));
                jsonObject.put("typemesure", resultSet.getString("typemesure"));
                jsonArray.put(jsonObject);
            }
        }
    }
}

// Connexion à la base de données
try (Connection connection = new Connexion().renvoi()) {
    String query = "SELECT timestamp, valeur, typemesure FROM capteurs WHERE typemesure = 'Capteur de lumiere'";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                // Création d'un objet JSON pour chaque ligne de la table
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", resultSet.getString("timestamp"));
                jsonObject.put("valeur", resultSet.getInt("valeur"));
                jsonObject.put("typemesure", resultSet.getString("typemesure"));
                jsonArray.put(jsonObject);
            }
        }
    }
}

// Connexion à la base de données
try (Connection connection = new Connexion().renvoi()) {
    String query = "SELECT timestamp, valeur, typemesure FROM capteurs WHERE typemesure = 'Capteur de son'";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                // Création d'un objet JSON pour chaque ligne de la table
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", resultSet.getString("timestamp"));
                jsonObject.put("valeur", resultSet.getInt("valeur"));
                jsonObject.put("typemesure", resultSet.getString("typemesure"));
                jsonArray.put(jsonObject);
            }
        }
    }
}
            return jsonArray;
        }
    }

    static class ModifierCapteurHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                // Lire le corps de la requête
                String requestBody = Utils.convertStreamToString(exchange.getRequestBody());
                try {
                    // Analyser les données JSON
                    JSONObject requestData = new JSONObject(requestBody);

                    // Effectuer les opérations de mise à jour dans la base de données pour le capteur
                    // Par exemple :
                    // String newMeasurementType = requestData.getString("newMeasurementType");
                    // int newMeasurementValue = requestData.getInt("newMeasurementValue");
                    // Mettre à jour la base de données en fonction des nouvelles données
                    // ...

                    // Répondre avec un code 200 OK
                    String response = "{\"message\": \"Modification du capteur effectuée avec succès.\"}";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.write(response.getBytes());
                    outputStream.close();
                } catch (JSONException e) {
                    // En cas d'erreur lors de l'analyse des données JSON
                    String errorMessage = "{\"error\": \"Erreur lors de l'analyse des données JSON.\"}";
                    exchange.sendResponseHeaders(400, errorMessage.getBytes().length);
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.write(errorMessage.getBytes());
                    outputStream.close();
                }
            } else {
                // Gérer les autres types de requêtes (GET, PUT, DELETE, etc.) si nécessaire
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }
    static class AjouterDonneesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                // Lire le corps de la requête
                String requestBody = Utils.convertStreamToString(exchange.getRequestBody());
                try {
                    // Analyser les données JSON
                    JSONObject requestData = new JSONObject(requestBody);

                    // Extraire les données du JSON
                    String nomObjet = requestData.getString("nomObjet");
                    String adresseIP = requestData.getString("adresseIP");
                    String type = requestData.getString("type");

                    // Insérer les données dans la base de données en fonction du type spécifié
                    if (type.equals("capteur")) {
                        String typeMesure = requestData.getJSONObject("typeSpecificData").getString("typeMesure");
                        int valeur = requestData.getJSONObject("typeSpecificData").getInt("valeur");
                        insererDonneesCapteur(nomObjet, adresseIP, typeMesure, valeur);
                    } else if (type.equals("actuateur")) {
                        String typeAction = requestData.getJSONObject("typeSpecificData").getString("typeAction");
                        insererDonneesActuateur(nomObjet, adresseIP, typeAction);
                    }

                    // Répondre avec un code 200 OK
                    String response = "{\"message\": \"Données ajoutées avec succès.\"}";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.write(response.getBytes());
                    outputStream.close();
                } catch (JSONException e) {
                    // En cas d'erreur lors de l'analyse des données JSON
                    String errorMessage = "{\"error\": \"Erreur lors de l'analyse des données JSON.\"}";
                    exchange.sendResponseHeaders(400, errorMessage.getBytes().length);
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.write(errorMessage.getBytes());
                    outputStream.close();
                } catch (SQLException e) {
                    // En cas d'erreur lors de l'insertion des données dans la base de données
                    String errorMessage = "{\"error\": \"Erreur lors de l'insertion des données dans la base de données.\"}";
                    exchange.sendResponseHeaders(500, errorMessage.getBytes().length);
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.write(errorMessage.getBytes());
                    outputStream.close();
                }
            } else {
                // Gérer les autres types de requêtes (GET, PUT, DELETE, etc.) si nécessaire
                exchange.sendResponseHeaders(405, -1);
            }
        }

        private void insererDonneesCapteur(String nomObjet, String adresseIP, String typeMesure, int valeur) throws SQLException {
            // Connexion à la base de données
            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/temphum", "postgres", "Marikoben10")) {
                String query = "INSERT INTO capteurs (nomObjet, adresseIP, typeMesure, valeur) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, nomObjet);
                    preparedStatement.setString(2, adresseIP);
                    preparedStatement.setString(3, typeMesure);
                    preparedStatement.setInt(4, valeur);
                    preparedStatement.executeUpdate();
                }
            }
        }

        private void insererDonneesActuateur(String nomObjet, String adresseIP, String typeAction) throws SQLException {
            // Connexion à la base de données
            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/temphum", "postgres", "Marikoben10")) {
                String query = "INSERT INTO actuateurs (nomObjet, adresseIP, typeAction) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, nomObjet);
                    preparedStatement.setString(2, adresseIP);
                    preparedStatement.setString(3, typeAction);
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    
    static class SuppressionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                // Lecture des données de la requête
                String requestBody = Utils.convertStreamToString(exchange.getRequestBody());
                JSONObject requestData = new JSONObject(requestBody);
                String objectName = requestData.getString("nomobjet");
                
                // Connexion à la base de données
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/votre_base_de_donnees", "utilisateur", "mot_de_passe")) {
                    Suppression suppression = new Suppression();
                    suppression.supprimerDonnee(connection);

                    // Répondre avec un message de succès
                    JSONObject response = new JSONObject();
                    response.put("message", "Objet supprimé avec succès");
                    sendResponse(exchange, 200, response.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Répondre avec un message d'erreur
                    JSONObject response = new JSONObject();
                    response.put("error", "Erreur lors de la suppression : " + e.getMessage());
                    sendResponse(exchange, 500, response.toString());
                }
            } else {
                // Méthode HTTP non autorisée
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }
    private void insererCapteur(Connection connection,String nomobjet, String ip, String typeMesure, int valeur) throws SQLException {
        String insertionCapteur = "INSERT INTO Capteurs (id_Equipements, typemesure, valeur, timestamp) VALUES (?, ?, ?, ?)";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertionCapteur)) {
            int idEquipement = recupererIdEquipement(connection, nomobjet, ip);
    
            if (idEquipement == -1) {
                // Si l'équipement n'existe pas, insérez-le dans la table Equipements
                idEquipement = insererEquipement(connection, nomobjet, ip);
            }
    
            preparedStatement.setInt(1, idEquipement);
            preparedStatement.setString(2, typeMesure);
            preparedStatement.setInt(3, valeur);
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
    
            System.out.println("Donnée insérée avec succès dans la base de données.\n");
        }
    }

    private int recupererIdEquipement(Connection connection, String nomObjet, String ip) throws SQLException {
        String selectIdEquipement = "SELECT id FROM Equipements WHERE nomobjet = ? AND addressip = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectIdEquipement)) {
            preparedStatement.setString(1, nomObjet);
            preparedStatement.setString(2, ip);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1;
    }

    private int insererEquipement(Connection connection, String nomObjet, String ip) throws SQLException {
        String insertionEquipement = "INSERT INTO Equipements (nomobjet, addressIp) VALUES (?, ?) RETURNING id";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertionEquipement)) {
            preparedStatement.setString(1, nomObjet);
            preparedStatement.setString(2, ip);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1;
    }
    
    // Nouvelle méthode pour supprimer les données
public void supprimerDonnees(String nomObjet, Stack<String> dataStack) {
    synchronized (dataStack) {
        try (Connection connection = new Connexion().renvoi()) {
            // Supprimer les données de la base de données en fonction du nom de l'objet
            String requeteSuppression = "DELETE FROM Capteurs WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
            try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
                statementSuppression.setString(1, nomObjet);
                statementSuppression.executeUpdate();
            }
            System.out.println("Données supprimées avec succès de la base de données.\n");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression des données : " + e.getMessage());
        }
    }
}

    // Méthode utilitaire pour envoyer la réponse au client
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }

    // Classe utilitaire pour convertir le corps de la requête en chaîne de caractères
    static class Utils {
        public static String convertStreamToString(java.io.InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
    
}
