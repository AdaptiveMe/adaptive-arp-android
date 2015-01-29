/**
 --| ADAPTIVE RUNTIME PLATFORM |----------------------------------------------------------------------------------------

 (C) Copyright 2013-2015 Carlos Lozano Diez t/a Adaptive.me <http://adaptive.me>.

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 . Unless required by appli-
 -cable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  License  for the specific language governing
 permissions and limitations under the License.

 Original author:

 * Carlos Lozano Diez
 <http://github.com/carloslozano>
 <http://twitter.com/adaptivecoder>
 <mailto:carlos@adaptive.me>

 Contributors:

 * Ferran Vila Conesa
 <http://github.com/fnva>
 <http://twitter.com/ferran_vila>
 <mailto:ferran.vila.conesa@gmail.com>

 * See source code files for contributors.

 Release:

 * @version v2.0.3

-------------------------------------------| aut inveniam viam aut faciam |--------------------------------------------
 */

package me.adaptive.arp.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.text.MessageFormat;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.Database;
import me.adaptive.arp.api.DatabaseColumn;
import me.adaptive.arp.api.DatabaseRow;
import me.adaptive.arp.api.DatabaseTable;
import me.adaptive.arp.api.IDatabase;
import me.adaptive.arp.api.IDatabaseResultCallback;
import me.adaptive.arp.api.IDatabaseResultCallbackError;
import me.adaptive.arp.api.IDatabaseResultCallbackWarning;
import me.adaptive.arp.api.IDatabaseTableResultCallback;
import me.adaptive.arp.api.IDatabaseTableResultCallbackError;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Interface for Managing the Cloud operations
 * Auto-generated implementation of IDatabase specification.
 */
public class DatabaseDelegate extends BaseDataDelegate implements IDatabase {


    static LoggingDelegate Logger;
    public String APIService = "database";

