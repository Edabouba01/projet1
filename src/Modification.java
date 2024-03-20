import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Modification {

    public String mettreAJour(Connection connection, String objectName, String updateType, String newObjectName, String newObjectIP, String newMeasurementType, int newMeasurementValue, String newActionType) {
        try {
            if (verifierObjet(connection, objectName)) {
                switch (updateType) {
                    case "equipement":
                        miseAJourEquipement(connection, objectName, newObjectName, newObjectIP);
                        return "Mise à jour de l'équipement effectuée avec succès.";
                    case "capteur":
                        miseAJourCapteur(connection, objectName, newMeasurementType, newMeasurementValue);
                        return "Mise à jour du capteur effectuée avec succès.";
                    case "actuateur":
                        miseAJourActuateur(connection, objectName, newActionType);
                        return "Mise à jour de l'actuateur effectuée avec succès.";
                    default:
                        return "Type de mise à jour non valide.";
                }
            } else {
                return "Aucun objet trouvé avec le nom spécifié.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur lors de la mise à jour : " + e.getMessage();
        }
    }

    private void miseAJourEquipement(Connection connection, String objectName, String newObjectName, String newObjectIP) throws SQLException {
        String updateQuery = "UPDATE Equipements SET nomobjet = ?, addressip = ? WHERE nomobjet = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newObjectName);
            preparedStatement.setString(2, newObjectIP);
            preparedStatement.setString(3, objectName);
            preparedStatement.executeUpdate();
        }
    }

    private void miseAJourCapteur(Connection connection, String objectName, String newMeasurementType, int newMeasurementValue) throws SQLException {
        String updateQuery = "UPDATE capteurs SET typemesure = ?, valeur = ? WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newMeasurementType);
            preparedStatement.setInt(2, newMeasurementValue);
            preparedStatement.setString(3, objectName);
            preparedStatement.executeUpdate();
        }
    }

    private void miseAJourActuateur(Connection connection, String objectName, String newActionType) throws SQLException {
        String updateQuery = "UPDATE actuateurs SET type_action = ? WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newActionType);
            preparedStatement.setString(2, objectName);
            preparedStatement.executeUpdate();
        }
    }

    private boolean verifierObjet(Connection connection, String objectName) throws SQLException {
        String query = "SELECT id FROM Equipements WHERE nomobjet = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, objectName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
        }
    }}
}
