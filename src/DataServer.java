import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.sun.net.httpserver.Headers;
import com.fasterxml.jackson.databind.ObjectMapper;



import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataServer {
    Suppression objetspp = new Suppression();
    private static final int PORT = 8080;
    private Stack<String> dataStack = new Stack<>();
    private String nomObjet;

   

    public static void main(String[] args) throws IOException {
        DataServer server = new DataServer();
        server.start();
        System.out.println("\nServeur démarré à : " + server.getServerAddress());
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/moisisure", new moisisurHandler());
        server.createContext("/son", new sonHandler());
        server.createContext("/lumiere", new lumiereHandler());
        server.createContext("/tip/supprimer", new SupprimerHandler());
        server.createContext("/update", new UpdateHandler());
        server.createContext("/equipements", new EquipementsHandler());
        server.createContext("/data", new DataHandler(this));
        server.createContext("/ajout", new AjoutHandler());
        server.setExecutor(null);
        server.start();
    }

    public String getServerAddress() {
        return "http://localhost:" + PORT +"/data" ;
    }

    public Stack<String> getDataStack() {
        return dataStack;
    }

    public void printDataStack() {
        System.out.println("\nPile de données :");
        for (String data : dataStack) {
            System.out.println( data);
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
                System.out.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            }
        }
    }

    private void insererDonneeDansLaBaseDeDonnees(Connection connection, String donnee) {
        try {
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
                            
                            break;
                    }
                }
            }

            if (typeMesure != null && ip != null) {
                insererCapteur(connection, ip, typeMesure, valeur);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion des données dans la base de données : " + e.getMessage());
        }
    }

    private void insererCapteur(Connection connection, String ip, String typeMesure, int valeur) throws SQLException {
        String insertionCapteur = "INSERT INTO Capteurs (id_Equipements, typemesure, valeur, timestamp) VALUES (?, ?, ?, ?)";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertionCapteur)) {
            int idEquipement = recupererIdEquipement(connection, nomObjet, ip);
    
            if (idEquipement == -1) {
                // Si l'équipement n'existe pas, insérez-le dans la table Equipements
                idEquipement = insererEquipement(connection, nomObjet, ip);
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
            System.out.println("\nDonnées reçues : " + receivedData);

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

            //
            serverInstance.insererDonneesDansLaBaseDeDonnees();
            serverInstance.insererDonneeDansLaBaseDeDonnees(null, response);
           
        }
    }



    //affichage lumiere
    static class lumiereHandler implements HttpHandler {
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

            return jsonArray;
        }
    }

