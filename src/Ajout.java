import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.ResultSet;

public class Ajout {

    public void ajouterDonnee(Connection connection) {
        try {
            // Affichage du dialogue pour saisir le nom de l'objet
            String nomObjet = JOptionPane.showInputDialog(null, "Entrez le nom de l'objet:", "Nom de l'objet", JOptionPane.QUESTION_MESSAGE);

            // Affichage du dialogue pour saisir l'adresse IP
            String adresseIPString = JOptionPane.showInputDialog(null, "Entrez l'adresse IP:", "Adresse IP", JOptionPane.QUESTION_MESSAGE);
            int AdresseIP = Integer.parseInt(adresseIPString);

            // Insertion des données dans la table 'Equipements'
            String insEquipement = "INSERT INTO Equipements (nomobjet, addressip) VALUES (?, ?)";
            try (PreparedStatement donneeEquipement = connection.prepareStatement(insEquipement)) {
                donneeEquipement.setString(1, nomObjet);
                donneeEquipement.setInt(2, AdresseIP);
                donneeEquipement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Nouvel objet ajouté avec succès à la table Equipements", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

            // Affichage du menu de gestion capteur ou actionneur
            Object[] options = {"Ajouter un capteur", "Ajouter un actionneur"};
            int choix = JOptionPane.showOptionDialog(null, "Menu de gestion capteur ou actionneur", "Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choix) {
                case 0:
                    ajouterCapteur(connection, nomObjet, AdresseIP);
                    break;
                case 1:
                    ajouterActionneur(connection, nomObjet, AdresseIP);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Choix invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Adresse IP invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout des données : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterCapteur(Connection connection, String nomObjet, int AdresseIP) throws SQLException {
        // Affichage du dialogue pour saisir le type de mesure
        String typeMesure = JOptionPane.showInputDialog(null, "Entrez le type de mesure:", "Type de mesure", JOptionPane.QUESTION_MESSAGE);

        // Affichage du dialogue pour saisir la valeur
        String valeurString = JOptionPane.showInputDialog(null, "Entrez la valeur:", "Valeur", JOptionPane.QUESTION_MESSAGE);
        int valeur = Integer.parseInt(valeurString);

        // Insertion des données dans la table 'capteurs'
        String insCapteur = "INSERT INTO capteurs (id_equipements, typemesure, valeur) VALUES (?, ?, ?)";
        try (PreparedStatement donneeCapteur = connection.prepareStatement(insCapteur)) {
            donneeCapteur.setInt(1, dernierIndex(connection));
            donneeCapteur.setString(2, typeMesure);
            donneeCapteur.setInt(3, valeur);
            donneeCapteur.executeUpdate();
            JOptionPane.showMessageDialog(null, "Données insérées dans la table 'capteurs'", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void ajouterActionneur(Connection connection, String nomObjet, int AdresseIP) throws SQLException {
        // Affichage du dialogue pour saisir le type d'action
        String typeAction = JOptionPane.showInputDialog(null, "Entrez le type d'action:", "Type d'action", JOptionPane.QUESTION_MESSAGE);

        // Insertion des données dans la table 'actuateurs'
        String insActionneur = "INSERT INTO actuateurs (id_equipements, type_action) VALUES (?, ?)";
        try (PreparedStatement donneeActionneur = connection.prepareStatement(insActionneur)) {
            donneeActionneur.setInt(1, dernierIndex(connection));
            donneeActionneur.setString(2, typeAction);
            donneeActionneur.executeUpdate();
            JOptionPane.showMessageDialog(null, "Données insérées dans la table 'actuateurs'", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static int dernierIndex(Connection connection) {
        try {
            // Récupération du dernier ID inséré dans la table Equipements
            String query = "SELECT MAX(id) FROM Equipements";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la récupération du dernier index : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return -1; 
    }
}
