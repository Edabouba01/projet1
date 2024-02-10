import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;  
import java.sql.SQLException;
import java.util.Scanner;
public class Modification {

    int prends;

    public void MettreJour (Connection connection, Scanner scanner) {
        try {
            System.out.print("Entrez l'ID de l'utilisateur que vous souhaitez mettre à jour : ");
            prends = scanner.nextInt();
            scanner.nextLine();

            // Vérifier si l'appareil avec l'ID spécifié existe
            if (verifi(connection, prends)) {
                // Demander les nouvelles informations
                System.out.println("Entrez les nouvelles informations :");
                System.out.print("Nouveau nom objet : ");
                String newNomComplet = scanner.nextLine();

                System.out.print("Nouvelle addressIp : ");
                int newMatricule = scanner.nextInt();
                scanner.nextLine();

                // Mettre à jour les informations dans la table 'enregistrements'
                String modification = "UPDATE objetconnectes SET nom_Objet = ?, addressIp = ?, timestamp = CURRENT_TIMESTAMP WHERE id = ?";
try (PreparedStatement modif = connection.prepareStatement(modification)) {
    modif.setString(1, newNomComplet);
    modif.setInt(2, newMatricule);
    modif.setInt(3, prends);
    modif.executeUpdate();
    System.out.println("Informations de l'utilisateur mises à jour avec succès dans la table objetconnectee.");
}



                // Mettre à jour les mesures associées à l'utilisateur dans la table 'mesures'
                mettreJourM(connection, scanner);

            } else {
                System.out.println("Aucun utilisateur trouvé avec l'ID spécifié.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mettreJourM(Connection connection, Scanner scanner) {
        try {
            // Demander les nouvelles informations pour les mesures
            System.out.println("Entrez les nouvelles informations pour les mesures :");
            System.out.print("Nouveau type capteur : ");
            String typeCapteur = scanner.nextLine();

            System.out.print("Nouvelle valeur : ");
            String newValeur = scanner.nextLine();

            // Mettre à jour les mesures dans la table 'mesures'
            String Mesuree = "UPDATE Capteurs SET type_capteur = ?, valeur = ? WHERE id_Objetconnectes= ?";
            try (PreparedStatement ModifiM = connection.prepareStatement(Mesuree)) {
                ModifiM.setString(1, typeCapteur);
                ModifiM.setString(2, newValeur);
                ModifiM.setInt(3, prends);
                ModifiM.executeUpdate();
                System.out.println("Mesures associées à l'utilisateur mises à jour avec succès dans la table 'Capteurs'.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean verifi(Connection connection, int deviceId) throws SQLException {
        // Vérifier si l'appareil avec l'ID spécifié existe dans la table 'enregistrements'
        String query = "SELECT id FROM Objetconnectes WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, deviceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
