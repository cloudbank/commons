package io.realm;


import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnInfo;
import io.realm.internal.LinkView;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Row;
import io.realm.internal.SharedRealm;
import io.realm.internal.Table;
import io.realm.internal.TableOrView;
import io.realm.internal.android.JsonUtils;
import io.realm.log.RealmLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ColorRealmProxy extends com.anubis.commons.models.Color
    implements RealmObjectProxy, ColorRealmProxyInterface {

    static final class ColorColumnInfo extends ColumnInfo
        implements Cloneable {

        public long idIndex;
        public long timestampIndex;
        public long colorIndex;
        public long colorPhotosIndex;

        ColorColumnInfo(String path, Table table) {
            final Map<String, Long> indicesMap = new HashMap<String, Long>(4);
            this.idIndex = getValidColumnIndex(path, table, "Color", "id");
            indicesMap.put("id", this.idIndex);
            this.timestampIndex = getValidColumnIndex(path, table, "Color", "timestamp");
            indicesMap.put("timestamp", this.timestampIndex);
            this.colorIndex = getValidColumnIndex(path, table, "Color", "color");
            indicesMap.put("color", this.colorIndex);
            this.colorPhotosIndex = getValidColumnIndex(path, table, "Color", "colorPhotos");
            indicesMap.put("colorPhotos", this.colorPhotosIndex);

            setIndicesMap(indicesMap);
        }

        @Override
        public final void copyColumnInfoFrom(ColumnInfo other) {
            final ColorColumnInfo otherInfo = (ColorColumnInfo) other;
            this.idIndex = otherInfo.idIndex;
            this.timestampIndex = otherInfo.timestampIndex;
            this.colorIndex = otherInfo.colorIndex;
            this.colorPhotosIndex = otherInfo.colorPhotosIndex;

            setIndicesMap(otherInfo.getIndicesMap());
        }

        @Override
        public final ColorColumnInfo clone() {
            return (ColorColumnInfo) super.clone();
        }

    }
    private ColorColumnInfo columnInfo;
    private ProxyState proxyState;
    private RealmList<com.anubis.commons.models.Photo> colorPhotosRealmList;
    private static final List<String> FIELD_NAMES;
    static {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("id");
        fieldNames.add("timestamp");
        fieldNames.add("color");
        fieldNames.add("colorPhotos");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    ColorRealmProxy() {
        if (proxyState == null) {
            injectObjectContext();
        }
        proxyState.setConstructionFinished();
    }

    private void injectObjectContext() {
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (ColorColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState(com.anubis.commons.models.Color.class, this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @SuppressWarnings("cast")
    public String realmGet$id() {
        if (proxyState == null) {
            // Called from model's constructor. Inject context.
            injectObjectContext();
        }

        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.idIndex);
    }

    public void realmSet$id(String value) {
        if (proxyState == null) {
            // Called from model's constructor. Inject context.
            injectObjectContext();
        }

        if (proxyState.isUnderConstruction()) {
            // default value of the primary key is always ignored.
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        throw new io.realm.exceptions.RealmException("Primary key field 'id' cannot be changed after object was created.");
    }

    @SuppressWarnings("cast")
    public Date realmGet$timestamp() {
        if (proxyState == null) {
            // Called from model's constructor. Inject context.
            injectObjectContext();
        }

        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.timestampIndex)) {
            return null;
        }
        return (java.util.Date) proxyState.getRow$realm().getDate(columnInfo.timestampIndex);
    }

    public void realmSet$timestamp(Date value) {
        if (proxyState == null) {
            // Called from model's constructor. Inject context.
            injectObjectContext();
        }

        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.timestampIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setDate(columnInfo.timestampIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.timestampIndex);
            return;
        }
        proxyState.getRow$realm().setDate(columnInfo.timestampIndex, value);
    }

    @SuppressWarnings("cast")
    public String realmGet$color() {
        if (proxyState == null) {
            // Called from model's constructor. Inject context.
            injectObjectContext();
        }

        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.colorIndex);
    }

    public void realmSet$color(String value) {
        if (proxyState == null) {
            // Called from model's constructor. Inject context.
            injectObjectContext();
        }

        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.colorIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.colorIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.colorIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.colorIndex, value);
    }

    public RealmList<com.anubis.commons.models.Photo> realmGet$colorPhotos() {
        if (proxyState == null) {
            // Called from model's constructor. Inject context.
            injectObjectContext();
        }

        proxyState.getRealm$realm().checkIfValid();
        // use the cached value if available
        if (colorPhotosRealmList != null) {
            return colorPhotosRealmList;
        } else {
            LinkView linkView = proxyState.getRow$realm().getLinkList(columnInfo.colorPhotosIndex);
            colorPhotosRealmList = new RealmList<com.anubis.commons.models.Photo>(com.anubis.commons.models.Photo.class, linkView, proxyState.getRealm$realm());
            return colorPhotosRealmList;
        }
    }

    public void realmSet$colorPhotos(RealmList<com.anubis.commons.models.Photo> value) {
        if (proxyState == null) {
            // Called from model's constructor. Inject context.
            injectObjectContext();
        }

        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("colorPhotos")) {
                return;
            }
            if (value != null && !value.isManaged()) {
                final Realm realm = (Realm) proxyState.getRealm$realm();
                final RealmList<com.anubis.commons.models.Photo> original = value;
                value = new RealmList<com.anubis.commons.models.Photo>();
                for (com.anubis.commons.models.Photo item : original) {
                    if (item == null || RealmObject.isManaged(item)) {
                        value.add(item);
                    } else {
                        value.add(realm.copyToRealm(item));
                    }
                }
            }
        }

        proxyState.getRealm$realm().checkIfValid();
        LinkView links = proxyState.getRow$realm().getLinkList(columnInfo.colorPhotosIndex);
        links.clear();
        if (value == null) {
            return;
        }
        for (RealmModel linkedObject : (RealmList<? extends RealmModel>) value) {
            if (!(RealmObject.isManaged(linkedObject) && RealmObject.isValid(linkedObject))) {
                throw new IllegalArgumentException("Each element of 'value' must be a valid managed object.");
            }
            if (((RealmObjectProxy)linkedObject).realmGet$proxyState().getRealm$realm() != proxyState.getRealm$realm()) {
                throw new IllegalArgumentException("Each element of 'value' must belong to the same Realm.");
            }
            links.add(((RealmObjectProxy)linkedObject).realmGet$proxyState().getRow$realm().getIndex());
        }
    }

    public static RealmObjectSchema createRealmObjectSchema(RealmSchema realmSchema) {
        if (!realmSchema.contains("Color")) {
            RealmObjectSchema realmObjectSchema = realmSchema.create("Color");
            realmObjectSchema.add(new Property("id", RealmFieldType.STRING, Property.PRIMARY_KEY, Property.INDEXED, !Property.REQUIRED));
            realmObjectSchema.add(new Property("timestamp", RealmFieldType.DATE, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED));
            realmObjectSchema.add(new Property("color", RealmFieldType.STRING, !Property.PRIMARY_KEY, Property.INDEXED, !Property.REQUIRED));
            if (!realmSchema.contains("Photo")) {
                PhotoRealmProxy.createRealmObjectSchema(realmSchema);
            }
            realmObjectSchema.add(new Property("colorPhotos", RealmFieldType.LIST, realmSchema.get("Photo")));
            return realmObjectSchema;
        }
        return realmSchema.get("Color");
    }

    public static Table initTable(SharedRealm sharedRealm) {
        if (!sharedRealm.hasTable("class_Color")) {
            Table table = sharedRealm.getTable("class_Color");
            table.addColumn(RealmFieldType.STRING, "id", Table.NULLABLE);
            table.addColumn(RealmFieldType.DATE, "timestamp", Table.NULLABLE);
            table.addColumn(RealmFieldType.STRING, "color", Table.NULLABLE);
            if (!sharedRealm.hasTable("class_Photo")) {
                PhotoRealmProxy.initTable(sharedRealm);
            }
            table.addColumnLink(RealmFieldType.LIST, "colorPhotos", sharedRealm.getTable("class_Photo"));
            table.addSearchIndex(table.getColumnIndex("id"));
            table.addSearchIndex(table.getColumnIndex("color"));
            table.setPrimaryKey("id");
            return table;
        }
        return sharedRealm.getTable("class_Color");
    }

    public static ColorColumnInfo validateTable(SharedRealm sharedRealm, boolean allowExtraColumns) {
        if (sharedRealm.hasTable("class_Color")) {
            Table table = sharedRealm.getTable("class_Color");
            final long columnCount = table.getColumnCount();
            if (columnCount != 4) {
                if (columnCount < 4) {
                    throw new RealmMigrationNeededException(sharedRealm.getPath(), "Field count is less than expected - expected 4 but was " + columnCount);
                }
                if (allowExtraColumns) {
                    RealmLog.debug("Field count is more than expected - expected 4 but was %1$d", columnCount);
                } else {
                    throw new RealmMigrationNeededException(sharedRealm.getPath(), "Field count is more than expected - expected 4 but was " + columnCount);
                }
            }
            Map<String, RealmFieldType> columnTypes = new HashMap<String, RealmFieldType>();
            for (long i = 0; i < 4; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }

            final ColorColumnInfo columnInfo = new ColorColumnInfo(sharedRealm.getPath(), table);

            if (!columnTypes.containsKey("id")) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Missing field 'id' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("id") != RealmFieldType.STRING) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Invalid type 'String' for field 'id' in existing Realm file.");
            }
            if (!table.isColumnNullable(columnInfo.idIndex)) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(),"@PrimaryKey field 'id' does not support null values in the existing Realm file. Migrate using RealmObjectSchema.setNullable(), or mark the field as @Required.");
            }
            if (table.getPrimaryKey() != table.getColumnIndex("id")) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Primary key not defined for field 'id' in existing Realm file. Add @PrimaryKey.");
            }
            if (!table.hasSearchIndex(table.getColumnIndex("id"))) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Index not defined for field 'id' in existing Realm file. Either set @Index or migrate using io.realm.internal.Table.removeSearchIndex().");
            }
            if (!columnTypes.containsKey("timestamp")) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Missing field 'timestamp' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("timestamp") != RealmFieldType.DATE) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Invalid type 'Date' for field 'timestamp' in existing Realm file.");
            }
            if (!table.isColumnNullable(columnInfo.timestampIndex)) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Field 'timestamp' is required. Either set @Required to field 'timestamp' or migrate using RealmObjectSchema.setNullable().");
            }
            if (!columnTypes.containsKey("color")) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Missing field 'color' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("color") != RealmFieldType.STRING) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Invalid type 'String' for field 'color' in existing Realm file.");
            }
            if (!table.isColumnNullable(columnInfo.colorIndex)) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Field 'color' is required. Either set @Required to field 'color' or migrate using RealmObjectSchema.setNullable().");
            }
            if (!table.hasSearchIndex(table.getColumnIndex("color"))) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Index not defined for field 'color' in existing Realm file. Either set @Index or migrate using io.realm.internal.Table.removeSearchIndex().");
            }
            if (!columnTypes.containsKey("colorPhotos")) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Missing field 'colorPhotos'");
            }
            if (columnTypes.get("colorPhotos") != RealmFieldType.LIST) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Invalid type 'Photo' for field 'colorPhotos'");
            }
            if (!sharedRealm.hasTable("class_Photo")) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Missing class 'class_Photo' for field 'colorPhotos'");
            }
            Table table_3 = sharedRealm.getTable("class_Photo");
            if (!table.getLinkTarget(columnInfo.colorPhotosIndex).hasSameSchema(table_3)) {
                throw new RealmMigrationNeededException(sharedRealm.getPath(), "Invalid RealmList type for field 'colorPhotos': '" + table.getLinkTarget(columnInfo.colorPhotosIndex).getName() + "' expected - was '" + table_3.getName() + "'");
            }
            return columnInfo;
        } else {
            throw new RealmMigrationNeededException(sharedRealm.getPath(), "The 'Color' class is missing from the schema for this Realm.");
        }
    }

    public static String getTableName() {
        return "class_Color";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    @SuppressWarnings("cast")
    public static com.anubis.commons.models.Color createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = new ArrayList<String>(1);
        com.anubis.commons.models.Color obj = null;
        if (update) {
            Table table = realm.getTable(com.anubis.commons.models.Color.class);
            long pkColumnIndex = table.getPrimaryKey();
            long rowIndex = TableOrView.NO_MATCH;
            if (json.isNull("id")) {
                rowIndex = table.findFirstNull(pkColumnIndex);
            } else {
                rowIndex = table.findFirstString(pkColumnIndex, json.getString("id"));
            }
            if (rowIndex != TableOrView.NO_MATCH) {
                final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.schema.getColumnInfo(com.anubis.commons.models.Color.class), false, Collections.<String> emptyList());
                    obj = new io.realm.ColorRealmProxy();
                } finally {
                    objectContext.clear();
                }
            }
        }
        if (obj == null) {
            if (json.has("colorPhotos")) {
                excludeFields.add("colorPhotos");
            }
            if (json.has("id")) {
                if (json.isNull("id")) {
                    obj = (io.realm.ColorRealmProxy) realm.createObjectInternal(com.anubis.commons.models.Color.class, null, true, excludeFields);
                } else {
                    obj = (io.realm.ColorRealmProxy) realm.createObjectInternal(com.anubis.commons.models.Color.class, json.getString("id"), true, excludeFields);
                }
            } else {
                throw new IllegalArgumentException("JSON object doesn't have the primary key field 'id'.");
            }
        }
        if (json.has("timestamp")) {
            if (json.isNull("timestamp")) {
                ((ColorRealmProxyInterface) obj).realmSet$timestamp(null);
            } else {
                Object timestamp = json.get("timestamp");
                if (timestamp instanceof String) {
                    ((ColorRealmProxyInterface) obj).realmSet$timestamp(JsonUtils.stringToDate((String) timestamp));
                } else {
                    ((ColorRealmProxyInterface) obj).realmSet$timestamp(new Date(json.getLong("timestamp")));
                }
            }
        }
        if (json.has("color")) {
            if (json.isNull("color")) {
                ((ColorRealmProxyInterface) obj).realmSet$color(null);
            } else {
                ((ColorRealmProxyInterface) obj).realmSet$color((String) json.getString("color"));
            }
        }
        if (json.has("colorPhotos")) {
            if (json.isNull("colorPhotos")) {
                ((ColorRealmProxyInterface) obj).realmSet$colorPhotos(null);
            } else {
                ((ColorRealmProxyInterface) obj).realmGet$colorPhotos().clear();
                JSONArray array = json.getJSONArray("colorPhotos");
                for (int i = 0; i < array.length(); i++) {
                    com.anubis.commons.models.Photo item = PhotoRealmProxy.createOrUpdateUsingJsonObject(realm, array.getJSONObject(i), update);
                    ((ColorRealmProxyInterface) obj).realmGet$colorPhotos().add(item);
                }
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.anubis.commons.models.Color createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        boolean jsonHasPrimaryKey = false;
        com.anubis.commons.models.Color obj = new com.anubis.commons.models.Color();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    ((ColorRealmProxyInterface) obj).realmSet$id(null);
                } else {
                    ((ColorRealmProxyInterface) obj).realmSet$id((String) reader.nextString());
                }
                jsonHasPrimaryKey = true;
            } else if (name.equals("timestamp")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    ((ColorRealmProxyInterface) obj).realmSet$timestamp(null);
                } else if (reader.peek() == JsonToken.NUMBER) {
                    long timestamp = reader.nextLong();
                    if (timestamp > -1) {
                        ((ColorRealmProxyInterface) obj).realmSet$timestamp(new Date(timestamp));
                    }
                } else {
                    ((ColorRealmProxyInterface) obj).realmSet$timestamp(JsonUtils.stringToDate(reader.nextString()));
                }
            } else if (name.equals("color")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    ((ColorRealmProxyInterface) obj).realmSet$color(null);
                } else {
                    ((ColorRealmProxyInterface) obj).realmSet$color((String) reader.nextString());
                }
            } else if (name.equals("colorPhotos")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    ((ColorRealmProxyInterface) obj).realmSet$colorPhotos(null);
                } else {
                    ((ColorRealmProxyInterface) obj).realmSet$colorPhotos(new RealmList<com.anubis.commons.models.Photo>());
                    reader.beginArray();
                    while (reader.hasNext()) {
                        com.anubis.commons.models.Photo item = PhotoRealmProxy.createUsingJsonStream(realm, reader);
                        ((ColorRealmProxyInterface) obj).realmGet$colorPhotos().add(item);
                    }
                    reader.endArray();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        if (!jsonHasPrimaryKey) {
            throw new IllegalArgumentException("JSON object doesn't have the primary key field 'id'.");
        }
        obj = realm.copyToRealm(obj);
        return obj;
    }

    public static com.anubis.commons.models.Color copyOrUpdate(Realm realm, com.anubis.commons.models.Color object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().threadId != realm.threadId) {
            throw new IllegalArgumentException("Objects which belong to Realm instances in other threads cannot be copied into this Realm instance.");
        }
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return object;
        }
        final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
        RealmObjectProxy cachedRealmObject = cache.get(object);
        if (cachedRealmObject != null) {
            return (com.anubis.commons.models.Color) cachedRealmObject;
        } else {
            com.anubis.commons.models.Color realmObject = null;
            boolean canUpdate = update;
            if (canUpdate) {
                Table table = realm.getTable(com.anubis.commons.models.Color.class);
                long pkColumnIndex = table.getPrimaryKey();
                String value = ((ColorRealmProxyInterface) object).realmGet$id();
                long rowIndex = TableOrView.NO_MATCH;
                if (value == null) {
                    rowIndex = table.findFirstNull(pkColumnIndex);
                } else {
                    rowIndex = table.findFirstString(pkColumnIndex, value);
                }
                if (rowIndex != TableOrView.NO_MATCH) {
                    try {
                        objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.schema.getColumnInfo(com.anubis.commons.models.Color.class), false, Collections.<String> emptyList());
                        realmObject = new io.realm.ColorRealmProxy();
                        cache.put(object, (RealmObjectProxy) realmObject);
                    } finally {
                        objectContext.clear();
                    }
                } else {
                    canUpdate = false;
                }
            }

            if (canUpdate) {
                return update(realm, realmObject, object, cache);
            } else {
                return copy(realm, object, update, cache);
            }
        }
    }

    public static com.anubis.commons.models.Color copy(Realm realm, com.anubis.commons.models.Color newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.anubis.commons.models.Color) cachedRealmObject;
        } else {
            // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
            com.anubis.commons.models.Color realmObject = realm.createObjectInternal(com.anubis.commons.models.Color.class, ((ColorRealmProxyInterface) newObject).realmGet$id(), false, Collections.<String>emptyList());
            cache.put(newObject, (RealmObjectProxy) realmObject);
            ((ColorRealmProxyInterface) realmObject).realmSet$timestamp(((ColorRealmProxyInterface) newObject).realmGet$timestamp());
            ((ColorRealmProxyInterface) realmObject).realmSet$color(((ColorRealmProxyInterface) newObject).realmGet$color());

            RealmList<com.anubis.commons.models.Photo> colorPhotosList = ((ColorRealmProxyInterface) newObject).realmGet$colorPhotos();
            if (colorPhotosList != null) {
                RealmList<com.anubis.commons.models.Photo> colorPhotosRealmList = ((ColorRealmProxyInterface) realmObject).realmGet$colorPhotos();
                for (int i = 0; i < colorPhotosList.size(); i++) {
                    com.anubis.commons.models.Photo colorPhotosItem = colorPhotosList.get(i);
                    com.anubis.commons.models.Photo cachecolorPhotos = (com.anubis.commons.models.Photo) cache.get(colorPhotosItem);
                    if (cachecolorPhotos != null) {
                        colorPhotosRealmList.add(cachecolorPhotos);
                    } else {
                        colorPhotosRealmList.add(PhotoRealmProxy.copyOrUpdate(realm, colorPhotosList.get(i), update, cache));
                    }
                }
            }

            return realmObject;
        }
    }

    public static long insert(Realm realm, com.anubis.commons.models.Color object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy)object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.anubis.commons.models.Color.class);
        long tableNativePtr = table.getNativeTablePointer();
        ColorColumnInfo columnInfo = (ColorColumnInfo) realm.schema.getColumnInfo(com.anubis.commons.models.Color.class);
        long pkColumnIndex = table.getPrimaryKey();
        String primaryKeyValue = ((ColorRealmProxyInterface) object).realmGet$id();
        long rowIndex = TableOrView.NO_MATCH;
        if (primaryKeyValue == null) {
            rowIndex = Table.nativeFindFirstNull(tableNativePtr, pkColumnIndex);
        } else {
            rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, primaryKeyValue);
        }
        if (rowIndex == TableOrView.NO_MATCH) {
            rowIndex = table.addEmptyRowWithPrimaryKey(primaryKeyValue, false);
        } else {
            Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
        }
        cache.put(object, rowIndex);
        java.util.Date realmGet$timestamp = ((ColorRealmProxyInterface)object).realmGet$timestamp();
        if (realmGet$timestamp != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.timestampIndex, rowIndex, realmGet$timestamp.getTime(), false);
        }
        String realmGet$color = ((ColorRealmProxyInterface)object).realmGet$color();
        if (realmGet$color != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.colorIndex, rowIndex, realmGet$color, false);
        }

        RealmList<com.anubis.commons.models.Photo> colorPhotosList = ((ColorRealmProxyInterface) object).realmGet$colorPhotos();
        if (colorPhotosList != null) {
            long colorPhotosNativeLinkViewPtr = Table.nativeGetLinkView(tableNativePtr, columnInfo.colorPhotosIndex, rowIndex);
            for (com.anubis.commons.models.Photo colorPhotosItem : colorPhotosList) {
                Long cacheItemIndexcolorPhotos = cache.get(colorPhotosItem);
                if (cacheItemIndexcolorPhotos == null) {
                    cacheItemIndexcolorPhotos = PhotoRealmProxy.insert(realm, colorPhotosItem, cache);
                }
                LinkView.nativeAdd(colorPhotosNativeLinkViewPtr, cacheItemIndexcolorPhotos);
            }
            LinkView.nativeClose(colorPhotosNativeLinkViewPtr);
        }

        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.anubis.commons.models.Color.class);
        long tableNativePtr = table.getNativeTablePointer();
        ColorColumnInfo columnInfo = (ColorColumnInfo) realm.schema.getColumnInfo(com.anubis.commons.models.Color.class);
        long pkColumnIndex = table.getPrimaryKey();
        com.anubis.commons.models.Color object = null;
        while (objects.hasNext()) {
            object = (com.anubis.commons.models.Color) objects.next();
            if(!cache.containsKey(object)) {
                if (object instanceof RealmObjectProxy && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                    cache.put(object, ((RealmObjectProxy)object).realmGet$proxyState().getRow$realm().getIndex());
                    continue;
                }
                String primaryKeyValue = ((ColorRealmProxyInterface) object).realmGet$id();
                long rowIndex = TableOrView.NO_MATCH;
                if (primaryKeyValue == null) {
                    rowIndex = Table.nativeFindFirstNull(tableNativePtr, pkColumnIndex);
                } else {
                    rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, primaryKeyValue);
                }
                if (rowIndex == TableOrView.NO_MATCH) {
                    rowIndex = table.addEmptyRowWithPrimaryKey(primaryKeyValue, false);
                } else {
                    Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
                }
                cache.put(object, rowIndex);
                java.util.Date realmGet$timestamp = ((ColorRealmProxyInterface)object).realmGet$timestamp();
                if (realmGet$timestamp != null) {
                    Table.nativeSetTimestamp(tableNativePtr, columnInfo.timestampIndex, rowIndex, realmGet$timestamp.getTime(), false);
                }
                String realmGet$color = ((ColorRealmProxyInterface)object).realmGet$color();
                if (realmGet$color != null) {
                    Table.nativeSetString(tableNativePtr, columnInfo.colorIndex, rowIndex, realmGet$color, false);
                }

                RealmList<com.anubis.commons.models.Photo> colorPhotosList = ((ColorRealmProxyInterface) object).realmGet$colorPhotos();
                if (colorPhotosList != null) {
                    long colorPhotosNativeLinkViewPtr = Table.nativeGetLinkView(tableNativePtr, columnInfo.colorPhotosIndex, rowIndex);
                    for (com.anubis.commons.models.Photo colorPhotosItem : colorPhotosList) {
                        Long cacheItemIndexcolorPhotos = cache.get(colorPhotosItem);
                        if (cacheItemIndexcolorPhotos == null) {
                            cacheItemIndexcolorPhotos = PhotoRealmProxy.insert(realm, colorPhotosItem, cache);
                        }
                        LinkView.nativeAdd(colorPhotosNativeLinkViewPtr, cacheItemIndexcolorPhotos);
                    }
                    LinkView.nativeClose(colorPhotosNativeLinkViewPtr);
                }

            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.anubis.commons.models.Color object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy)object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.anubis.commons.models.Color.class);
        long tableNativePtr = table.getNativeTablePointer();
        ColorColumnInfo columnInfo = (ColorColumnInfo) realm.schema.getColumnInfo(com.anubis.commons.models.Color.class);
        long pkColumnIndex = table.getPrimaryKey();
        String primaryKeyValue = ((ColorRealmProxyInterface) object).realmGet$id();
        long rowIndex = TableOrView.NO_MATCH;
        if (primaryKeyValue == null) {
            rowIndex = Table.nativeFindFirstNull(tableNativePtr, pkColumnIndex);
        } else {
            rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, primaryKeyValue);
        }
        if (rowIndex == TableOrView.NO_MATCH) {
            rowIndex = table.addEmptyRowWithPrimaryKey(primaryKeyValue, false);
        }
        cache.put(object, rowIndex);
        java.util.Date realmGet$timestamp = ((ColorRealmProxyInterface)object).realmGet$timestamp();
        if (realmGet$timestamp != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.timestampIndex, rowIndex, realmGet$timestamp.getTime(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.timestampIndex, rowIndex, false);
        }
        String realmGet$color = ((ColorRealmProxyInterface)object).realmGet$color();
        if (realmGet$color != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.colorIndex, rowIndex, realmGet$color, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.colorIndex, rowIndex, false);
        }

        long colorPhotosNativeLinkViewPtr = Table.nativeGetLinkView(tableNativePtr, columnInfo.colorPhotosIndex, rowIndex);
        LinkView.nativeClear(colorPhotosNativeLinkViewPtr);
        RealmList<com.anubis.commons.models.Photo> colorPhotosList = ((ColorRealmProxyInterface) object).realmGet$colorPhotos();
        if (colorPhotosList != null) {
            for (com.anubis.commons.models.Photo colorPhotosItem : colorPhotosList) {
                Long cacheItemIndexcolorPhotos = cache.get(colorPhotosItem);
                if (cacheItemIndexcolorPhotos == null) {
                    cacheItemIndexcolorPhotos = PhotoRealmProxy.insertOrUpdate(realm, colorPhotosItem, cache);
                }
                LinkView.nativeAdd(colorPhotosNativeLinkViewPtr, cacheItemIndexcolorPhotos);
            }
        }
        LinkView.nativeClose(colorPhotosNativeLinkViewPtr);

        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.anubis.commons.models.Color.class);
        long tableNativePtr = table.getNativeTablePointer();
        ColorColumnInfo columnInfo = (ColorColumnInfo) realm.schema.getColumnInfo(com.anubis.commons.models.Color.class);
        long pkColumnIndex = table.getPrimaryKey();
        com.anubis.commons.models.Color object = null;
        while (objects.hasNext()) {
            object = (com.anubis.commons.models.Color) objects.next();
            if(!cache.containsKey(object)) {
                if (object instanceof RealmObjectProxy && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                    cache.put(object, ((RealmObjectProxy)object).realmGet$proxyState().getRow$realm().getIndex());
                    continue;
                }
                String primaryKeyValue = ((ColorRealmProxyInterface) object).realmGet$id();
                long rowIndex = TableOrView.NO_MATCH;
                if (primaryKeyValue == null) {
                    rowIndex = Table.nativeFindFirstNull(tableNativePtr, pkColumnIndex);
                } else {
                    rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, primaryKeyValue);
                }
                if (rowIndex == TableOrView.NO_MATCH) {
                    rowIndex = table.addEmptyRowWithPrimaryKey(primaryKeyValue, false);
                }
                cache.put(object, rowIndex);
                java.util.Date realmGet$timestamp = ((ColorRealmProxyInterface)object).realmGet$timestamp();
                if (realmGet$timestamp != null) {
                    Table.nativeSetTimestamp(tableNativePtr, columnInfo.timestampIndex, rowIndex, realmGet$timestamp.getTime(), false);
                } else {
                    Table.nativeSetNull(tableNativePtr, columnInfo.timestampIndex, rowIndex, false);
                }
                String realmGet$color = ((ColorRealmProxyInterface)object).realmGet$color();
                if (realmGet$color != null) {
                    Table.nativeSetString(tableNativePtr, columnInfo.colorIndex, rowIndex, realmGet$color, false);
                } else {
                    Table.nativeSetNull(tableNativePtr, columnInfo.colorIndex, rowIndex, false);
                }

                long colorPhotosNativeLinkViewPtr = Table.nativeGetLinkView(tableNativePtr, columnInfo.colorPhotosIndex, rowIndex);
                LinkView.nativeClear(colorPhotosNativeLinkViewPtr);
                RealmList<com.anubis.commons.models.Photo> colorPhotosList = ((ColorRealmProxyInterface) object).realmGet$colorPhotos();
                if (colorPhotosList != null) {
                    for (com.anubis.commons.models.Photo colorPhotosItem : colorPhotosList) {
                        Long cacheItemIndexcolorPhotos = cache.get(colorPhotosItem);
                        if (cacheItemIndexcolorPhotos == null) {
                            cacheItemIndexcolorPhotos = PhotoRealmProxy.insertOrUpdate(realm, colorPhotosItem, cache);
                        }
                        LinkView.nativeAdd(colorPhotosNativeLinkViewPtr, cacheItemIndexcolorPhotos);
                    }
                }
                LinkView.nativeClose(colorPhotosNativeLinkViewPtr);

            }
        }
    }

    public static com.anubis.commons.models.Color createDetachedCopy(com.anubis.commons.models.Color realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.anubis.commons.models.Color unmanagedObject;
        if (cachedObject != null) {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.anubis.commons.models.Color)cachedObject.object;
            } else {
                unmanagedObject = (com.anubis.commons.models.Color)cachedObject.object;
                cachedObject.minDepth = currentDepth;
            }
        } else {
            unmanagedObject = new com.anubis.commons.models.Color();
            cache.put(realmObject, new RealmObjectProxy.CacheData(currentDepth, unmanagedObject));
        }
        ((ColorRealmProxyInterface) unmanagedObject).realmSet$id(((ColorRealmProxyInterface) realmObject).realmGet$id());
        ((ColorRealmProxyInterface) unmanagedObject).realmSet$timestamp(((ColorRealmProxyInterface) realmObject).realmGet$timestamp());
        ((ColorRealmProxyInterface) unmanagedObject).realmSet$color(((ColorRealmProxyInterface) realmObject).realmGet$color());

        // Deep copy of colorPhotos
        if (currentDepth == maxDepth) {
            ((ColorRealmProxyInterface) unmanagedObject).realmSet$colorPhotos(null);
        } else {
            RealmList<com.anubis.commons.models.Photo> managedcolorPhotosList = ((ColorRealmProxyInterface) realmObject).realmGet$colorPhotos();
            RealmList<com.anubis.commons.models.Photo> unmanagedcolorPhotosList = new RealmList<com.anubis.commons.models.Photo>();
            ((ColorRealmProxyInterface) unmanagedObject).realmSet$colorPhotos(unmanagedcolorPhotosList);
            int nextDepth = currentDepth + 1;
            int size = managedcolorPhotosList.size();
            for (int i = 0; i < size; i++) {
                com.anubis.commons.models.Photo item = PhotoRealmProxy.createDetachedCopy(managedcolorPhotosList.get(i), nextDepth, maxDepth, cache);
                unmanagedcolorPhotosList.add(item);
            }
        }
        return unmanagedObject;
    }

    static com.anubis.commons.models.Color update(Realm realm, com.anubis.commons.models.Color realmObject, com.anubis.commons.models.Color newObject, Map<RealmModel, RealmObjectProxy> cache) {
        ((ColorRealmProxyInterface) realmObject).realmSet$timestamp(((ColorRealmProxyInterface) newObject).realmGet$timestamp());
        ((ColorRealmProxyInterface) realmObject).realmSet$color(((ColorRealmProxyInterface) newObject).realmGet$color());
        RealmList<com.anubis.commons.models.Photo> colorPhotosList = ((ColorRealmProxyInterface) newObject).realmGet$colorPhotos();
        RealmList<com.anubis.commons.models.Photo> colorPhotosRealmList = ((ColorRealmProxyInterface) realmObject).realmGet$colorPhotos();
        colorPhotosRealmList.clear();
        if (colorPhotosList != null) {
            for (int i = 0; i < colorPhotosList.size(); i++) {
                com.anubis.commons.models.Photo colorPhotosItem = colorPhotosList.get(i);
                com.anubis.commons.models.Photo cachecolorPhotos = (com.anubis.commons.models.Photo) cache.get(colorPhotosItem);
                if (cachecolorPhotos != null) {
                    colorPhotosRealmList.add(cachecolorPhotos);
                } else {
                    colorPhotosRealmList.add(PhotoRealmProxy.copyOrUpdate(realm, colorPhotosList.get(i), true, cache));
                }
            }
        }
        return realmObject;
    }

    @Override
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Color = [");
        stringBuilder.append("{id:");
        stringBuilder.append(realmGet$id() != null ? realmGet$id() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{timestamp:");
        stringBuilder.append(realmGet$timestamp() != null ? realmGet$timestamp() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{color:");
        stringBuilder.append(realmGet$color() != null ? realmGet$color() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{colorPhotos:");
        stringBuilder.append("RealmList<Photo>[").append(realmGet$colorPhotos().size()).append("]");
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public ProxyState realmGet$proxyState() {
        return proxyState;
    }

    @Override
    public int hashCode() {
        String realmName = proxyState.getRealm$realm().getPath();
        String tableName = proxyState.getRow$realm().getTable().getName();
        long rowIndex = proxyState.getRow$realm().getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorRealmProxy aColor = (ColorRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aColor.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aColor.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aColor.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }

}