//endpoint total
static class EquipementsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";

        try {
            // Récupérer les données des équipements et des tables associées de la base de données
            JSONArray data = getEquipementsAndTablesFromDatabase();

            // Convertir les données en format JSON
            response = data.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            response = "{\"error\": \"Une erreur s'est produite lors de la récupération des équipements et des tables associées.\"}";
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

    // Méthode pour récupérer les équipements et les tables associées depuis la base de données
    private JSONArray getEquipementsAndTablesFromDatabase() throws SQLException {
        JSONArray jsonArray = new JSONArray();

        try (Connection connection = new Connexion().renvoi()) {
            String query = "SELECT e.id AS equipement_id, e.nomobjet, " +
            "MAX(c.typemesure) AS capteur_typemesure, " +
            "MAX(a.type_action) AS actuateur_type_action " +
            "FROM Equipements e " +
            "LEFT JOIN Capteurs c ON e.id = c.id_Equipements " +
            "LEFT JOIN Actuateurs a ON e.id = a.id_Equipements " +
            "GROUP BY e.id, e.nomobjet";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // Création d'un objet JSON pour chaque équipement et ses tables associées
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("equipement_id", resultSet.getInt("equipement_id"));
                        jsonObject.put("nomobjet", resultSet.getString("nomobjet"));
                        jsonObject.put("capteur_typemesure", resultSet.getString("capteur_typemesure"));
                        jsonObject.put("actuateur_type_action", resultSet.getString("actuateur_type_action"));
                        jsonArray.put(jsonObject);
                    }
                }
            }
        }

        return jsonArray;
    }
}


      //affichage moisisure
    static class moisisurHandler implements HttpHandler {
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


            return jsonArray;
        }
    }



    //affichage son 
    static class sonHandler implements HttpHandler {
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

 


// Modification


static class UpdateHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Répondre OK aux requêtes OPTIONS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
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
            System.out.println("\nDonnées reçues : " + receivedData);

            // Effectuer la mise à jour des données en fonction du type
            String response = updateData(receivedData);

            // Envoyer une réponse au client
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        } else {
            // Méthode non autorisée
            String response = "Méthode non autorisée.";
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(405, response.getBytes().length); // 405 Method Not Allowed
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private String updateData(String data) {
        try {
            // Créer une connexion à la base de données
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/temphum", "postgres", "Marikoben10");

            // Extraire les données de la requête JSON
            // Vous pouvez utiliser une bibliothèque comme Jackson pour parser les données JSON
            // Pour cet exemple, nous allons simplement découper les données
            String[] dataArray = data.split(",");
            String objectName = dataArray[0].split(":")[1].replaceAll("\"", "").trim();
            String updateType = dataArray[1].split(":")[1].replaceAll("\"", "").trim();

            // Effectuer la mise à jour en fonction du type
            String result;
            if (updateType.equals("equipement")) {
                result = updateEquipment(connection, objectName, dataArray);
            } else if (updateType.equals("capteur")) {
                result = updateSensor(connection, objectName, dataArray);
            } else if (updateType.equals("actuateur")) {
                result = updateActuator(connection, objectName, dataArray);
            } else {
                result = "Type de mise à jour non valide.";
            }

            // Fermer la connexion à la base de données
            connection.close();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur lors de la mise à jour : " + e.getMessage();
        }
    }

    private String updateEquipment(Connection connection, String objectName, String[] dataArray) throws SQLException {
        String newObjectName
        = dataArray[2].split(":")[1].replaceAll("\"", "").trim();
        String newObjectIP = dataArray[3].split(":")[1].replaceAll("\"", "").trim();

        // Exécuter la mise à jour dans la base de données
        String updateQuery = "UPDATE Equipements SET nomobjet = ?, addressip = ? WHERE nomobjet = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newObjectName);
            preparedStatement.setString(2, newObjectIP);
            preparedStatement.setString(3, objectName);
            preparedStatement.executeUpdate();
        }

        return "Mise à jour de l'équipement effectuée avec succès.";
    }

    private String updateSensor(Connection connection, String objectName, String[] dataArray) throws SQLException {
        String newMeasurementType = dataArray[2].split(":")[1].replaceAll("\"", "").trim();
        int newMeasurementValue = Integer.parseInt(dataArray[3].split(":")[1].replaceAll("\"", "").trim());

        // Exécuter la mise à jour dans la base de données
        String updateQuery = "UPDATE capteurs SET typemesure = ?, valeur = ? WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newMeasurementType);
            preparedStatement.setInt(2, newMeasurementValue);
            preparedStatement.setString(3, objectName);
            preparedStatement.executeUpdate();
        }

        return "Mise à jour du capteur effectuée avec succès.";
    }

    private String updateActuator(Connection connection, String objectName, String[] dataArray) throws SQLException {
        String newActionType = dataArray[2].split(":")[1].replaceAll("\"", "").trim();

        // Exécuter la mise à jour dans la base de données
        String updateQuery = "UPDATE actuateurs SET type_action = ? WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newActionType);
            preparedStatement.setString(2, objectName);
            preparedStatement.executeUpdate();
        }

        return "Mise à jour de l'actuateur effectuée avec succès.";
    }
}






// SuppressionHandler


static class SupprimerHandler implements HttpHandler {
   

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Répondre OK aux requêtes OPTIONS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.sendResponseHeaders(200, -1);
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Lecture des données envoyées depuis le client
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder dataReceived = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                dataReceived.append(line).append("\n");
            }

            // Traitement des données reçues pour la suppression
            JSONObject requestData = new JSONObject(dataReceived.toString());
            String nomObjet = requestData.optString("nomObjet", "");
            // Affichage des données reçues dans la console
            System.out.println("Données reçues du client : " + nomObjet);

            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/temphum", "postgres", "Marikoben10")) {

                // Ajout des en-têtes CORS
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                Suppression suppression = new Suppression();
                if (suppression.verifier(connection, nomObjet)) {
                    suppression.supprimerDonnee(connection, nomObjet);
                    // Envoyer une réponse au client après la suppression réussie
                    String response = "Données supprimées avec succès";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream outputStream = exchange.getResponseBody()) {
                        outputStream.write(response.getBytes());
                    }
                } else {
                    // Envoyer une réponse au client indiquant que l'objet n'existe pas
                    String errorResponse = "L'objet spécifié n'existe pas.";
                    exchange.sendResponseHeaders(404, errorResponse.getBytes().length);
                    try (OutputStream outputStream = exchange.getResponseBody()) {
                        outputStream.write(errorResponse.getBytes());
                    }
                }
            } catch (SQLException e) {
                // En cas d'erreur, renvoyer une réponse d'erreur au client
                String errorResponse = "Erreur objet inexistant";
                exchange.sendResponseHeaders(500, errorResponse.getBytes().length);
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(errorResponse.getBytes());
                }
            }
        } else {
            // Méthode non autorisée
            String response = "Méthode non autorisée.";
            exchange.sendResponseHeaders(405, response.getBytes().length); // 405 Method Not Allowed
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}



