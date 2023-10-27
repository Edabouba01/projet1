import com.fazecast.jSerialComm.*;/** packages nécessaires à la communication série avec jSerialComm */

/**la gestion de la base de données PostgreSQL */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Date;/**representer la date */
import java.util.Properties;/**gérer des propriétés sous forme de paires clé-valeur utilisée pour la gestion de configurations */
import org.postgresql.Driver;/**permet d’accéder à la classe du pilote PostgreSQL, qui implémente l’interface JDBC () */


/**
 * Le pilote JDBC PostgreSQL permet aux programmes Java de se connecter à une
 * base de données PostgreSQL à l’aide d’une base de données standard code Java
 * indépendant. pgJDBC est un pilote JDBC open source écrit en Java pur (Type
 * 4), et communique dans le répertoire Protocole réseau natif PostgreSQL. Pour
 * cette raison, le conducteur est indépendant de la plate-forme ; Une fois
 * compilés, les Le pilote peut être utilisé sur n’importe quel système
 * 
 * liens :https://jdbc.postgresql.org/
 */

public class TemperatureSensor {

    public static void main(String[] args) {

        String dbUrl = "jdbc:postgresql://localhost:5432/temphum";/**
                                                                   * indiquation de l utilisation du pilote JDBC
                                                                   * PostgreSQL pour établir une connexion.au port 5432
                                                                   * avec la base de donnee temphum
                                                                   */

        // Créer un objet Properties pour les propriétés de connexion
        /**
         * L utilisation de properties a du s imposer car l objet properties sera
         * appeller dans getconnection pour revoyer les donner qui lui ont ete affecter
         */
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "Marikoben10");

        // Définition du port série
        SerialPort serialPort = SerialPort.getCommPort("COM3"); // port de lecture Arduino
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
            Connection connection = DriverManager.getConnection(dbUrl, properties);/**
                                                                                    * Nous utilisons la bibliotheque
                                                                                    * pilote pour etablir la connection
                                                                                    * avec la base de donnee
                                                                                    */

            System.out.println("Connecté à la base de données PostgreSQL");
            /** Lorsque cest connecter ca retourne un message */

            while (true) {
                byte[] readBuffer = new byte[20];/** stocker les données lues dans le port série */
                int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
                /** lire les données depuis le port série avec a longueur maximal desirer */

                /**si longueur est supperieur a 0 alors il ya des donnee */
                if (numRead > 0) {
                    String data = new String(readBuffer, 0, numRead);/**
                                                                      * utilisée pour convertir un tableau d'octets en
                                                                      * une chaîne de caractères en Java
                                                                      */
                    System.out.print("Données humidite : " + data);

                    // requête SQL pour insérer les données dans la table "mesures"
                    String insertQuery = "INSERT INTO mesures(humidite, timestamp) VALUES (?, ?)";
                    /** ouvre un bloc pour gérer automatiquement */
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                        preparedStatement.setString(1, "Sen0018"); // le nom du capteur

                        preparedStatement.setString(1, data); /** configure */
                        preparedStatement.setTimestamp(2, new java.sql.Timestamp(new Date().getTime())); // prendre la
                                                                                                         // date
                                                                                                         // actuelle

                        // Exécutez la requête d'insertion
                        preparedStatement.executeUpdate();

                        /**
                         * gere les exception qui pourrait arrivee lors de la transmission avec postgre
                         */
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                Thread.sleep(1000);
                System.out.println(); // Ajoutez une nouvelle ligne pour la prochaine série de lectures
            }
        } catch (Exception e) {
            e.printStackTrace();// gere les exception
        } finally {
            // Fermeture du port série
            serialPort.closePort();
            System.out.println("Port série fermé.");
        }
    }
}
