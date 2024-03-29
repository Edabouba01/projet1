import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Connexion {

    public Connection renvoi() {
        Connection connection = null;

        String dbUrl = "jdbc:postgresql://localhost:5432/temphum"; // chemin pour accéder à la base de données

        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "Marikoben10");

        try {
            connection = DriverManager.getConnection(dbUrl, properties);
            System.out.println("Connecté à la base de données PostgreSQL");

            // Créer la table "enregistrements" pour stocker les informations sur les appareils connectés
            String tableObjet = "CREATE TABLE IF NOT EXISTS Objetconnectes (id SERIAL PRIMARY KEY, nom_Objet VARCHAR(255) NOT NULL, addressIp INT, timestamp TIMESTAMP NOT NULL)";
            try (PreparedStatement prepaObjet = connection.prepareStatement(tableObjet)) {
                prepaObjet.executeUpdate();
                System.out.println("Table Objet Connectee créée avec succès.");
            }
            

            // Créer la table "mesures" pour stocker les données des capteurs
            String tableCapteur = "CREATE TABLE IF NOT EXISTS Capteurs (" +
            "id SERIAL PRIMARY KEY, " +
            "id_Objetconnectes INTEGER REFERENCES Objetconnectes(id), " +
            "type_capteur VARCHAR(255) NOT NULL, " +
            "valeur VARCHAR(255) NOT NULL)";
    try (PreparedStatement prepaCapteur = connection.prepareStatement(tableCapteur)) {
        prepaCapteur.executeUpdate();
        System.out.println("Table 'Capteurs' créée avec succès.");
    }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}