static class AjoutHandler implements HttpHandler {
    private Stack<String> dataStack = new Stack<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Répondre OK aux requêtes OPTIONS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Récupérer les données de la requête et les traiter
            InputStream inputStream = exchange.getRequestBody();
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder requestData = new StringBuilder();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                requestData.append(new String(buffer, 0, bytesRead));
            }
            String receivedData = requestData.toString();
            System.out.println("\nDonnées reçues : " + receivedData);

            // Ajouter les données à la pile
            dataStack.push(receivedData);

            // Envoyer une réponse au client
            String response = "Données reçues avec succès";
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();

            // Insérer les données dans la base de données
            insererDonneesDansLaBaseDeDonnees();
        } else {
            // Méthode non autorisée
            String response = "Méthode non autorisée.";
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(405, response.getBytes().length); // 405 Method Not Allowed
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private void insererDonneesDansLaBaseDeDonnees() {
        synchronized (dataStack) {
            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/temphum", "postgres", "Marikoben10")) {
                while (!dataStack.isEmpty()) {
                    String donnee = dataStack.pop();
                    insererDonneeDansLaBaseDeDonnees(connection, donnee);
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            }
        }
    }

    private void insererDonneeDansLaBaseDeDonnees(Connection connection, String donnee) {
        try {
            JSONObject jsonData = new JSONObject(donnee);
            String nomObjet = jsonData.getString("nomObjet");
            String adresseIP = jsonData.getString("adresseIP");
            String type = jsonData.getString("type");
            String typeMesure = jsonData.getString("typeMesure");
            int valeur = jsonData.getInt("valeur");
            String typeAction = jsonData.getString("typeAction");

            insererEquipement(connection, nomObjet, adresseIP);

            if ("capteur".equals(type)) {
                insererCapteur(connection, nomObjet, typeMesure, valeur);
            } else if ("actionneur".equals(type)) {
                insererActuateur(connection, nomObjet, typeAction);
            }

            System.out.println("Données insérées avec succès dans la base de données.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l’insertion des données dans la base de données : " + e.getMessage());
        }
    }

    private void insererCapteur(Connection connection, String nomObjet, String typeMesure, int valeur) throws SQLException {
        String insertionCapteur = "INSERT INTO Capteurs (id_Equipements, typemesure, valeur, timestamp) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertionCapteur)) {
            int idEquipement = recupererIdEquipement(connection, nomObjet);

            preparedStatement.setInt(1, idEquipement);
            preparedStatement.setString(2, typeMesure);
            preparedStatement.setInt(3, valeur);
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
        }
    }

    private void insererActuateur(Connection connection, String nomObjet, String typeAction) throws SQLException {
        String insertionActuateur = "INSERT INTO Actuateurs (id_Equipements, type_action, timestamp) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertionActuateur)) {
            int idEquipement = recupererIdEquipement(connection, nomObjet);

            preparedStatement.setInt(1, idEquipement);
            preparedStatement.setString(2, typeAction);
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
        }
    }

    private int recupererIdEquipement(Connection connection, String nomObjet) throws SQLException {
        String selectIdEquipement = "SELECT id FROM Equipements WHERE nomobjet = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectIdEquipement)) {
            preparedStatement.setString(1, nomObjet);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1;
    }

    private void insererEquipement(Connection connection, String nomObjet, String adresseIP) throws SQLException {
        String insertionEquipement = "INSERT INTO Equipements (nomobjet, addressip) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertionEquipement)) {
            preparedStatement.setString(1, nomObjet);
            preparedStatement.setString(2, adresseIP);
            preparedStatement.executeUpdate();
        }
    }
}
}