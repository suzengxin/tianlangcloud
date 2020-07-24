package com.tianlang.config;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MySqlDao extends JdbcTemplate {

    @Override
    @Resource(name = "dataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }
    
    public Long queryForLong(String sql, Object... args) throws SQLException {
        try {
          return (Long)queryForObject(sql, Long.class, args);
        } catch (Exception e) {
          e.printStackTrace();
          throw new SQLException(e);
        } 
      }

    @Override
    public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException {
        return queryForObject(sql, args, getColumnMapRowMapper());
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1));

        return requiredSingleResult(results);
    }

    private static <T> T requiredSingleResult(Collection<T> results) throws IncorrectResultSizeDataAccessException {
        int size = (results != null ? results.size() : 0);
        if (size == 0) {
            return null;
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, size);
        }
        return results.iterator().next();
    }

    public NamedParameterJdbcTemplate getNamedParamterDao() {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(super.getDataSource());
        return jdbcTemplate;
    }
}