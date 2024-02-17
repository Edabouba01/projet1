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
        int donnee;
        // Créer une pile pour stocker les données 
        Stack<String> donneesProvenance = new Stack<>();

        // Ppile instance de la classe Ajout
        Ajout ajout = new Ajout();
        ajout.afficherDonneesPile(donneesProvenance);

        Connection connection = connect.renvoi(); // Obtenir la connexion avec la base de données et créer les tables
        boolean exit = false;

        while (!exit) {
            System.out.println("\n\n---------------------------------------------------------------");
            System.out.println("\n Menu Principal de gestion\n ");
            System.out.println("1- Entrez 1 pour ajouter des nouveaux donnees ");
            System.out.println("2- Entrez 2 pour afficher les donnees enregistrees  ");
            System.out.println("3- Entrez 3 pour Supprimer un objet");
            System.out.println("4- Entrez 4 pour effectuer des modifications sur les données ");
            System.out.println(" \n----------------------------------------------------------------- \n");
            System.out.println("Cliquez ici pour faire un choix :   ");

            // vérification pour éviter les erreurs
            if (scanner.hasNextInt()) {
                donnee = scanner.nextInt();
                scanner.nextLine();

                switch (donnee) {
                    case 1:
                        objetAj.ajouterDonnee(connection, scanner);

                        objetAj.simulerDonnees(connection, "temperature", donneesProvenance);
                       
                        break;
                    case 2:
                        objtAff.afficherEnregistrements(connection);
                        objtAff.afficherCapteurs(connection);
                        objtAff.afficherActuateurs(connection);

                        ajout.afficherDonneesPile(donneesProvenance);
                        ;
                        break;
                    case 3:
                        objetspp.supprimerDonnee(connection, scanner);
                        break;
                    case 4:
                        objetMod.mettreAJour(connection, scanner);
                        break;
                    case 5:
                        exit = true;
                        break;
                    default:
                        System.out.println("Entrez un nombre entre 1 et 4");
                }
            } else {
                System.out.println("Entrez un nombre valide entre 1 et 4");
                scanner.next();
            }
        }
    }
}
