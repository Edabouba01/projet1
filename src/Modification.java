import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
public class Modification {

    String objetNom;

    public void mettreAJour(Connection connection) {
        try {
            // Demander le nom de l'objet à mettre à jour
            objetNom = JOptionPane.showInputDialog(null, "Entrez le nom de l'objet à mettre à jour :");

            // Vérifier si l'objet avec le nom spécifié existe
            if (verifi(connection, objetNom)) {
                // Afficher un menu pour choisir le type de mise à jour
                Object[] options = {"Mise à jour des informations de l'équipement", "Mise à jour des informations du capteur", "Mise à jour des informations de l'actuateur", "Mise à jour globale"};
                int choix = JOptionPane.showOptionDialog(null, "Choisissez le type de mise à jour :", "Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                switch (choix) {
                    case 0:
                        // Mise à jour des informations de l'équipement
                        miseAJourEquipement(connection);
                        break;
                    case 1:
                        // Mise à jour des informations du capteur
                        miseAJourCapteur(connection);
                        break;
                    case 2:
                        // Mise à jour des informations de l'actuateur
                        miseAJourActuateur(connection);
                        break;
                    case 3:
                        // Mise à jour des informations de l'équipement, du capteur et de l'actuateur
                        miseAJourEquipement(connection);
                        miseAJourCapteur(connection);
                        miseAJourActuateur(connection);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Aucun objet trouvé avec le nom spécifié.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la mise à jour : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void miseAJourEquipement(Connection connection) throws SQLException {
        // Demander les nouvelles informations de l'équipement
        String newNomObjet = JOptionPane.showInputDialog(null, "Nouveau nom de l'objet :");
        String newAdresseIP = JOptionPane.showInputDialog(null, "Nouvelle adresse IP :");

        // Mettre à jour les informations dans la table 'Equipements'
        String modificationEquipements = "UPDATE Equipements SET nomobjet = ?, addressip = ? WHERE nomobjet = ?";
        try (PreparedStatement modifEquipements = connection.prepareStatement(modificationEquipements)) {
            modifEquipements.setString(1, newNomObjet);
            modifEquipements.setString(2, newAdresseIP);
            modifEquipements.setString(3, objetNom);
            modifEquipements.executeUpdate();
            JOptionPane.showMessageDialog(null, "Informations de l'objet mises à jour avec succès dans la table 'Equipements'.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void miseAJourCapteur(Connection connection) throws SQLException {
        // Demander les nouvelles informations du capteur
        String newTypeMesure = JOptionPane.showInputDialog(null, "Nouveau type de mesure :");
        String newValueString = JOptionPane.showInputDialog(null, "Nouvelle valeur :");
        int newValue = Integer.parseInt(newValueString);

        // Mettre à jour les informations dans la table 'capteurs'
        String modificationCapteur = "UPDATE capteurs SET typemesure = ?, valeur = ? WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement modifCapteur = connection.prepareStatement(modificationCapteur)) {
            modifCapteur.setString(1, newTypeMesure);
            modifCapteur.setInt(2, newValue);
            modifCapteur.setString(3, objetNom);
            modifCapteur.executeUpdate();
            JOptionPane.showMessageDialog(null, "Informations du capteur mises à jour avec succès dans la table 'capteurs'.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void miseAJourActuateur(Connection connection) throws SQLException {
        // Demander la nouvelle action de l'actuateur
        String newTypeAction = JOptionPane.showInputDialog(null, "Nouvelle action :");

        // Mettre à jour les informations dans la table 'actuateurs'
        String modificationActuateur = "UPDATE actuateurs SET type_action = ? WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement modifActuateur = connection.prepareStatement(modificationActuateur)) {
            modifActuateur.setString(1, newTypeAction);
            modifActuateur.setString(2, objetNom);
            modifActuateur.executeUpdate();
            JOptionPane.showMessageDialog(null, "Informations de l'actuateur mises à jour avec succès dans la table 'actuateurs'.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static boolean verifi(Connection connection, String objetNom) throws SQLException {
        String query = "SELECT id FROM Equipements WHERE nomobjet = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, objetNom);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
