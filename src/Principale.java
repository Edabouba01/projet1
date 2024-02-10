import java.sql.Connection;
import java.util.Scanner;

public class Principale {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connexion connect = new Connexion();
        Ajout objetAj = new Ajout();
        Affichage objtAff = new Affichage();
        Suppression objetspp = new Suppression();
        Modification objetMod = new Modification();
        int donnee;
        
            Connection connection = connect.renvoi(); // Obtenir la connexion avec la base de données et créer les tables
            boolean exit = false;

            while (!exit) {
                System.out.println("\n\n---------------------------------------------------------------");
                System.out.println("\n Menu Principal de gestion\n ");
                System.out.println("1- Entrez 1 pour ajouter des nouveaux donnees ");
                System.out.println("2- Entrez 2 pour afficher les donnees enregistrees  ");
                System.out.println("3- Entrez 3 pour Supprimer un utilisateur");
                System.out.println("4- Entrez 4 pour effectuer des modifications sur les données ");
                System.out.println(" \n----------------------------------------------------------------- \n");
                System.out.println("cliquez ici pour faire un choix :   ");
                 donnee = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (donnee) {
                    case 1:
                        objetAj.Ajouterdonnee(connection, scanner);
                        break;
                    case 2:
                        objtAff.Afficherdonnee(connection);
                        break;
                    case 3:
                        objetspp.supprimerdonnee(connection, scanner);
                        break;
                    case 4:
                       objetMod.MettreJour(connection, scanner);
                        break;
                    default:
                        System.out.println("Entrez un nombre entre 1 et 4");
                }
            }

        
    }
}
