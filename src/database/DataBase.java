package database;

import java.io.File;
import java.sql.*;

public class DataBase {
    private static Connection connection;
    private static boolean connected = false;

    private static void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        String dataBasePath = System.getenv("APPDATA") + "\\ElwynnProject";
        System.out.println("DataBase Path: " + dataBasePath);

        File file = new File(dataBasePath);

        if (file.exists() || file.mkdir()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBasePath + "\\elwynn.db");
            initialise();
        }
    }

    private static void initialise() throws SQLException {
        if (!connected) {
            connected = true;
            Statement statement01 = connection.createStatement();
            String queryString = "SELECT name FROM sqlite_master WHERE type='table' AND name='parameter'";
            ResultSet result = statement01.executeQuery(queryString);

            if (!result.next()) {
                System.out.println("");

                Statement statement02 = connection.createStatement();
                statement02.execute("CREATE TABLE parameter(parameterName varchar(64)," + "parameterValue integer);");
            }
        }
    }

    public static int selectParameter(String parameter) {
        int parameterValue = -1;
        if (!doestParameterExist(parameter)) {
            return parameterValue;
        }

        ResultSet resultSet = executeStatement("SELECT parameterValue FROM parameter WHERE parameterName='" + parameter + "';");
        if (resultSet != null) {
            try {
                parameterValue = resultSet.getInt("parameterValue");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return parameterValue;
    }

    public static void insertOrUpdateParameter(String parameterName, int parameterValue) {
        if (doestParameterExist(parameterName)) {
            updateParameter(parameterName, parameterValue);
        } else {
            insertParameter(parameterName, parameterValue);
        }
    }

    public static void updateParameter(String parameterName, int parameterValue) {
        if (connection == null) {
            try {
                getConnection();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE parameter SET parameterValue = "+ parameterValue + " WHERE parameterName = '" + parameterName + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertParameter(String parameterName, int parameterValue) {
        if (connection == null) {
            try {
                getConnection();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO parameter values(?,?);");
            statement.setString(1, parameterName);
            statement.setInt(2, parameterValue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean doestParameterExist(String parameterName) {
        return countParameters(parameterName) > 0;
    }

    public static int countParameters(String parameterName) {
        ResultSet resultSet = executeStatement("SELECT COUNT(parameterName) FROM parameter WHERE parameterName='" + parameterName + "';");
        if (resultSet != null) {
            try {
                return resultSet.getInt(1);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return 0;
    }

    private static ResultSet executeStatement(String queryString) {
        if (connection == null) {
            try {
                getConnection();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        ResultSet result = null;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeQuery(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
