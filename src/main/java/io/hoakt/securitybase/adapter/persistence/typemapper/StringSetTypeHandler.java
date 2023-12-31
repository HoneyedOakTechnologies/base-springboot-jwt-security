package io.hoakt.securitybase.adapter.persistence.typemapper;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class StringSetTypeHandler extends BaseTypeHandler<Set<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Set<String> parameter, JdbcType jdbcType) throws SQLException {
        Connection connection = ps.getConnection();
        Array array = connection.createArrayOf("VARCHAR", parameter.toArray());
        ps.setArray(i, array);
    }

    @Override
    public Set<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        if (array == null) {
            return null;
        }
        return convertArrayToSet(array);
    }

    @Override
    public Set<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Array array = rs.getArray(columnIndex);
        if (array == null) {
            return null;
        }
        return convertArrayToSet(array);
    }

    @Override
    public Set<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Array array = cs.getArray(columnIndex);
        if (array == null) {
            return null;
        }
        return convertArrayToSet(array);
    }

    private Set<String> convertArrayToSet(Array array) throws SQLException {
        Object[] objectArray = (Object[]) array.getArray();
        Set<String> list = new HashSet<>();
        for (Object element : objectArray) {
            list.add(element.toString());
        }
        return list;
    }
}
