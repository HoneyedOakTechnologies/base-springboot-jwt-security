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
import java.util.LinkedHashMap;
import java.util.Map;

public class StringStringMapTypeHandler extends BaseTypeHandler<Map<String, String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, String> parameter, JdbcType jdbcType) throws SQLException {
        PGobject pGobject = new PGobject();
        pGobject.setType("hstore");
        pGobject.setValue(HStoreConverter.toString(parameter));
        ps.setObject(i, pGobject);
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return readMap(rs.getString(columnName));
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return readMap(rs.getString(columnIndex));
    }

    @Override
    public Map<String, String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return readMap(cs.getString(columnIndex));
    }

    private Map<String, String> readMap(String hstring) {
        if (hstring != null) {
            Map<String, String> rawMap = HStoreConverter.fromString(hstring);

            return new LinkedHashMap<>(rawMap);
        }
        return null;
    }
}
