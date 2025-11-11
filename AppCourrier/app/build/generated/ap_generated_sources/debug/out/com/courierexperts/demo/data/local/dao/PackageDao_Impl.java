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
import com.courierexperts.demo.data.local.entity.PackageEntity;
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
public final class PackageDao_Impl implements PackageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PackageEntity> __insertionAdapterOfPackageEntity;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public PackageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPackageEntity = new EntityInsertionAdapter<PackageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `packages` (`id`,`label`,`description`,`status`,`lastUpdate`,`thumbnailUrl`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PackageEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.label == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.label);
        }
        if (entity.description == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.description);
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
        final String _query = "DELETE FROM packages";
        return _query;
      }
    };
  }

  @Override
  public void upsertAll(final List<PackageEntity> items) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPackageEntity.insert(items);
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
  public LiveData<List<PackageEntity>> observeAll() {
    final String _sql = "SELECT * FROM packages ORDER BY lastUpdate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"packages"}, false, new Callable<List<PackageEntity>>() {
      @Override
      @Nullable
      public List<PackageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfLastUpdate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdate");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final List<PackageEntity> _result = new ArrayList<PackageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PackageEntity _item;
            _item = new PackageEntity();
            _item.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _item.label = null;
            } else {
              _item.label = _cursor.getString(_cursorIndexOfLabel);
            }
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _item.description = null;
            } else {
              _item.description = _cursor.getString(_cursorIndexOfDescription);
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
