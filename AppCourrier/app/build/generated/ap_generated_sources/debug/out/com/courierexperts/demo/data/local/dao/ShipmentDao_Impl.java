package com.courierexperts.demo.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.courierexperts.demo.data.local.entity.ShipmentEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ShipmentDao_Impl implements ShipmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ShipmentEntity> __insertionAdapterOfShipmentEntity;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public ShipmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfShipmentEntity = new EntityInsertionAdapter<ShipmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `shipments` (`id`,`title`,`trackingNumber`,`status`,`lastUpdate`,`thumbnailUrl`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ShipmentEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.title == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.title);
        }
        if (entity.trackingNumber == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.trackingNumber);
        }
        if (entity.status == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.status);
        }
        if (entity.lastUpdate == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.lastUpdate);
        }
        if (entity.thumbnailUrl == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.thumbnailUrl);
        }
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM shipments";
        return _query;
      }
    };
  }

  @Override
  public void upsertAll(final List<ShipmentEntity> items) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfShipmentEntity.insert(items);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clear() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClear.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClear.release(_stmt);
    }
  }

  @Override
  public LiveData<List<ShipmentEntity>> observeAll() {
    final String _sql = "SELECT * FROM shipments ORDER BY lastUpdate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"shipments"}, false, new Callable<List<ShipmentEntity>>() {
      @Override
      @Nullable
      public List<ShipmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfTrackingNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "trackingNumber");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfLastUpdate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdate");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final List<ShipmentEntity> _result = new ArrayList<ShipmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ShipmentEntity _item;
            _item = new ShipmentEntity();
            _item.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _item.title = null;
            } else {
              _item.title = _cursor.getString(_cursorIndexOfTitle);
            }
            if (_cursor.isNull(_cursorIndexOfTrackingNumber)) {
              _item.trackingNumber = null;
            } else {
              _item.trackingNumber = _cursor.getString(_cursorIndexOfTrackingNumber);
            }
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _item.status = null;
            } else {
              _item.status = _cursor.getString(_cursorIndexOfStatus);
            }
            if (_cursor.isNull(_cursorIndexOfLastUpdate)) {
              _item.lastUpdate = null;
            } else {
              _item.lastUpdate = _cursor.getString(_cursorIndexOfLastUpdate);
            }
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _item.thumbnailUrl = null;
            } else {
              _item.thumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
