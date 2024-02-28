import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.TimerTask;

public class Ajout {

    public void ajouterDonnee(Connection connection, Scanner scanner) {
        try {
            System.out.println("\nAjouter de nouveaux donnees:\n");

            System.out.print("\n Equipement  ");
            System.out.print("Nom objet: ");
            String nomObjet = scanner.nextLine();

            // Validation
            System.out.print("Adresse IP: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Veuillez entrer une adresse IP valide");
                scanner.next(); 
            }
            int AdresseIP = scanner.nextInt();
            scanner.nextLine();

            // Insertion des données dans la table 'Equipements'
            String insC = "INSERT INTO Equipements (nomobjet, addressip) VALUES (?, ?)";
            try (PreparedStatement donneeC = connection.prepareStatement(insC)) {
                donneeC.setString(1, nomObjet);
                donneeC.setInt(2, AdresseIP);
                donneeC.executeUpdate();
                System.out.println("Nouvel objet ajouté avec succès à la table Equipements");
            }

            // Choix entre capteur et actionneur
            boolean exit = false;

            while (!exit) {
                System.out.println("\n\n---------------------------------------------------------------");
                System.out.println("\n Menu de gestion capteur ou actuateur\n ");
                System.out.println("1- Entrez 1 pour ajouter un capteur ");
                System.out.println("2- Entrez 2 pour ajouter un actionneur  ");
                System.out.println(" \n----------------------------------------------------------------- \n");
                System.out.println("Cliquez ici pour faire un choix :   ");

                // Vérification pour éviter les erreurs
                if (scanner.hasNextInt()) {
                    int entree = scanner.nextInt();
                    scanner.nextLine();

                    switch (entree) {
                        case 1:
                            ajouterCapteur(connection, nomObjet, AdresseIP, scanner);
                            exit = true;
                            break;
                        case 2:
                            ajouterActionneur(connection, nomObjet, AdresseIP, scanner);
                            exit = true;
                            break;
                        default:
                            System.out.println("Choisissez 1 ou 2");
                    }
                } else {
                    System.out.println("Choisissez 1 ou 2");
                    scanner.next(); // Éviter une boucle infinie
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout des données : " + e.getMessage());
        }
    }

    private void ajouterCapteur(Connection connection, String nomObjet, int AdresseIP, Scanner scanner) throws SQLException {
        // Insertion des données dans la table capteurs
        System.out.print("Type de mesure : ");
        String typeMesure = scanner.nextLine();

        // Validation
        System.out.print("Valeur : ");
        while (!scanner.hasNextInt()) {
            System.out.println("Veuillez entrer une valeur valide");
            scanner.next();
        }
        int valeur = scanner.nextInt();
        scanner.nextLine();

        // Utilisation de l'héritage
        Capteurs capteur = new Capteurs(dernierIndex(connection), nomObjet, AdresseIP, typeMesure, valeur);
        capteur.ajouter(connection);

        System.out.println("Données insérées dans la table 'capteurs'.");
    }

    private void ajouterActionneur(Connection connection, String nomObjet, int AdresseIP, Scanner scanner) throws SQLException {
        // Insertion des données dans la table actuateurs
        System.out.print("Type d action : ");
        String typeaction = scanner.nextLine();

        // Utilisation de l'héritage
        Actuateur actionneur = new Actuateur(dernierIndex(connection), nomObjet, AdresseIP, typeaction);
        actionneur.ajouter(connection);

        System.out.println("Données insérées dans la table 'actionneurs'.");
    }

    public static int dernierIndex(Connection connection) {
        try {
            // Récupérer le dernier ID inséré dans la table Equipements
            String query = "SELECT MAX(id) FROM Equipements";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du dernier index : " + e.getMessage());
        }
        return -1; 
    }

    public void simulerDonnees(Connection connection, String nomObjet, Stack<String> donnees) {
        Timer timer = new Timer(true);
    
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // generation par random
                    Random random = new Random();
                    int valeur = random.nextInt(100); // Valeur aléatoire entre 0 et 100
    
                    // Générer des données de capteur aléatoires
                    genererDonneesCapteur(connection, nomObjet, valeur);
    
                    // Ajouter les données à la pile
                    ajouterDonnee(donnees, "Simulation l'objet : " + nomObjet + "   Valeur  " + valeur);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60000); // Générer des données chaque minute
    }
    
    private void genererDonneesCapteur(Connection connection, String nomObjet, int valeur) throws SQLException {
        // Ajouter les données de capteur à la table capteurs
        String ajoutDonneesQuery = "INSERT INTO capteurs (id_Equipements, typemesure, valeur, timestamp) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ajoutDonneesStatement = connection.prepareStatement(ajoutDonneesQuery)) {
            ajoutDonneesStatement.setInt(1, dernierIndex(connection));
            ajoutDonneesStatement.setString(2, "temperature");
            ajoutDonneesStatement.setInt(3, valeur);
            ajoutDonneesStatement.executeUpdate();
        }
    }
    private void ajouterDonnee(Stack<String> donnees, String donnee) {
        // Ajouter les données à la pile
        donnees.push(donnee);
    }
    public void afficherDonneesPile(Stack<String> donnees) {
        System.out.println("\nAffichage des données de la pile :\n");
    
        if (donnees.isEmpty()) {
            System.out.println("La pile est vide.");
        } else {
            while (!donnees.isEmpty()) {
                String donnee = donnees.pop();
                System.out.println(donnee);
            }
        }
    }
    
}
