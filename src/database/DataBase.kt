package database

import main.Log

import java.io.File
import java.sql.*

class DataBase {

  private var connected = false
  private val connection: Connection by lazy {
    Class.forName("org.sqlite.JDBC")
    val dataBasePath: String = System.getenv("APPDATA") + "\\ElwynnProject"
    Log.l("DataBase Path: $dataBasePath")
    val file = File(dataBasePath)
    if (!file.exists()) file.mkdir()
    DriverManager.getConnection("jdbc:sqlite:$dataBasePath\\elwynn.db")
  }

  init {
    connected = true
    val statement01 = connection.createStatement()
    val queryString = "SELECT name FROM sqlite_master WHERE type='table' AND name='parameter'"
    val result = statement01.executeQuery(queryString)

    if (!result.next()) {
      val statement02 = connection.createStatement()
      val queryString = "CREATE TABLE IF NOT EXISTS parameter(parameterName varchar(64)," + "parameterValue integer);"
      statement02.execute(queryString)
      Log.l("DataBase initialized!")
      statement02.close()
    }
    statement01.close()
  }

  fun selectParameter(parameter: String): Int {
    val parameterValue = -1;
    if (!doestParameterExist(parameter)) {
      return parameterValue
    }

    return executeStatement("SELECT parameterValue FROM parameter WHERE parameterName='$parameter';").getInt("parameterValue")
  }

  fun insertOrUpdateParameter(parameterName: String, parameterValue: Int) {
    if (doestParameterExist(parameterName)) {
      updateParameter(parameterName, parameterValue);
    } else {
      insertParameter(parameterName, parameterValue);
    }
  }

  fun updateParameter(parameterName: String, parameterValue: Int) {
    try {
      val statement = connection.createStatement()
      Log.l("Updating parameter. parameterValue = $parameterValue, parameterName = '$parameterName'")
      statement.executeUpdate("UPDATE parameter SET parameterValue = $parameterValue WHERE parameterName = '$parameterName';")
      statement.close()
    } catch (e: SQLException) {
      Log.e("Error updating parameter. parameterValue = $parameterValue, parameterName = '$parameterName'")
      e.printStackTrace()
    }
  }

  fun insertParameter(parameterName: String, parameterValue: Int) {
    val statement = connection.prepareStatement("INSERT INTO parameter values(?,?);")
    statement.setString(1, parameterName)
    statement.setInt(2, parameterValue)
    statement.execute()
    statement.close()
  }

  fun doestParameterExist(parameterName: String): Boolean {
    return countParameters(parameterName) > 0;
  }

  fun countParameters(parameterName: String): Int {
    return executeStatement("SELECT COUNT(parameterName) FROM parameter WHERE parameterName='$parameterName';").getInt(
      1
    )
  }

  fun executeStatement(queryString: String): ResultSet {
    return connection.createStatement().executeQuery(queryString)
  }
}
