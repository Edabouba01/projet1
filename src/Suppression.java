import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Suppression {

    public void supprimerDonnee(Connection connection, Scanner scanner) {
        int id;
        try {
            System.out.print("Entrez l'ID de l'objet que vous souhaitez supprimer : ");

            while (!scanner.hasNextInt()) {
                System.out.println("Veuillez entrer une valeur valide (1 à ...)");
                scanner.next();
            }
            id = scanner.nextInt();
            scanner.nextLine();

            // Vérifier si l'objet avec l'ID spécifié existe
            if (verifier(connection, id)) {
                // Déterminer le type d'association
                String typeAssociation = getType(connection, id);

                // Supprimer les capteurs et actionneurs associés à l'objet
                if ("capteurs".equals(typeAssociation)) {
                    supprimerCapteurs(connection, id);
                } else if ("actuateurs".equals(typeAssociation)) {
                    supprimerActuateurs(connection, id);
                }

                // Supprimer l'objet de la table 'enregistrements'
                String requeteSuppression = "DELETE FROM enregistrements WHERE id = ?";
                try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
                    statementSuppression.setInt(1, id);
                    statementSuppression.executeUpdate();
                    System.out.println("Objet supprimé de la table 'enregistrements'");
                }
            } else {
                System.out.println("Aucun objet trouvé avec l'ID spécifié.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerCapteurs(Connection connection, int idObjet) throws SQLException {
        // Supprimer les capteurs associés à l'objet dans la table 'capteurs'
        String requeteSuppression = "DELETE FROM capteurs WHERE id_enregistrements = ?";
        try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
            statementSuppression.setInt(1, idObjet);
            statementSuppression.executeUpdate();
            System.out.println("Capteurs associés à l'objet supprimés de la table 'capteurs'");
        }
    }

    public void supprimerActuateurs(Connection connection, int idObjet) throws SQLException {
        // Supprimer les actionneurs associés à l'objet dans la table 'actuateurs'
        String requeteSuppression = "DELETE FROM actuateurs WHERE id_enregistrements = ?";
        try (PreparedStatement statementSuppression = connection.prepareStatement(requeteSuppression)) {
            statementSuppression.setInt(1, idObjet);
            statementSuppression.executeUpdate();
            System.out.println("Actionneurs associés à l'objet supprimés de la table 'actuateurs'");
        }
    }

    public boolean verifier(Connection connection, int idObjet) throws SQLException {
        // Vérifier si l'objet avec l'ID spécifié existe dans la table 'enregistrements'
        String requeteVerification = "SELECT id FROM enregistrements WHERE id = ?";
        try (PreparedStatement statementVerification = connection.prepareStatement(requeteVerification)) {
            statementVerification.setInt(1, idObjet);
            try (ResultSet resultSet = statementVerification.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public String getType(Connection connection, int idObjet) throws SQLException {
        // Vérifier si l'objet est associé à des capteurs
        String requeteCapteurs = "SELECT id_enregistrements FROM capteurs WHERE id_enregistrements = ?";
        try (PreparedStatement statementCapteurs = connection.prepareStatement(requeteCapteurs)) {
            statementCapteurs.setInt(1, idObjet);
            try (ResultSet resultSet = statementCapteurs.executeQuery()) {
                if (resultSet.next()) {
                    return "capteurs";
                }
            }
        }

        // Vérifier si l'objet est associé à des actionneurs
        String requeteActuateurs = "SELECT id_enregistrements FROM actuateurs WHERE id_enregistrements = ?";
        try (PreparedStatement statementActuateurs = connection.prepareStatement(requeteActuateurs)) {
            statementActuateurs.setInt(1, idObjet);
            try (ResultSet resultSet = statementActuateurs.executeQuery()) {
                if (resultSet.next()) {
                    return "actuateurs";
                }
            }
        }

        // Si l'objet n'est associé ni à des capteurs ni à des actionneurs
        return "Aucune association";
    }
}
