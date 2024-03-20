import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GUIPrincipale {

    private Connection connection;
    private JTextArea textArea; // Composant pour afficher du texte
    private JPanel chartPanel; // Composant pour afficher les graphiques
    private Ajout ajout; // Instance de la classe Ajout
    private Suppression suppression ;
    private Modification modif;
    private Affichage affiche;
    public GUIPrincipale() {
        // Initialiser la connexion à la base de données
        connection = new Connexion().renvoi();
        if (connection == null) {
            System.err.println("Erreur lors de l'initialisation de la connexion à la base de données.");
            System.exit(1); // Quitter le programme en cas d'échec de la connexion
        }
        // Initialiser l'instance de la classe Ajout
        ajout = new Ajout();
        modif  = new Modification(); // Initialisation 
        suppression = new Suppression();
        affiche = new Affichage();
    }

    public void afficherInterfaceGraphique() {
        // Créer la fenêtre principale
        JFrame frame = new JFrame("Menu Principal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(4, 1));

        // Créer les boutons
        JButton ajouterButton = new JButton("Ajouter Donnée");
        JButton afficherButton = new JButton("Afficher Donnée");
        JButton supprimerButton = new JButton("Supprimer Donnée");
        JButton modifierButton = new JButton("Modifier Donnée");

        // Ajouter des ActionListener aux boutons
        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Ajouter une donnée");
                ajout.ajouterDonnee(connection); // Appel de la méthode sur l'instance de Ajout
            }
        });

        afficherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 
                // Logique pour afficher les données et mettre à jour les graphiques
                afficherCapteursEtMettreAJourGraphique();
                affiche.afficherEnregistrements(connection, textArea);
                 affiche.afficherCapteurs(connection, textArea);
                 affiche.afficherCapteurs(connection, textArea);
            }
        });

        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logique pour supprimer une donnée
                JOptionPane.showMessageDialog(null, "Supprimer une donnée");
                
            }
        });

        modifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logique pour modifier une donnée
                JOptionPane.showMessageDialog(null, "Modifier une donnée");
                
            }
        });

        // Ajouter les boutons à la fenêtre
        frame.add(ajouterButton);
        frame.add(afficherButton);
        frame.add(supprimerButton);
        frame.add(modifierButton);

        // Centrer la fenêtre
        frame.setLocationRelativeTo(null);

        // Rendre la fenêtre visible
        frame.setVisible(true);
    }

    private void afficherCapteursEtMettreAJourGraphique() {
        // Créer la fenêtre pour afficher les graphiques
        JFrame frame = new JFrame("Application");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Créer le JTextArea pour afficher du texte
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Créer le JPanel pour afficher les graphiques
        chartPanel = new JPanel();
        chartPanel.setLayout(new GridLayout(1, 3)); // 3 colonnes pour les graphiques
        frame.add(chartPanel, BorderLayout.SOUTH);

        // Afficher les graphiques
        afficherGraphiqueCapteur("Moisissure", "SELECT timestamp, valeur FROM capteurs WHERE typemesure = 'Moisissure'");
        afficherGraphiqueCapteur("Capteur de lumiere", "SELECT timestamp, valeur FROM capteurs WHERE typemesure = 'Capteur de lumiere'");
        afficherGraphiqueCapteur("Capteur de son", "SELECT timestamp, valeur FROM capteurs WHERE typemesure = 'Capteur de son'");

        // Afficher la fenêtre
        frame.setVisible(true);
    }

    private void afficherGraphiqueCapteur(String titre, String requete) {
        DefaultCategoryDataset dataset = recupererDonneesCapteurs(requete);
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Données des Capteurs - " + titre, "Temps", "Valeur",
                dataset);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        this.chartPanel.add(chartPanel);
        this.chartPanel.revalidate();
        this.chartPanel.repaint();
    }

    private DefaultCategoryDataset recupererDonneesCapteurs(String requete) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (PreparedStatement statement = connection.prepareStatement(requete);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String timestamp = resultSet.getString("timestamp");
                int valeur = resultSet.getInt("valeur");
                dataset.addValue(valeur, "Valeur", timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIPrincipale gui = new GUIPrincipale();
            gui.afficherInterfaceGraphique();
        });
    }
}