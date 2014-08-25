package me.adaptive.arp.impl.data;/*
 * =| ADAPTIVE RUNTIME PLATFORM |=======================================================================================
 *
 * (C) Copyright 2013-2014 Carlos Lozano Diez t/a Adaptive.me <http://adaptive.me>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Original author:
 *
 *     * Carlos Lozano Diez
 *                 <http://github.com/carloslozano>
 *                 <http://twitter.com/adaptivecoder>
 *                 <mailto:carlos@adaptive.me>
 *
 * Contributors:
 *
 *     * Francisco Javier Martin Bueno
 *             <https://github.com/kechis>
 *             <mailto:kechis@gmail.com>
 *
 * =====================================================================================================================
 */

import me.adaptive.arp.api.IDatabase;
import me.adaptive.arp.api.IDatabaseResultCallback;
import me.adaptive.arp.api.ITableResultCallback;
import me.adaptive.arp.api.Table;

public class DatabaseImpl implements IDatabase {

    @Override
    public void createDatabase(me.adaptive.arp.api.Database database, IDatabaseResultCallback callback) {

    }

    @Override
    public void deleteDatabase(me.adaptive.arp.api.Database database, IDatabaseResultCallback callback) {

    }

    @Override
    public boolean existsDatabase(me.adaptive.arp.api.Database database) {
        return false;
    }

    @Override
    public void getDatabase(me.adaptive.arp.api.Database database, IDatabaseResultCallback callback) {

    }

    @Override
    public void createTable(me.adaptive.arp.api.Database database, Table table, ITableResultCallback callback) {

    }

    @Override
    public void deleteTable(me.adaptive.arp.api.Database database, Table table, ITableResultCallback callback) {

    }

    @Override
    public boolean existsTable(me.adaptive.arp.api.Database database, Table table) {
        return false;
    }

    @Override
    public void executeSqlQuery(me.adaptive.arp.api.Database database, String query, String[] replacements, ITableResultCallback callback) {

    }

    @Override
    public void executeSqlStatement(me.adaptive.arp.api.Database database, String statement, String[] replacements, ITableResultCallback callback) {

    }

    @Override
    public void executeSqlTransactions(me.adaptive.arp.api.Database database, String[] statements, boolean rollbackFlag, ITableResultCallback callback) {

    }
}
