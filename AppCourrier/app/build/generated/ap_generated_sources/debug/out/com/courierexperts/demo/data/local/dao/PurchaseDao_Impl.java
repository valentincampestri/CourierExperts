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
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
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
public final class PurchaseDao_Impl implements PurchaseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PurchaseEntity> __insertionAdapterOfPurchaseEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public PurchaseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPurchaseEntity = new EntityInsertionAdapter<PurchaseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `purchases` (`id`,`storeName`,`orderId`,`status`,`createdAt`,`thumbnailUrl`,`pendingSync`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PurchaseEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.storeName == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.storeName);
        }
        if (entity.orderId == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.orderId);
        }
        if (entity.status == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.status);
        }
        if (entity.createdAt == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.createdAt);
        }
        if (entity.thumbnailUrl == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.thumbnailUrl);
        }
        final int _tmp = entity.pendingSync ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM purchases WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM purchases";
        return _query;
      }
    };
  }

  @Override
  public void upsertAll(final List<PurchaseEntity> items) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPurchaseEntity.insert(items);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insert(final PurchaseEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfPurchaseEntity.insertAndReturnId(item);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteById(final long id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteById.release(_stmt);
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
  public LiveData<List<PurchaseEntity>> observeAll() {
    final String _sql = "SELECT * FROM purchases ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"purchases"}, false, new Callable<List<PurchaseEntity>>() {
      @Override
      @Nullable
      public List<PurchaseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStoreName = CursorUtil.getColumnIndexOrThrow(_cursor, "storeName");
          final int _cursorIndexOfOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "orderId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingSync");
          final List<PurchaseEntity> _result = new ArrayList<PurchaseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PurchaseEntity _item;
            _item = new PurchaseEntity();
            _item.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfStoreName)) {
              _item.storeName = null;
            } else {
              _item.storeName = _cursor.getString(_cursorIndexOfStoreName);
            }
            if (_cursor.isNull(_cursorIndexOfOrderId)) {
              _item.orderId = null;
            } else {
              _item.orderId = _cursor.getString(_cursorIndexOfOrderId);
            }
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _item.status = null;
            } else {
              _item.status = _cursor.getString(_cursorIndexOfStatus);
            }
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _item.createdAt = null;
            } else {
              _item.createdAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _item.thumbnailUrl = null;
            } else {
              _item.thumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPendingSync);
            _item.pendingSync = _tmp != 0;
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

  @Override
  public List<PurchaseEntity> listPending() {
    final String _sql = "SELECT * FROM purchases WHERE pendingSync = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfStoreName = CursorUtil.getColumnIndexOrThrow(_cursor, "storeName");
      final int _cursorIndexOfOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "orderId");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
      final int _cursorIndexOfPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingSync");
      final List<PurchaseEntity> _result = new ArrayList<PurchaseEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final PurchaseEntity _item;
        _item = new PurchaseEntity();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfStoreName)) {
          _item.storeName = null;
        } else {
          _item.storeName = _cursor.getString(_cursorIndexOfStoreName);
        }
        if (_cursor.isNull(_cursorIndexOfOrderId)) {
          _item.orderId = null;
        } else {
          _item.orderId = _cursor.getString(_cursorIndexOfOrderId);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _item.createdAt = null;
        } else {
          _item.createdAt = _cursor.getString(_cursorIndexOfCreatedAt);
        }
        if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
          _item.thumbnailUrl = null;
        } else {
          _item.thumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfPendingSync);
        _item.pendingSync = _tmp != 0;
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
