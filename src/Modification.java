import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Scanner;

public class Modification {

    int prends;
    String newNomObjet;
    int newAdresseIP;

    public void mettreAJour(Connection connection, Scanner scanner) {
        try {
            System.out.print("Entrez l'ID de l'objet que vous souhaitez mettre à jour : ");

            while (!scanner.hasNextInt()) {
                System.out.println("Entrez une valeur valide (entier 1 à ...)");
                scanner.next();
            prends = scanner.nextInt();
            scanner.nextLine();

            // Vérifier si l'objet avec l'ID spécifié existe
            if (verifi(connection, prends)) {
                // Demander les nouvelles informations
                System.out.println("Entrez les nouvelles informations :");
                System.out.print("Nouveau nom objet: ");
                newNomObjet = scanner.nextLine();

                // Validation de l'adresse IP
                System.out.print("Entrez la nouvelle adresse IP : ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Veuillez entrer une adresse IP valide (entier).");
                    scanner.next(); 
                }
                newAdresseIP = scanner.nextInt();
                scanner.nextLine(); 

                // Mettre à jour les informations dans la table enregistrements
                String modification = "UPDATE enregistrements SET nomobjet = ?, addressip = ? WHERE id = ?";
                try (PreparedStatement modif = connection.prepareStatement(modification)) {
                    modif.setString(1, newNomObjet);
                    modif.setInt(2, newAdresseIP);
                    modif.setInt(3, prends);
                    modif.executeUpdate();
                    System.out.println("Informations de l'objet mises à jour avec succès dans la table 'enregistrements'.");
                }

                // Vérifier si l'objet est associé à un capteur
                if (verifiCapteur(connection, prends)) {
                    // Mise à jour des informations dans la table capteurs
                    String modificationCapteur = "UPDATE capteurs SET typemesure = ?, valeur = ? WHERE id_enregistrements = ?";
                    try (PreparedStatement modifCapteur = connection.prepareStatement(modificationCapteur)) {
                        System.out.print("Nouveau type de mesure : ");
                        modifCapteur.setString(1, scanner.nextLine());

                        System.out.print("Nouvelle valeur : ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Veuillez entrer une valeur valide (entier 1 à ...)");
                            scanner.next();
                        }
                        modifCapteur.setInt(2, scanner.nextInt());
                        scanner.nextLine(); 

                        modifCapteur.setInt(3, prends);
                        modifCapteur.executeUpdate();
                        System.out.println("Informations du capteur mises à jour.");
                    }
                }

                // Vérifier si l'objet est associé à un actuateur
                if (verifiActionneur(connection, prends)) {
                    // Mise à jour des informations dans la table actuateurs
                    String modificationActuateur = "UPDATE actuateurs SET type_action = ? WHERE id_enregistrements = ?";
                    try (PreparedStatement modifActuateur = connection.prepareStatement(modificationActuateur)) {
                        System.out.print("Nouvelle action : ");
                        modifActuateur.setString(1, scanner.nextLine());

                        modifActuateur.setInt(2, prends);
                        modifActuateur.executeUpdate();
                        System.out.println("Informations de l'actuateur mises à jour ");
                    }
                }

            } else {
                System.out.println("Aucun objet trouvé avec l'ID spécifié");
            }}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean verifi(Connection connection, int objetId) throws SQLException {
        String query = "SELECT id FROM enregistrements WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, objetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public static boolean verifiCapteur(Connection connection, int objetId) throws SQLException {
        String query = "SELECT id_enregistrements FROM capteurs WHERE id_enregistrements = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, objetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public static boolean verifiActionneur(Connection connection, int objetId) throws SQLException {
        String query = "SELECT id_enregistrements FROM actuateurs WHERE id_enregistrements = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, objetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
