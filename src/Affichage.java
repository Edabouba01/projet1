import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;

public class Affichage {

    public static void afficherEnregistrements(Connection connection) {
        String query = "SELECT * FROM Equipements";
        afficherEquipements(connection, query);
    }

    public static void afficherCapteurs(Connection connection) {
        String query = "SELECT capteurs.id, capteurs.id_Equipements, Equipements.nomobjet, Equipements.addressip, capteurs.typemesure, capteurs.valeur, capteurs.timestamp FROM capteurs JOIN Equipements ON capteurs.id_Equipements = Equipements.id";
        afficherCapteursTable(connection, query);
    }

    public static void afficherActuateurs(Connection connection) {
        String query = "SELECT actuateurs.id, actuateurs.id_Equipements, Equipements.nomobjet, Equipements.addressip, actuateurs.type_action, actuateurs.timestamp FROM actuateurs JOIN Equipements ON actuateurs.id_Equipements = Equipements.id";
        afficherActuateursTable(connection, query);
    }

    private static void afficherEquipements(Connection connection, String query) {
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\nAffichage des données de la table 'Equipements':");
            System.out.println("------------------------------------------------");
            System.out.printf("| %-4s | %-20s | %-15s |%n", "ID", "Nom Objet", "Adresse IP");
            System.out.println("------------------------------------------------");
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nomObjet = resultSet.getString("nomobjet");
                String addressIP = resultSet.getString("addressip");
                System.out.printf("| %-4d | %-20s | %-15s |%n", id, nomObjet, addressIP);
            }
            
            System.out.println("------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void afficherCapteursTable(Connection connection, String query) {
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\nAffichage des données de la table 'Capteurs':");
            System.out.println("--------------------------------------------------------------------------------------");
            System.out.printf("| %-4s | %-20s | %-15s | %-15s | %-12s | %-6s |%n",
                    "ID", "Nom Objet", "Adresse IP", "Type de Mesure", "Valeur", "Timestamp");
            System.out.println("--------------------------------------------------------------------------------------");
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nomObjet = resultSet.getString("nomobjet");
                String addressIP = resultSet.getString("addressip");
                String typeMesure = resultSet.getString("typemesure");
                int valeur = resultSet.getInt("valeur");
                String timestamp = resultSet.getString("timestamp");
                
                System.out.printf("| %-4d | %-20s | %-15s | %-15s | %-12d | %-19s |%n",
                        id, nomObjet, addressIP, typeMesure, valeur, timestamp);
            }
            
            System.out.println("--------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void afficherActuateursTable(Connection connection, String query) {
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\nAffichage des données de la table 'Actuateurs':");
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.printf("| %-4s | %-20s | %-15s | %-12s | %-19s |%n",
                    "ID", "Nom Objet", "Adresse IP", "Type d'Action", "Timestamp");
            System.out.println("-----------------------------------------------------------------------------------");
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nomObjet = resultSet.getString("nomobjet");
                String addressIP = resultSet.getString("addressip");
                String typeAction = resultSet.getString("type_action");
                String timestamp = resultSet.getString("timestamp");
                
                System.out.printf("| %-4d | %-20s | %-15s | %-12s | %-19s |%n",
                        id, nomObjet, addressIP, typeAction, timestamp);
            }
            
            System.out.println("-----------------------------------------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afficherDonneesPile(Stack<String> donnees) {
        System.out.println("\nAffichage des données de la pile :\n");

        if (donnees.isEmpty()) {
            System.out.println("La pile est vide.");
        } else {
            int index = 1;
            for (String donnee : donnees) {
                System.out.println("Index " + index++ + ": " + donnee);
            }
        }
    }
}
