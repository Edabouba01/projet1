import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Suppression {

    public void supprimerDonnee(Connection connection) {
        try {
            String nomObjet = JOptionPane.showInputDialog(null, "Entrez le nom de l'objet que vous souhaitez supprimer :");

            if (verifier(connection, nomObjet)) {
                String typeAssociation = getType(connection, nomObjet);

                if ("capteurs".equals(typeAssociation)) {
                    supprimerCapteurs(connection, nomObjet);
                } else if ("actuateurs".equals(typeAssociation)) {
                    supprimerActuateurs(connection, nomObjet);
                }

                String requeteSuppression = "DELETE FROM Equipements WHERE nomobjet = ?";
                try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
                    statementSuppression.setString(1, nomObjet);
                    statementSuppression.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Objet supprimé de la table 'Equipements'", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Aucun objet trouvé avec le nom spécifié.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void supprimerCapteurs(Connection connection, String nomObjet) throws SQLException {
        String requeteSuppression = "DELETE FROM capteurs WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
            statementSuppression.setString(1, nomObjet);
            statementSuppression.executeUpdate();
            JOptionPane.showMessageDialog(null, "Capteurs associés à l'objet supprimés de la table 'capteurs'", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void supprimerActuateurs(Connection connection, String nomObjet) throws SQLException {
        String requeteSuppression = "DELETE FROM actuateurs WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
            statementSuppression.setString(1, nomObjet);
            statementSuppression.executeUpdate();
            JOptionPane.showMessageDialog(null, "Actuateurs associés à l'objet supprimés de la table 'actuateurs'", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public boolean verifier(Connection connection, String nomObjet) throws SQLException {
        String requeteVerification = "SELECT id FROM Equipements WHERE nomobjet = ?";
        try (PreparedStatement statementVerification = connection.prepareStatement(requeteVerification)) {
            statementVerification.setString(1, nomObjet);
            try (ResultSet resultSet = statementVerification.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public String getType(Connection connection, String nomObjet) throws SQLException {
        String requeteCapteurs = "SELECT id_Equipements FROM capteurs WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement statementCapteurs = connection.prepareStatement(requeteCapteurs)) {
            statementCapteurs.setString(1, nomObjet);
            try (ResultSet resultSet = statementCapteurs.executeQuery()) {
                if (resultSet.next()) {
                    return "capteurs";
                }
            }
        }

        String requeteActuateurs = "SELECT id_Equipements FROM actuateurs WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement statementActuateurs = connection.prepareStatement(requeteActuateurs)) {
            statementActuateurs.setString(1, nomObjet);
            try (ResultSet resultSet = statementActuateurs.executeQuery()) {
                if (resultSet.next()) {
                    return "actuateurs";
                }
            }
        }

        return "Aucune association";
    }
}
