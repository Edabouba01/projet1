import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Suppression {

    public void supprimerdonnee(Connection connection, Scanner scanner) {
        int id;
        try {
            System.out.print("Entrez l'ID de l'utilisateur que vous souhaitez supprimer : ");
            id = scanner.nextInt();
            scanner.nextLine();
    
            // Vérifier si l'utilisateur avec l'ID spécifié existe
            if (verifi(connection, id)) {
                // Supprimer les mesures associées à l'utilisateur de la table mesures
                supprimerM(connection, id);
    
                // Supprimer l'utilisateur de la table 'enregistrements'
                String deleteQuery = "DELETE FROM Objetconnectes WHERE id = ?";
                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                    deleteStatement.setInt(1, id);
                    deleteStatement.executeUpdate();
                    System.out.println("Utilisateur supprimé de la table enregistrements");
                }
            } else {
                System.out.println("L'utilisateur  n'existe pas.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void supprimerM(Connection connection, int userId) throws SQLException {
        // Supprimer les mesures associées à l'utilisateur dans la table 'mesures'
        String stoc = "DELETE FROM Capteurs WHERE id_Objetconnectes = ?";
        try (PreparedStatement PreparedStatement = connection.prepareStatement(stoc)) {
            PreparedStatement.setInt(1, userId);
            PreparedStatement.executeUpdate();
            System.out.println("Capteurs associées à l'utilisateur supprimées de la table Capteurs");
        }
    }
    
    public  boolean verifi(Connection connection, int deviceId) throws SQLException {
        // Vérifier si l'appareil avec l'ID spécifié existe dans la table
        // 'enregistrements'
        String query = "SELECT id FROM Objetconnectes WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, deviceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

}