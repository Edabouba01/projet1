import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;
import javax.swing.JTextArea;

import org.jfree.data.category.DefaultCategoryDataset;

public class Affichage {

    public static void afficherEnregistrements(Connection connection, JTextArea textArea) {
        String query = "SELECT * FROM Equipements";
        afficherEquipements(connection, query, textArea);
    }

    public static void afficherCapteurs(Connection connection, JTextArea textArea) {
        String query = "SELECT capteurs.id, capteurs.id_Equipements, Equipements.nomobjet, Equipements.addressip, capteurs.typemesure, capteurs.valeur, capteurs.timestamp FROM capteurs JOIN Equipements ON capteurs.id_Equipements = Equipements.id";
        afficherCapteursTable(connection, query, textArea);
    }

    public static void afficherActuateurs(Connection connection, JTextArea textArea) {
        String query = "SELECT actuateurs.id, actuateurs.id_Equipements, Equipements.nomobjet, Equipements.addressip, actuateurs.type_action, actuateurs.timestamp FROM actuateurs JOIN Equipements ON actuateurs.id_Equipements = Equipements.id";
        afficherActuateursTable(connection, query, textArea);
    }

    private static void afficherEquipements(Connection connection, String query, JTextArea textArea) {
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Effacez le contenu précédent du JTextArea
            textArea.setText("");

            // Ajoutez le texte d'en-tête
            textArea.append("\nAffichage des données de la table 'Equipements':\n");
            textArea.append("------------------------------------------------\n");
            textArea.append(String.format("| %-4s | %-20s | %-15s |\n", "ID", "Nom Objet", "Adresse IP"));
            textArea.append("------------------------------------------------\n");

            // Parcourez le ResultSet et ajoutez chaque ligne au JTextArea
            boolean anyData = false;
            while (resultSet.next()) {
                anyData = true;
                int id = resultSet.getInt("id");
                String nomObjet = resultSet.getString("nomobjet");
                String addressIP = resultSet.getString("addressip");
                textArea.append(String.format("| %-4d | %-20s | %-15s |\n", id, nomObjet, addressIP));
            }

            // Ajoutez le texte de pied de page si des données sont trouvées
            if (!anyData) {
                textArea.append("Aucun enregistrement trouvé.\n");
            } else {
                textArea.append("------------------------------------------------\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void afficherCapteursTable(Connection connection, String query, JTextArea textArea) {
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Effacez le contenu précédent du JTextArea
            textArea.setText("");

            // Ajoutez le texte d'en-tête
            textArea.append("\nAffichage des données de la table 'Capteurs':\n");
            textArea.append("--------------------------------------------------------------------------------------\n");
            textArea.append(String.format("| %-4s | %-20s | %-15s | %-15s | %-12s | %-6s |\n",
                    "ID", "Nom Objet", "Adresse IP", "Type de Mesure", "Valeur", "Timestamp"));
            textArea.append("--------------------------------------------------------------------------------------\n");

            // Parcourez le ResultSet et ajoutez chaque ligne au JTextArea
            boolean anyData = false;
            while (resultSet.next()) {
                anyData = true;
                int id = resultSet.getInt("id");
                String nomObjet = resultSet.getString("nomobjet");
                String addressIP = resultSet.getString("addressip");
                String typeMesure = resultSet.getString("typemesure");
                int valeur = resultSet.getInt("valeur");
                String timestamp = resultSet.getString("timestamp");
                
                textArea.append(String.format("| %-4d | %-20s | %-15s | %-15s | %-12d | %-19s |\n",
                        id, nomObjet, addressIP, typeMesure, valeur, timestamp));
            }

            // Ajoutez le texte de pied de page si des données sont trouvées
            if (!anyData) {
                textArea.append("Aucun enregistrement trouvé.\n");
            } else {
                textArea.append("--------------------------------------------------------------------------------------\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void afficherActuateursTable(Connection connection, String query, JTextArea textArea) {
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Effacez le contenu précédent du JTextArea
            textArea.setText("");

            // Ajoutez le texte d'en-tête
            textArea.append("\nAffichage des données de la table 'Actuateurs':\n");
            textArea.append("-----------------------------------------------------------------------------------\n");
            textArea.append(String.format("| %-4s | %-20s | %-15s | %-12s | %-19s |\n",
                    "ID", "Nom Objet", "Adresse IP", "Type d'Action", "Timestamp"));
            textArea.append("-----------------------------------------------------------------------------------\n");
            
            // Parcourez le ResultSet et ajoutez chaque ligne au JTextArea
            boolean anyData = false;
            while (resultSet.next()) {
                anyData = true;
                int id = resultSet.getInt("id");
                String nomObjet = resultSet.getString("nomobjet");
                String addressIP = resultSet.getString("addressip");
                String typeAction = resultSet.getString("type_action");
                String timestamp = resultSet.getString("timestamp");
                
                textArea.append(String.format("| %-4d | %-20s | %-15s | %-12s | %-19s |\n",
                        id, nomObjet, addressIP, typeAction, timestamp));
            }

            // Ajoutez le texte de pied de page si des données sont trouvées
            if (!anyData) {
                textArea.append("Aucun enregistrement trouvé.\n");
            } else {
                textArea.append("-----------------------------------------------------------------------------------\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static DefaultCategoryDataset recupererDonneesCapteurs(Connection connection) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String query = "SELECT timestamp, valeur FROM capteurs";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String timestamp = resultSet.getString("timestamp");
                int valeur = resultSet.getInt("valeur");
                dataset.addValue(valeur, "Valeur", timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public static void afficherDonneesPile(Stack<String> donnees, JTextArea textArea) {
        // Effacez le contenu précédent du JTextArea
        textArea.setText("");
        
        textArea.append("\nAffichage des données de la pile :\n");

        if (donnees.isEmpty()) {
            textArea.append("La pile est vide.\n");
        } else {
            int index = 1;
            for (String donnee : donnees) {
                textArea.append("Index " + index++ + ": " + donnee + "\n");
            }
        }
    }
}
