package database;

import main.Log;

import java.io.File;
import java.sql.*;

public class DataBase {
    private static Connection connection;
    private static boolean connected = false;

    private static void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        String dataBasePath = System.getenv("APPDATA") + "\\ElwynnProject";
        Log.l("DataBase Path: " + dataBasePath);

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
            String queryString;
            ResultSet result;

            queryString = "SELECT name FROM sqlite_master WHERE type='table' AND name='parameter'";
            result = statement01.executeQuery(queryString);

            if (!result.next()) {
                Statement statement02 = connection.createStatement();
                queryString = "CREATE TABLE IF NOT EXISTS parameter(parameterName varchar(64)," + "parameterValue integer);";
                statement02.execute(queryString);
                Log.l("DataBase initialized!");
                statement02.close();
            }
            statement01.close();
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
//            Log.l(parameterName + "value updated into database!");
        } else {
            insertParameter(parameterName, parameterValue);
//            Log.l(parameterName + "value inserted into database!");
        }
    }

    public static void updateParameter(String parameterName, int parameterValue) {
        if (connection == null) {
            try {
                getConnection();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            Statement statement = connection.createStatement();
            Log.l("Updating parameter. parameterValue = "+ parameterValue + ", parameterName = '" + parameterName + "'");
            statement.executeUpdate("UPDATE parameter SET parameterValue = "+ parameterValue + " WHERE parameterName = '" + parameterName + "';");
            statement.close();
        } catch (SQLException e) {
            Log.e("Error updating parameter. parameterValue = "+ parameterValue + ", parameterName = '" + parameterName + "'");
            e.printStackTrace();
        }
    }

    public static void insertParameter(String parameterName, int parameterValue) {
        if (connection == null) {
            try {
                getConnection();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO parameter values(?,?);");
            statement.setString(1, parameterName);
            statement.setInt(2, parameterValue);
            statement.execute();
            statement.close();
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
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
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