    /**
     * Default Constructor.
     */
    public DatabaseDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

    }

    /**
     * Creates a database on default path for every platform.
     *
     * @param callback Asynchronous callback
     * @param database Database object to create
     * @since ARP1.0
     */
    public void createDatabase(Database database, IDatabaseResultCallback callback) {
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "createDatabase: dbName " + database.getName());
        SQLiteDatabase sqlDB = null;
        try {
            Context context = AppContextDelegate.getMainActivity().getApplicationContext();
            sqlDB = context.openOrCreateDatabase(database.getName(), Context.MODE_PRIVATE,
                    null);
            if (!sqlDB.isOpen()) {
                callback.onWarning(database, IDatabaseResultCallbackWarning.IsOpen);
            }

        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "createDatabase: Error " + ex.getLocalizedMessage());
            callback.onError(IDatabaseResultCallbackError.SqlException);
            return;
        } finally {
            closeDatabase(sqlDB);
        }

        Logger.log(ILoggingLogLevel.DEBUG, APIService, "createDatabase: " + database.getName() + " Created!");
        callback.onResult(database);
    }

    /**
     * Creates a databaseTable inside a database for every platform.
     *
     * @param database      Database for databaseTable creating.
     * @param databaseTable DatabaseTable object with the name of the databaseTable inside.
     * @param callback      DatabaseTable callback with the response
     * @since ARP1.0
     */
    public void createTable(Database database, DatabaseTable databaseTable, IDatabaseTableResultCallback callback) {
        SQLiteDatabase sqlDB = null;
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "createTable: " + databaseTable.getName());
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < databaseTable.getDatabaseColumns().length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(databaseTable.getDatabaseColumns()[i]);
            }
            String columns = sb.toString();
            columns = columns.replace("\"", "");

            sqlDB = openDatabase(database);
            if (sqlDB != null) {
                String sql = "CREATE TABLE IF NOT EXISTS " + databaseTable.getName() + " ("
                        + columns + ")";
                sqlDB.execSQL(sql);
            }
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "createTable: Error " + ex.getLocalizedMessage());
            callback.onError(IDatabaseTableResultCallbackError.SqlException);
            return;
        } finally {
            closeDatabase(sqlDB);
        }
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "createTable: " + databaseTable.getName() + " IS created");
        callback.onResult(databaseTable);
    }

    /**
     * Deletes a database on default path for every platform.
     *
     * @param database Database object to delete
     * @param callback Asynchronous callback
     * @since ARP1.0
     */
    public void deleteDatabase(Database database, IDatabaseResultCallback callback) {
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "deleteDatabase: " + database.getName());
        try {
            Context context = AppContextDelegate.getMainActivity().getApplicationContext();
            context.deleteDatabase(database.getName());
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "deleteDatabase: Error " + ex.getLocalizedMessage());
            callback.onError(IDatabaseResultCallbackError.NotDeleted);
            return;
        } finally {

        }
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "deleteDatabase: " + database.getName() + " Deleted!");
        callback.onResult(database);

    }

    /**
     * Deletes a databaseTable inside a database for every platform.
     *
     * @param database      Database for databaseTable removal.
     * @param databaseTable DatabaseTable object with the name of the databaseTable inside.
     * @param callback      DatabaseTable callback with the response
     * @since ARP1.0
     */
    public void deleteTable(Database database, DatabaseTable databaseTable, IDatabaseTableResultCallback callback) {
        SQLiteDatabase sqlDB = null;
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "deleteTable: Deleting Table: " + databaseTable);
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < databaseTable.getDatabaseColumns().length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(databaseTable.getDatabaseColumns()[i]);
            }
            String columns = sb.toString();
            columns = columns.replace("\"", "");

            sqlDB = openDatabase(database);
            if (sqlDB != null) {
                String sql = "DROP TABLE " + databaseTable.getName();
                sqlDB.execSQL(sql);
            }
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "deleteTable: Error " + ex.getLocalizedMessage());
            callback.onError(IDatabaseTableResultCallbackError.SqlException);
            return;
        } finally {
            closeDatabase(sqlDB);
        }
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "deleteTable: " + databaseTable.getName() + " Deleted!");
        callback.onResult(databaseTable);
    }

    /**
     * Executes SQL statement into the given database. The replacements
     * should be passed as a parameter
     *
     * @param database     The database object reference.
     * @param statement    SQL statement.
     * @param replacements List of SQL statement replacements.
     * @param callback     DatabaseTable callback with the response.
     * @since ARP1.0
     */
    public void executeSqlStatement(Database database, String statement, String[] replacements, IDatabaseTableResultCallback callback) {
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "executeSqlStatement: " + String.valueOf(new Object[]{database, statement,
                replacements}));
        String formatedStatement = null;
        SQLiteDatabase sqlDB = null;
        try {
            sqlDB = openDatabase(database);
            if (sqlDB != null) {
                if ((replacements != null) && (replacements.length > 0)) {
                    formatedStatement = getFormattedSQL(statement, replacements);
                }
                sqlDB.execSQL(formatedStatement);

            }
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "executeSqlStatement: Error: " + ex.getLocalizedMessage());
            callback.onError(IDatabaseTableResultCallbackError.SqlException);
            return;
        } finally {
            closeDatabase(sqlDB);
        }
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "executeSqlStatement: " + formatedStatement + " executed!");
        //TODO create table?
        callback.onResult(new DatabaseTable());

    }

    /**
     * Executes SQL transaction (some statements chain) inside given database.
     *
     * @param database     The database object reference.
     * @param statements   The statements to be executed during transaction.
     * @param rollbackFlag Indicates if rollback should be performed when any
     *                     statement execution fails.
     * @param callback     DatabaseTable callback with the response.
     * @since ARP1.0
     */
    public void executeSqlTransactions(Database database, String[] statements, boolean rollbackFlag, IDatabaseTableResultCallback callback) {
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "executeSqlTransactions: " + String.valueOf(new Object[]{database, statements,

                rollbackFlag}));

        boolean result = false, rollback = false;
        SQLiteDatabase sqlDB = null;
        try {
            sqlDB = openDatabase(database);
            if (sqlDB != null) {
                sqlDB.beginTransaction();
                for (String statement : statements) {
                    try {
                        sqlDB.execSQL(statement);
                    } catch (Exception ex) {
                        Logger.log(ILoggingLogLevel.ERROR, APIService, "executeSqlTransactions: " +
                                "ExecuteSQLTransaction error executing sql statement ["
                                + statement + "] " + ex.getLocalizedMessage());
                        if (rollbackFlag) {
                            Logger.log(ILoggingLogLevel.INFO, APIService, "executeSqlTransactions: " +
                                    "Transaction rolled back");
                            rollback = true;
                            break;
                        }
                    }
                }
                if (!rollback) {
                    sqlDB.setTransactionSuccessful();
                    result = true;
                }
            }
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "executeSqlTransactions: Error " + ex.getLocalizedMessage());
            callback.onError(IDatabaseTableResultCallbackError.SqlException);
            return;
        } finally {
            if (sqlDB != null) {
                sqlDB.endTransaction();
            }
            closeDatabase(sqlDB);
        }
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "executeSqlTransactions: SQL Transaction finished");
        //TODO create table?
        callback.onResult(new DatabaseTable());
    }

    /**
     * Checks if database exists by given database name.
     *
     * @param database Database Object to check if exists
     * @return True if exists, false otherwise
     * @since ARP1.0
     */
    public boolean existsDatabase(Database database) {
        boolean result = false;
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "existsDatabase: dbName " + database.getName());
        try {
            Context context = AppContextDelegate.getMainActivity().getApplicationContext();
            String[] databaseNames = context.databaseList();
            for (String dbName : databaseNames) {
                if (database.getName().equals(dbName)) {
                    result = true;
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "existsDatabase: Error " + ex);
        } finally {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "existsDatabase: " + String.valueOf(result));
        }

        return result;
    }

    /**
     * Checks if databaseTable exists by given database name.
     *
     * @param database      Database for databaseTable consulting.
     * @param databaseTable DatabaseTable object with the name of the databaseTable inside.
     * @return True if exists, false otherwise
     * @since ARP1.0
     */
    public boolean existsTable(Database database, DatabaseTable databaseTable) {
        boolean result = false;
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "existsTable: dbName " + databaseTable.getName());
        SQLiteDatabase sqlDB = null;
        try {


            sqlDB = openDatabase(database);
            if (sqlDB != null) {
                Cursor cursor = sqlDB.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + databaseTable.getName() + "'", null);
                if (cursor.getCount() > 0) {
                    cursor.close();
                    result = true;
                }
                cursor.close();
            } else result = false;

        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "existsTable: Error " + ex.toString());
            return false;
        } finally {
            closeDatabase(sqlDB);
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "existsTable: " + databaseTable.getName() + " DOES exist");
        }

        return result;
    }


    //TODO @Override

    /**
     * Execute a query
     *
     * @param database     Adaptive database
     * @param query        String
     * @param replacements values
     * @param callback     Adaptive callback to send the results
     */
    public void executeSqlQuery(Database database, String query, String[] replacements, IDatabaseTableResultCallback callback) {

        String sql = getFormattedSQL(query, replacements);
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "executeSqlQuery: Query: " + sql);
        Cursor cursor = null;
        DatabaseTable result = null;
        try {
            SQLiteDatabase sqlDB = openDatabase(database);
            cursor = sqlDB.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                result = cursorToTable(cursor);
            } else {
                callback.onError(IDatabaseTableResultCallbackError.NoTableFound);
                return;
            }
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.ERROR, APIService, "executeSqlQuery: Error" + ex.getLocalizedMessage());
            callback.onError(IDatabaseTableResultCallbackError.SqlException);
            return;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "executeSqlQuery: " + sql + " executed!");
        callback.onResult(result);
    }

    /**
     * Cast a native Cursor to Adaptive Database Object
     *
     * @param cursor native object
     * @return Adaptive Database
     */
    private DatabaseTable cursorToTable(Cursor cursor) {
        DatabaseTable table = new DatabaseTable();
        MatrixCursor mxcursor;
        String[] columnNames = cursor.getColumnNames();
        int colL = columnNames.length;
        table.setColumnCount(colL);
        DatabaseColumn[] columns = new DatabaseColumn[colL];
        int i;
        for (i = 0; i < colL; i++) {
            columns[i] = new DatabaseColumn(columnNames[i]);
        }
        table.setDatabaseColumns(columns);
        //mxcursor = new MatrixCursor(columnNames, cursor.getCount());
        DatabaseRow[] rows = new DatabaseRow[colL];
        i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Object[] row = new Object[columnNames.length];
            for (String columnName : columnNames) {
                int index = cursor.getColumnIndex(columnName);
                row[index] = cursor.getString(index);
            }
            cursor.moveToNext();
            rows[i++].setValues((String[]) row);
            //mxcursor.addRow(row);

        }
        table.setDatabaseRows(rows);
        cursor.close();
        return table;
    }

    /**
     * Open native database
     *
     * @param db Adaptive database
     * @return the native database Object
     * @throws SQLiteException Exception
     */
    private SQLiteDatabase openDatabase(Database db) throws SQLiteException {
        Context context = AppContextDelegate.getMainActivity().getApplicationContext();
        if (db != null) {
            return SQLiteDatabase.openDatabase(context
                            .getDatabasePath(db.getName()).getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        }

        Logger.log(ILoggingLogLevel.ERROR, APIService, "openDatabase: openDatabase() Given database object is null. Please, check code to provide appropiated database object.");
        return null;
    }

    /**
     * Close opened Database to prevent memory Leaks
     *
     * @param sqlDB native database
     * @throws SQLiteException Exception
     */
    private void closeDatabase(SQLiteDatabase sqlDB) throws SQLiteException {

        if ((sqlDB != null) && (sqlDB.isOpen())) {
            try {
                sqlDB.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Prepare the string as Android database sqlite statement
     *
     * @param sql    statement string
     * @param params values to be replaced
     * @return sqlite-like string properly formatted
     */
    private String getFormattedSQL(String sql, String[] params) {
        String result;

        if (params != null) {
            sql = sql.replaceAll("'", "\"");
            for (int i = 0; i < params.length; i++) {
                if (params[i] != null) {
                    params[i] = params[i].replace("\"", "");
                }
            }
            result = MessageFormat.format(sql, (Object[]) params);
        } else {
            result = sql;
        }

        return result;
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
