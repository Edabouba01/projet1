import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Scanner;

public class Modification {

    String objetNom;

    public void mettreAJour(Connection connection, Scanner scanner) {
        try {
            System.out.print("Entrez le nom de l'objet que vous souhaitez mettre à jour : ");
            objetNom = scanner.nextLine();

            // Vérifier si l'objet avec le nom spécifié existe
            if (verifi(connection, objetNom)) {
                // Demander le type de mise à jour
                System.out.println("Choisissez le type de mise à jour :");
                System.out.println("1. Mise à jour des informations de l'équipement");
                System.out.println("2. Mise à jour des informations du capteur");
                System.out.println("3. Mise à jour des informations de l'actuateur");
                System.out.println("4. Mise à jour des deux");

                int choix = 0;
                while (choix < 1 || choix > 4) {
                    System.out.print("Entrez 1, 2, 3 ou 4 : ");
                    while (!scanner.hasNextInt()) {
                        System.out.println("Veuillez entrer une valeur valide (1, 2, 3 ou 4).");
                        scanner.next(); // Consommer l'entrée invalide
                    }
                    choix = scanner.nextInt();
                    scanner.nextLine(); // Consommer la nouvelle ligne après le nombre
                }

                switch (choix) {
                    case 1:
                        // Mise à jour des informations de l'équipement
                        miseAJourEquipement(connection, scanner);
                        break;
                    case 2:
                        // Mise à jour des informations du capteur
                        miseAJourCapteur(connection, scanner);
                        break;
                    case 3:
                        // Mise à jour des informations de l'actuateur
                        miseAJourActuateur(connection, scanner);
                        break;
                    case 4:
                        // Mise à jour des informations de l'équipement, du capteur et de l'actuateur
                        miseAJourEquipement(connection, scanner);
                        miseAJourCapteur(connection, scanner);
                        miseAJourActuateur(connection, scanner);
                        break;
                }
            } else {
                System.out.println("Aucun objet trouvé avec le nom spécifié.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void miseAJourEquipement(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Mise à jour des informations de l'équipement :");

        // Nouveau nom objet
        System.out.print("Nouveau nom objet: ");
        String newNomObjet = scanner.nextLine();

        // Nouvelle adresse IP
        System.out.print("Entrez la nouvelle adresse IP : ");
        String newAdresseIP = scanner.nextLine();

        // Mettre à jour les informations dans la table 'Equipements'
        String modificationEquipements = "UPDATE Equipements SET nomobjet = ?, addressip = ? WHERE nomobjet = ?";
        try (PreparedStatement modifEquipements = connection.prepareStatement(modificationEquipements)) {
            modifEquipements.setString(1, newNomObjet);
            modifEquipements.setString(2, newAdresseIP);
            modifEquipements.setString(3, objetNom);
            modifEquipements.executeUpdate();
            System.out.println("Informations de l'objet mises à jour avec succès dans la table 'Equipements'.");
        }
    }

    private void miseAJourCapteur(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Mise à jour des informations du capteur :");

        // Nouveau type de mesure
        System.out.print("Nouveau type de mesure : ");
        String newTypeMesure = scanner.nextLine();

        // Nouvelle valeur
        System.out.print("Nouvelle valeur : ");
        while (!scanner.hasNextInt()) {
            System.out.println("Veuillez entrer une valeur valide (entier 1 à ...)");
            scanner.next();
        }
        int newValue = scanner.nextInt();
        scanner.nextLine(); // Consommer la nouvelle ligne après le nombre

        // Mettre à jour les informations dans la table 'capteurs'
        String modificationCapteur = "UPDATE capteurs SET typemesure = ?, valeur = ? WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement modifCapteur = connection.prepareStatement(modificationCapteur)) {
            modifCapteur.setString(1, newTypeMesure);
            modifCapteur.setInt(2, newValue);
            modifCapteur.setString(3, objetNom);
            modifCapteur.executeUpdate();
            System.out.println("Informations du capteur mises à jour avec succès dans la table 'capteurs'.");
        }
    }

    private void miseAJourActuateur(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Mise à jour des informations de l'actuateur :");

        // Nouvelle action
        System.out.print("Nouvelle action : ");
        String newTypeAction = scanner.nextLine();

        // Mettre à jour les informations dans la table 'actuateurs'
        String modificationActuateur = "UPDATE actuateurs SET type_action = ? WHERE id_Equipements IN (SELECT id FROM Equipements WHERE nomobjet = ?)";
        try (PreparedStatement modifActuateur = connection.prepareStatement(modificationActuateur)) {
            modifActuateur.setString(1, newTypeAction);
            modifActuateur.setString(2, objetNom);
            modifActuateur.executeUpdate();
            System.out.println("Informations de l'actuateur mises à jour avec succès dans la table 'actuateurs'.");
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
