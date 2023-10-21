import com.fazecast.jSerialComm.*;
import java.sql.Connection;
import java.sql.DriverManager;
import com.fazecast.jSerialComm.SerialPort;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import org.postgresql.Driver;

public class TemperatureSensor {

    public static void main(String[] args) {

        String dbUrl = "jdbc:postgresql://localhost:5432/temphum";

        // Créer un objet Properties pour les propriétés de connexion
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "Marikoben10");

        // Définition du port série
        SerialPort serialPort = SerialPort.getCommPort("COM3"); // Remplacez par le port de votre Arduino
        serialPort.setBaudRate(9600);

        // Ouvrir le port série
        if (serialPort.openPort()) {
            System.out.println("Port série ouvert avec succès.");
        } else {
            System.err.println("Impossible d'ouvrir le port série.");
            return;
        }

        // Attente de l'initialisation du port série
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Lecture en continu de données depuis le port série
        try {
            Connection connection = DriverManager.getConnection(dbUrl, properties);

            System.out.println("Connecté à la base de données PostgreSQL");

            while (true) {
                byte[] readBuffer = new byte[20];
                int numRead = serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead > 0) {
                    String data = new String(readBuffer, 0, numRead);
                    System.out.print("Données humidite : " + data);
             

                    // Créez une requête SQL pour insérer les données dans la table "mesuresHum"
                    String insertQuery = "INSERT INTO mesures(humidite, timestamp) VALUES (?, ?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        // Remplacez les ? par les valeurs correspondantes
                        preparedStatement.setString(1, "Sen0018"); // Remplacez par le nom du capteur
                        // Remplacez les ? par les valeurs correspondantes
                        preparedStatement.setString(1, data); // Remplacez par l'humidité mesurée (de type
                                                              // double)
                        preparedStatement.setTimestamp(2, new java.sql.Timestamp(new Date().getTime())); // Utilisez la
                                                                                                         // date
                                                                                                         // actuelle

                        // Exécutez la requête d'insertion
                        preparedStatement.executeUpdate();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                Thread.sleep(1000);
                System.out.println(); // Ajoutez une nouvelle ligne pour la prochaine série de lectures
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fermeture du port série
            serialPort.closePort();
            System.out.println("Port série fermé.");
        }
    }
}
