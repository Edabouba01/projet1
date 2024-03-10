import java.sql.Connection;
import java.util.Scanner;
import java.util.Stack;

public class Principale {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connexion connect = new Connexion();
        Ajout objetAj = new Ajout();
        Affichage objtAff = new Affichage();
        Suppression objetspp = new Suppression();
        Modification objetMod = new Modification();
        int choix;

        // Créer une pile pour stocker les données
        Stack<String> donneesProvenance = new Stack<>();

        // Pile instance de la classe Ajout
        Ajout ajout = new Ajout();
        ajout.afficherDonneesPile(donneesProvenance);

        Connection connection = connect.renvoi(); // Obtenir la connexion avec la base de données et créer les tables
        boolean exit = false;

        System.out.println("Bienvenue dans le Système de Gestion des Données !");
        System.out.println("---------------------------------------------------------------");

        while (!exit) {
            System.out.println("\n Menu Principal de Gestion\n ");
            System.out.println("1- Ajouter de nouvelles données");
            System.out.println("2- Afficher les données enregistrées");
            System.out.println("3- Supprimer un objet");
            System.out.println("4- Effectuer des modifications sur les données");
            System.out.println("5- Afficher la pile de données");
            System.out.println("6- Quitter");
            System.out.println("-----------------------------------------------------------------");

            System.out.print("Faites un choix (1-4) : ");

            if (scanner.hasNextInt()) {
                choix = scanner.nextInt();
                scanner.nextLine(); // Consommer la nouvelle ligne après le nombre

                switch (choix) {
                    case 1:
                        System.out.println("\n------ Ajout de Nouvelles Données ------");
                        objetAj.ajouterDonnee(connection, scanner);
                        objetAj.simulerDonnees(connection, "temperature", donneesProvenance);
                        break;
                    case 2:
                        System.out.println("\n------ Affichage des Données Enregistrées ------");
                        objtAff.afficherEnregistrements(connection);
                        objtAff.afficherCapteurs(connection);
                        objtAff.afficherActuateurs(connection);
                        break;
                    case 3:
                        System.out.println("\n------ Suppression d'Objet ------");
                        objetspp.supprimerDonnee(connection, scanner);
                        break;
                    case 4:
                        System.out.println("\n------ Modification des Données ------");
                        objetMod.mettreAJour(connection, scanner);
                        break;
            
                    default:
                        System.out.println("Entrez un nombre entre 1 et 4");
                }
            } else {
                System.out.println("Entrez un nombre valide entre 1 et 4");
                scanner.next(); // Consommer l'entrée invalide
            }
        }

        scanner.close();
    }
}
