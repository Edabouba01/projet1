import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Connexion {

    // Méthode de connexion à la base de données
    public Connection renvoi() {
        Connection connection = null;

        String dbUrl = "jdbc:postgresql://localhost:5432/temphum"; // chemin pour accéder à la base de données

        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "Marikoben10");

        try {
            connection = DriverManager.getConnection(dbUrl, properties);
            System.out.println("\nConnecté à la base de données PostgreSQL");

            // Créer les tables nécessaires
            createTables(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    // Méthode pour créer les tables
    private void createTables(Connection connection) {
        createUsersTable(connection);
        createAdministratorsTable(connection);
        createObjectsTable(connection);
        createProjectsTable(connection);
        createUserProjectTable(connection);
        createControllersTable(connection);
        createObjectControllerTable(connection);
        createActuatorsTable(connection);
        createCaptorsTable(connection);
        createControllerCaptorsTable(connection);
        createControllerActuatorsTable(connection);
        createProjectControllersTable(connection); // Nouvelle table de liaison
    }

    // Méthode pour créer la table des utilisateurs
    private void createUsersTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS USERS (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL)";
        executeCreateTable(connection, createTableSQL, "USERS");
    }

    // Méthode pour créer la table des administrateurs
    private void createAdministratorsTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS ADMINISTRATORS (" +
                "id SERIAL PRIMARY KEY, " +
                "email VARCHAR(255) NOT NULL, " +
                "name VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL)";
        executeCreateTable(connection, createTableSQL, "ADMINISTRATORS");
    }

    // Méthode pour créer la table des objets
    private void createObjectsTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS OBJET (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT)";
        executeCreateTable(connection, createTableSQL, "OBJET");
    }

    // Méthode pour créer la table des projets
    private void createProjectsTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS PROJECT (" +
                "id SERIAL PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT)";
        executeCreateTable(connection, createTableSQL, "PROJECT");
    }

    // Méthode pour créer la table de liaison entre les utilisateurs et les projets
    private void createUserProjectTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS USER_PROJECT (" +
                "user_id INTEGER REFERENCES USERS(id), " +
                "project_id INTEGER REFERENCES PROJECT(id), " +
                "PRIMARY KEY (user_id, project_id))";
        executeCreateTable(connection, createTableSQL, "USER_PROJECT");
    }

    // Méthode pour créer la table des contrôleurs
    private void createControllersTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS CONTROLLER (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT)";
        executeCreateTable(connection, createTableSQL, "CONTROLLER");
    }

    // Méthode pour créer la table de liaison entre les objets et les contrôleurs
    private void createObjectControllerTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS OBJECT_CONTROLLER (" +
                "object_id INTEGER REFERENCES OBJET(id), " +
                "controller_id INTEGER REFERENCES CONTROLLER(id), " +
                "PRIMARY KEY (object_id, controller_id))";
        executeCreateTable(connection, createTableSQL, "OBJECT_CONTROLLER");
    }

    // Méthode pour créer la table des actionneurs
    private void createActuatorsTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS ACTUATOR (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT)";
        executeCreateTable(connection, createTableSQL, "ACTUATOR");
    }

    // Méthode pour créer la table des capteurs
    private void createCaptorsTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS CAPTOR (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT)";
        executeCreateTable(connection, createTableSQL, "CAPTOR");
    }

    // Méthode pour créer la table de liaison entre les contrôleurs et les capteurs
    private void createControllerCaptorsTable(Connection connection)
    {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS CONTROLLER_CAPTORS (" +
                "id SERIAL PRIMARY KEY, " +
                "controller_id INTEGER REFERENCES CONTROLLER(id), " +
                "captor_id INTEGER REFERENCES CAPTOR(id), " +
                "value VARCHAR(255), " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        executeCreateTable(connection, createTableSQL, "CONTROLLER_CAPTORS");
    }

    // Méthode pour créer la table de liaison entre les contrôleurs et les actionneurs
    private void createControllerActuatorsTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS CONTROLLER_ACTUATORS (" +
                "id SERIAL PRIMARY KEY, " +
                "controller_id INTEGER REFERENCES CONTROLLER(id), " +
                "actuator_id INTEGER REFERENCES ACTUATOR(id), " +
                "status VARCHAR(255), " +
                "values VARCHAR(255), " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        executeCreateTable(connection, createTableSQL, "CONTROLLER_ACTUATORS");
    }

    // Méthode pour créer la table de liaison entre les projets et les contrôleurs
    private void createProjectControllersTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS PROJECT_CONTROLLERS (" +
                "project_id INTEGER REFERENCES PROJECT(id), " +
                "controller_id INTEGER REFERENCES CONTROLLER(id), " +
                "PRIMARY KEY (project_id, controller_id))";
        executeCreateTable(connection, createTableSQL, "PROJECT_CONTROLLERS");
    }

    // Méthode pour ajouter un contrôleur à un projet
    public void addControllerToProject(Connection connection, int projectId, int controllerId) {
        String insertAssignmentSQL = "INSERT INTO PROJECT_CONTROLLERS (project_id, controller_id)VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertAssignmentSQL)) {
            preparedStatement.setInt(1, projectId);
            preparedStatement.setInt(2, controllerId);
            preparedStatement.executeUpdate();
            System.out.println("Contrôleur ajouté au projet avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour exécuter une requête de création de table
    private void executeCreateTable(Connection connection, String createTableSQL, String tableName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
            System.out.println("\nTable '" + tableName + "' créée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
