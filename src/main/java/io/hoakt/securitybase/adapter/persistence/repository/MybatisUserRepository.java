package io.hoakt.securitybase.adapter.persistence.repository;

import io.hoakt.securitybase.adapter.persistence.entity.MybatisUserEntity;
import io.hoakt.securitybase.adapter.persistence.typemapper.StringSetTypeHandler;
import io.hoakt.securitybase.adapter.persistence.typemapper.UuidTypeHandler;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.apache.ibatis.type.JdbcType.ARRAY;

public interface MybatisUserRepository {

    @Select("""
            SELECT id, username, email, password, roles, array_agg(DISTINCT authority) AS combined_authorities
            FROM "user" u
            LEFT JOIN LATERAL unnest(u.authorities) AS individual_authority ON true
            LEFT JOIN LATERAL unnest(u.roles) AS user_role ON true
            LEFT JOIN role_authority ra ON ra.role = user_role
            WHERE id = #{id}
            GROUP BY u.id, u.username, u.roles;
            """)
    @Results(id = "MybatisUserEntity", value = {
            @Result(property = "id", column = "id", javaType = UUID.class, typeHandler = UuidTypeHandler.class),
            @Result(property = "username", column = "username", javaType = String.class),
            @Result(property = "email", column = "email", javaType = String.class),
            @Result(property = "password", column = "password", javaType = String.class),
            @Result(property = "roles", column = "roles", javaType = Set.class, jdbcType = ARRAY, typeHandler = StringSetTypeHandler.class),
            @Result(property = "authorities", column = "authorities", javaType = Set.class, jdbcType = ARRAY, typeHandler = StringSetTypeHandler.class)
    })
    Optional<MybatisUserEntity> getById(@Param("id") UUID id);

    @Select("""
            SELECT id, username, email, password, roles, array_agg(DISTINCT authority) AS combined_authorities
            FROM "user" u
            LEFT JOIN LATERAL unnest(u.authorities) AS individual_authority ON true
            LEFT JOIN LATERAL unnest(u.roles) AS user_role ON true
            LEFT JOIN role_authority ra ON ra.role = user_role
            GROUP BY u.id, u.username, u.roles;
            """)
    @ResultMap("MybatisUserEntity")
    List<MybatisUserEntity> findAll();

    @Select("""
            SELECT id, username, email, password, roles, array_agg(DISTINCT authority) AS combined_authorities
            FROM "user" u
            LEFT JOIN LATERAL unnest(u.authorities) AS individual_authority ON true
            LEFT JOIN LATERAL unnest(u.roles) AS user_role ON true
            LEFT JOIN role_authority ra ON ra.role = user_role
            WHERE username = #{username}
            GROUP BY u.id, u.username, u.roles;
            """)
    @ResultMap("MybatisUserEntity")
    Optional<MybatisUserEntity> getByUsername(@Param("username") String username);

    @Insert("""
            INSERT INTO "user"
            (username, email, password, roles, authorities)
            VALUES (#{username}, #{email}, #{password}, #{roles}, #{authorities})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(MybatisUserEntity user);

    @Update("""
            UPDATE "user"
            SET username = #{username}, email = #{email}, password = #{password}, roles = #{roles}, authorities = #{authorities}
            WHERE id = #{id}
            """)
    void update(MybatisUserEntity user);

    @Delete("""
            DELETE FROM "user" WHERE id = #{id}
            """)
    void deleteById(@Param("id") UUID id);
}
