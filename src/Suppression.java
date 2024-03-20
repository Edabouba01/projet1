import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Suppression {

    public void supprimerDonnee(Connection connection, String nomObjet) {
        try {
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
                    System.out.println("Objet supprimé de la table 'Equipements'");
                }
            } else {
                System.out.println("Aucun objet trouvé avec le nom spécifié.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void supprimerCapteurs(Connection connection, String nomObjet) throws SQLException {
        String requeteSuppression = "DELETE FROM capteurs WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
            statementSuppression.setString(1, nomObjet);
            statementSuppression.executeUpdate();
            System.out.println("Capteurs associés à l'objet supprimés de la table 'capteurs'");
        }
    }

    public void supprimerActuateurs(Connection connection, String nomObjet) throws SQLException {
        String requeteSuppression = "DELETE FROM actuateurs WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
            statementSuppression.setString(1, nomObjet);
            statementSuppression.executeUpdate();
            System.out.println("Actuateurs associés à l'objet supprimés de la table 'actuateurs'");
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