package io.hoakt.securitybase.adapter.persistence.typemapper;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.HStoreConverter;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StringUuidMapTypeHandler extends BaseTypeHandler<Map<String, UUID>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, UUID> parameter, JdbcType jdbcType) throws SQLException {
        PGobject pGobject = new PGobject();
        pGobject.setType("hstore");
        pGobject.setValue(HStoreConverter.toString(parameter));
        ps.setObject(i, pGobject);
    }

    @Override
    public Map<String, UUID> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return readMap(rs.getString(columnName));
    }

    @Override
    public Map<String, UUID> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return readMap(rs.getString(columnIndex));
    }

    @Override
    public Map<String, UUID> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return readMap(cs.getString(columnIndex));
    }

    private Map<String, UUID> readMap(String hstring) {
        if (hstring != null) {
            Map<String, UUID> map = new HashMap<>();
            Map<String, String> rawMap = HStoreConverter.fromString(hstring);
            for (Map.Entry<String, String> entry : rawMap.entrySet()) {
                map.put(entry.getKey(), UUID.fromString(entry.getValue())); // convert from <String, String> to <String,UUID>
            }

            return map;
        }
        return null;
    }
}
