package com.dpcsa.compon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dpcsa.compon.base.BaseDB;
import com.dpcsa.compon.interfaces_classes.DescriptTableDB;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IDbListener;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.ParamDB;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.single.Injector;

public class DatabaseManager extends BaseDB {

    private Context context;
    public ParamDB paramDB;
    public DBHelper dbHelper;
    private int mOpenCounter;
    public SQLiteDatabase mDatabase;
    private AppParams appParams;
    private String tagDB;

    public DatabaseManager(Context context, ParamDB paramDB) {
        this.context = context;
        this.paramDB = paramDB;
        appParams = Injector.getComponGlob().appParams;
        tagDB = appParams.NAME_LOG_DB;
        dbHelper = new DBHelper(context);
    }

    @Override
    public void remoteToLocale(IBase iBase, String url, String table, String nameAlias) {
        new RemoteToLocale(iBase, url, table, nameAlias, dbListener);
    }

    IDbListener dbListener = new IDbListener() {
        @Override
        public void onResponse(IBase iBase, ListRecords listRecords, String table, String nameAlias) {
            insertListRecord(iBase, table, listRecords, nameAlias);
        }
    };

    @Override
    public void insertListRecord(IBase iBase, String table, ListRecords listRecords, String nameAlias) {
        Log.d(tagDB, "insertListRecord TABLE="+table+" SIZE="+listRecords.size());
        String[] columnNames = null;
        String[] aliasNames = null;
        openDatabase();
        mDatabase.beginTransaction();
        int[] columnType;
        Cursor cc = mDatabase.rawQuery("PRAGMA table_info(" + table + ");", null);
        int count = cc.getCount();
        columnNames = new String[count];
        columnType = new int[count];
        int ind = 0;
        if (cc.moveToFirst()) {
            do {
                columnNames[ind] = cc.getString(1);
                int type = 0;
                switch (cc.getString(2).toLowerCase()) {
                    case "integer" :
                        type = Cursor.FIELD_TYPE_INTEGER;
                        break;
                    case "text" :
                        type = Cursor.FIELD_TYPE_STRING;
                        break;
                    case "real" :
                        type = Cursor.FIELD_TYPE_FLOAT;
                        break;
                    case "blob" :
                        type = Cursor.FIELD_TYPE_BLOB;
                        break;
                }
                columnType[ind] = type;
                ind++;
            } while (cc.moveToNext());
        }
        cc.close();

        if (columnNames != null) {
            int jk = columnNames.length;
            aliasNames = new String[jk];
            for (int i = 0; i < jk; i++) {
                aliasNames[i] = columnNames[i];
            }
            if (nameAlias != null && nameAlias.length() > 0) {
                String[] na = nameAlias.split(";");
                for (int i = 0; i < na.length; i++) {
                    String[] na1 = na[i].split(",");
                    String name = na1[0].trim();
                    for (int j = 0; j < jk; j++) {
                        if (aliasNames[j].equals(name)) {
                            aliasNames[j] = na1[1].trim();
                            break;
                        }
                    }
                }
            }
            int ii = 0;
            mDatabase.delete(table, null, null);
            for (Record record : listRecords) {
                ContentValues cv = new ContentValues();
                for (int j = 0; j < jk; j++) {
                    Field f = record.getField(aliasNames[j]);
                    if (f != null) {
                        switch (columnType[j]) {
                            case Cursor.FIELD_TYPE_INTEGER :
                                cv.put(columnNames[j], record.getLongField(f));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT :
                                cv.put(columnNames[j], record.getFloatField(f));
                                break;
                            case Cursor.FIELD_TYPE_STRING :
                                cv.put(columnNames[j], (String) f.value);
                                break;
                        }
                    }
                }
                long rowID = mDatabase.replace(table, null, cv);
            }
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        closeDatabase();
    }

    @Override
    public void deleteRecord(IBase iBase, ParamModel paramModel, String where, String[] param, IPresenterListener listener) {
        openDatabase();
        if (paramModel.method == ParamModel.DEL_DB) {
            if (appParams.LOG_LEVEL > 1) iBase.logDB("deleteRecord table=" + paramModel.updateTable + " WWW=" + where + " param="+param.toString());
            int i = mDatabase.delete(paramModel.updateTable, where, param);
            if (i > -1) {
                if (appParams.LOG_LEVEL > 0)
                    iBase.logDB("deleteRecord table=" + paramModel.updateTable + " удалено записей: " + i);
                if (listener != null) {
                    listener.onResponse(new Field("", Field.TYPE_RECORD, new Record()));
                }
            } else {
                if (listener != null) {
                    listener.onError(404, "deleteRecord error ", null);
                }
            }

        }
        closeDatabase();
    }

    @Override
    public void updateRecord(IBase iBase, ParamModel paramModel, ContentValues cv, String where, String[] param, IPresenterListener listener) {
        openDatabase();
        if (paramModel.method == ParamModel.UPDATE_DB) {
            int i = mDatabase.update(paramModel.updateTable, cv, where, param);
            if (i > -1) {
                if (appParams.LOG_LEVEL > 1)
                    iBase.logDB("updateRecord: table=" + paramModel.updateTable);
                if (appParams.LOG_LEVEL > 2)
                    iBase.logDB("updateRecord количество замененных записей: " + i);
                if (listener != null) {
                    listener.onResponse(new Field("", Field.TYPE_RECORD, new Record()));
                } else {
                    if (listener != null) {
                        listener.onError(404, "updateRecord error ", null);
                    }
                }
            }
        }
        closeDatabase();
    }

    @Override
    public void insertRecord(String sql, Record record, IPresenterListener listener) {
        openDatabase();
        mDatabase.beginTransaction();
        int[] columnType;
        String[] columnNames;
        Cursor cc = mDatabase.rawQuery("PRAGMA table_info(" + sql + ");", null);
        int count = cc.getCount();
        columnNames = new String[count];
        columnType = new int[count];
        int ind = 0;
        if (cc.moveToFirst()) {
            do {
                columnNames[ind] = cc.getString(1);
                int type = 0;
                switch (cc.getString(2).toLowerCase()) {
                    case "integer" :
                        type = Cursor.FIELD_TYPE_INTEGER;
                        break;
                    case "text" :
                        type = Cursor.FIELD_TYPE_STRING;
                        break;
                    case "real" :
                        type = Cursor.FIELD_TYPE_FLOAT;
                        break;
                    case "blob" :
                        type = Cursor.FIELD_TYPE_BLOB;
                        break;
                }
                columnType[ind] = type;
                ind++;
            } while (cc.moveToNext());
        }
        cc.close();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

        ContentValues cv = new ContentValues();
        for (Field f : record) {
            int type = -1;
            for (int i = 0; i < count; i++) {
                if (f.name.equals(columnNames[i])) {
                    type = columnType[i];
                    break;
                }
            }
            if (type == Cursor.FIELD_TYPE_FLOAT) {
                cv.put(f.name, ((String) f.value).replace(",", "."));
            } else {
                cv.put(f.name, (String) f.value);
            }
        }
        try {
            long rowID = mDatabase.insertOrThrow(sql, null, cv);
            if (appParams.LOG_LEVEL > 1) Log.d(tagDB, "insertRecord: " + record);
            if (appParams.LOG_LEVEL > 2) Log.d(tagDB, "insertRecord идентификатор вставленной строки: " + rowID);
            listener.onResponse(new Field("", Field.TYPE_RECORD, record));
        } catch (SQLiteException e) {
            if (appParams.LOG_LEVEL > 0) Log.i(tagDB, "insertRecord error: " + e);
            listener.onError(404,"DatabaseManager insertRecord error: " + e, null);
        }
        closeDatabase();
    }

    @Override
    public void insertCV(String nameTable, ContentValues cv) {
        openDatabase();
        long rowID = mDatabase.insert(nameTable, null, cv);
        closeDatabase();
    }

    @Override
    public void get(IBase iBase, ParamModel paramModel, String[] param, IPresenterListener listener) {
        new GetDbPresenter(iBase, paramModel, param, listener);
    }

    @Override
    public Field get(IBase iBase, ParamModel paramModel, String sql, String[] param) {
//Log.d("QWERT","PPPPPPP="+param+" SSSSS="+param.length+" OOOOO="+param[0]+"<<<<<<");
        if (param != null) {
            String st = "";
            for (String sti : param) {
                st += sti + ",";
            }
            if (appParams.LOG_LEVEL > 1) Log.d(tagDB, "GET SQL=" + sql + "<< param=" + st);
        } else {
            if (appParams.LOG_LEVEL > 1) Log.d(tagDB, "GET SQL=" + sql + "<<");
        }
        openDatabase();
        Cursor c = null;
        try {
            c = mDatabase.rawQuery(sql, param);
        } catch (SQLiteException e) {
            iBase.log(e.getMessage());
        }
        ListRecords listRecords = new ListRecords();
        if (c != null) {
            if (c.moveToFirst()) {
                int countCol = c.getColumnCount();
                String[] nameColumn = c.getColumnNames();
                Record record;
                int row = 0;
                StringBuilder sb = new StringBuilder(1024);
                String sep = "response = [";
                do {
                    record = new Record();
                    for (int i = 0; i < countCol; i++) {
                        switch (c.getType(i)) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                record.add(new Field(nameColumn[i], Field.TYPE_LONG, new Long(c.getLong(i))));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                record.add(new Field(nameColumn[i], Field.TYPE_DOUBLE, c.getDouble(i)));
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                record.add(new Field(nameColumn[i], Field.TYPE_STRING, c.getString(i)));
                                break;
                            default:
                                record.add(new Field(nameColumn[i], Field.TYPE_STRING, c.getString(i)));
                                break;
                        }
                    }
                    if (paramModel.rowName != null && paramModel.rowName.length() > 0) {
                        row++;
                        record.add(new Field(paramModel.rowName, Field.TYPE_LONG, row));
                    }
                    if (appParams.LOG_LEVEL > 2) {
                        sb.append(sep + record.toString());
                        sep = ",\n";
                    }
                    listRecords.add(record);
                } while (c.moveToNext());
                if (appParams.LOG_LEVEL > 2) {
                    sb.append("]");
                    Log.d(tagDB, sb.toString());
                }
            } else {
                Log.d(tagDB, "get 0 rows");
            }
            c.close();
        }
        closeDatabase();
        return new Field("", Field.TYPE_LIST_RECORD, listRecords);
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter++;
        if(mOpenCounter == 1) {
            mDatabase = dbHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter--;
        if(mOpenCounter == 0) {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, paramDB.nameDB, null, paramDB.versionDB);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("QWERT","DBHelper onCreate onCreate");
            createUpgrade(db, 0, 0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("QWERT","DBHelper onUpgrade onUpgrade");
            createUpgrade(db, oldVersion, newVersion);
        }
    }

    private void createUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            for (DescriptTableDB dt : paramDB.listTables) {
                db.execSQL("DROP TABLE IF EXISTS " + dt.nameTable + ";");
                db.execSQL("CREATE TABLE " + dt.nameTable + " (" + dt.descriptTable + ");");
                if (dt.indexName != null && dt.indexName.length() > 0) {
                    db.execSQL("DROP INDEX IF EXISTS " + dt.indexName + ";");
                    db.execSQL("CREATE INDEX IF NOT EXISTS " + dt.indexName + " ON " + dt.nameTable + " (" + dt.indexColumn + ");");
                }
            }
        } catch (SQLiteException e) {
            Log.i(tagDB, "DBHelper Create or Upgrade error: " + e);
        }
    }

}
