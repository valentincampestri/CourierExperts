package com.courierexperts.demo.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.courierexperts.demo.data.local.dao.PackageDao;
import com.courierexperts.demo.data.local.dao.PackageDao_Impl;
import com.courierexperts.demo.data.local.dao.PurchaseDao;
import com.courierexperts.demo.data.local.dao.PurchaseDao_Impl;
import com.courierexperts.demo.data.local.dao.ShipmentDao;
import com.courierexperts.demo.data.local.dao.ShipmentDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PurchaseDao _purchaseDao;

  private volatile PackageDao _packageDao;

  private volatile ShipmentDao _shipmentDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `purchases` (`id` INTEGER NOT NULL, `storeName` TEXT, `orderId` TEXT, `status` TEXT, `createdAt` TEXT, `thumbnailUrl` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `packages` (`id` INTEGER NOT NULL, `label` TEXT, `description` TEXT, `status` TEXT, `lastUpdate` TEXT, `thumbnailUrl` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `shipments` (`id` INTEGER NOT NULL, `title` TEXT, `trackingNumber` TEXT, `status` TEXT, `lastUpdate` TEXT, `thumbnailUrl` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '1c37d30b89a29bcd5a3d07ae00073cbd')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `purchases`");
        db.execSQL("DROP TABLE IF EXISTS `packages`");
        db.execSQL("DROP TABLE IF EXISTS `shipments`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPurchases = new HashMap<String, TableInfo.Column>(6);
        _columnsPurchases.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("storeName", new TableInfo.Column("storeName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("orderId", new TableInfo.Column("orderId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("createdAt", new TableInfo.Column("createdAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("thumbnailUrl", new TableInfo.Column("thumbnailUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPurchases = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPurchases = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPurchases = new TableInfo("purchases", _columnsPurchases, _foreignKeysPurchases, _indicesPurchases);
        final TableInfo _existingPurchases = TableInfo.read(db, "purchases");
        if (!_infoPurchases.equals(_existingPurchases)) {
          return new RoomOpenHelper.ValidationResult(false, "purchases(com.courierexperts.demo.data.local.entity.PurchaseEntity).\n"
                  + " Expected:\n" + _infoPurchases + "\n"
                  + " Found:\n" + _existingPurchases);
        }
        final HashMap<String, TableInfo.Column> _columnsPackages = new HashMap<String, TableInfo.Column>(6);
        _columnsPackages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackages.put("label", new TableInfo.Column("label", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackages.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackages.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackages.put("lastUpdate", new TableInfo.Column("lastUpdate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackages.put("thumbnailUrl", new TableInfo.Column("thumbnailUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPackages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPackages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPackages = new TableInfo("packages", _columnsPackages, _foreignKeysPackages, _indicesPackages);
        final TableInfo _existingPackages = TableInfo.read(db, "packages");
        if (!_infoPackages.equals(_existingPackages)) {
          return new RoomOpenHelper.ValidationResult(false, "packages(com.courierexperts.demo.data.local.entity.PackageEntity).\n"
                  + " Expected:\n" + _infoPackages + "\n"
                  + " Found:\n" + _existingPackages);
        }
        final HashMap<String, TableInfo.Column> _columnsShipments = new HashMap<String, TableInfo.Column>(6);
        _columnsShipments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShipments.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShipments.put("trackingNumber", new TableInfo.Column("trackingNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShipments.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShipments.put("lastUpdate", new TableInfo.Column("lastUpdate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShipments.put("thumbnailUrl", new TableInfo.Column("thumbnailUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysShipments = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesShipments = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoShipments = new TableInfo("shipments", _columnsShipments, _foreignKeysShipments, _indicesShipments);
        final TableInfo _existingShipments = TableInfo.read(db, "shipments");
        if (!_infoShipments.equals(_existingShipments)) {
          return new RoomOpenHelper.ValidationResult(false, "shipments(com.courierexperts.demo.data.local.entity.ShipmentEntity).\n"
                  + " Expected:\n" + _infoShipments + "\n"
                  + " Found:\n" + _existingShipments);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "1c37d30b89a29bcd5a3d07ae00073cbd", "198f0286431c7e7f18733794d7fa6519");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "purchases","packages","shipments");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `purchases`");
      _db.execSQL("DELETE FROM `packages`");
      _db.execSQL("DELETE FROM `shipments`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PurchaseDao.class, PurchaseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PackageDao.class, PackageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ShipmentDao.class, ShipmentDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PurchaseDao purchaseDao() {
    if (_purchaseDao != null) {
      return _purchaseDao;
    } else {
      synchronized(this) {
        if(_purchaseDao == null) {
          _purchaseDao = new PurchaseDao_Impl(this);
        }
        return _purchaseDao;
      }
    }
  }

  @Override
  public PackageDao packageDao() {
    if (_packageDao != null) {
      return _packageDao;
    } else {
      synchronized(this) {
        if(_packageDao == null) {
          _packageDao = new PackageDao_Impl(this);
        }
        return _packageDao;
      }
    }
  }

  @Override
  public ShipmentDao shipmentDao() {
    if (_shipmentDao != null) {
      return _shipmentDao;
    } else {
      synchronized(this) {
        if(_shipmentDao == null) {
          _shipmentDao = new ShipmentDao_Impl(this);
        }
        return _shipmentDao;
      }
    }
  }
}
