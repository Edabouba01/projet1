import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Affichage {

    public void Afficherdonnee(Connection connection) {
        try {
            // Requête SQL pour sélectionner tous les utilisateurs et leurs mesures associées
            String query = "SELECT e.id, e.nom_Objet, e.addressIp, m.type_capteur, m.valeur, e.timestamp " +
                           "FROM Objetconnectes e " +
                           "LEFT JOIN Capteurs m ON e.id = m.id_Objetconnectes";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Afficher les résultats
                    System.out.println("Liste de tous les utilisateurs avec leurs mesures associées:\n");
                    System.out.printf("%-5s %-20s %-10s %-20s %-10s %-20s\n", "ID", "Nom objet", "address Ip", "type capteur", "Valeur", "Timestamp");
                    System.out.println("-----------------------------------------------------------------------------------------------------------");

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String nomObjet = resultSet.getString("nom_Objet");
                        int addressIp = resultSet.getInt("addressIp");
                        String typecapteur = resultSet.getString("type_capteur");
                        String valeur = resultSet.getString("valeur");
                        String timestamp = resultSet.getString("timestamp");

                        System.out.printf("%-5d %-20s %-10d %-20s %-10s %-20s\n", id, nomObjet, addressIp, typecapteur, valeur, timestamp);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

