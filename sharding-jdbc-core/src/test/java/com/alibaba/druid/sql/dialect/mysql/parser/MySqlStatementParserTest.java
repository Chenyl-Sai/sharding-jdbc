package com.alibaba.druid.sql.dialect.mysql.parser;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class MySqlStatementParserTest {
    
    @Test
    public void parseStatementWithInsertValue() {
        MySqlStatementParser statementParser = new MySqlStatementParser("INSERT INTO TABLE_XXX VALUE (1, 'value_char')");
        SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) statementParser.parseStatement();
        assertThat(sqlInsertStatement.getDbType(), is(JdbcConstants.MYSQL));
        assertThat(sqlInsertStatement.getTableName().getSimpleName(), is("TABLE_XXX"));
        assertNull(sqlInsertStatement.getTableSource().getAlias());
        assertNull(sqlInsertStatement.getAlias());
        assertTrue(sqlInsertStatement.getColumns().isEmpty());
        assertThat(sqlInsertStatement.getValues().getValues().size(), is(2));
        assertThat(((SQLIntegerExpr) sqlInsertStatement.getValues().getValues().get(0)).getNumber().intValue(), is(1));
        assertThat(sqlInsertStatement.getValues().getValues().get(1).toString(), is("'value_char'"));
        assertThat(((SQLCharExpr) sqlInsertStatement.getValues().getValues().get(1)).getText(), is("value_char"));
        assertTrue(sqlInsertStatement.getIdentifiersBetweenInsertAndInto().isEmpty());
        assertTrue(sqlInsertStatement.getIdentifiersBetweenTableAndValues().isEmpty());
        assertThat(sqlInsertStatement.toString(), is("INSERT INTO TABLE_XXX\nVALUES (1, 'value_char')"));
    }
    
    @Test
    public void parseStatementWithInsertValues() {
        MySqlStatementParser statementParser = new MySqlStatementParser("INSERT LOW_PRIORITY IGNORE INTO TABLE_XXX PARTITION (partition1,partition2) (`field1`, `field2`) VALUES (1, 'value_char')");
        SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) statementParser.parseStatement();
        assertThat(sqlInsertStatement.getDbType(), is(JdbcConstants.MYSQL));
        assertThat(sqlInsertStatement.getTableName().getSimpleName(), is("TABLE_XXX"));
        assertNull(sqlInsertStatement.getTableSource().getAlias());
        assertNull(sqlInsertStatement.getAlias());
        assertThat(sqlInsertStatement.getColumns().size(), is(2));
        assertThat(((SQLIdentifierExpr) sqlInsertStatement.getColumns().get(0)).getSimpleName(), is("`field1`"));
        assertThat(((SQLIdentifierExpr) sqlInsertStatement.getColumns().get(1)).getSimpleName(), is("`field2`"));
        assertThat(sqlInsertStatement.getValues().getValues().size(), is(2));
        assertThat(((SQLIntegerExpr) sqlInsertStatement.getValues().getValues().get(0)).getNumber().intValue(), is(1));
        assertThat(sqlInsertStatement.getValues().getValues().get(1).toString(), is("'value_char'"));
        assertThat(((SQLCharExpr) sqlInsertStatement.getValues().getValues().get(1)).getText(), is("value_char"));
        assertThat(sqlInsertStatement.getIdentifiersBetweenInsertAndInto().size(), is(2));
        assertThat(sqlInsertStatement.getIdentifiersBetweenInsertAndInto().get(0), is("LOW_PRIORITY"));
        assertThat(sqlInsertStatement.getIdentifiersBetweenInsertAndInto().get(1), is("IGNORE"));
        assertThat(sqlInsertStatement.getIdentifiersBetweenTableAndValues().size(), is(6));
        assertThat(sqlInsertStatement.getIdentifiersBetweenTableAndValues().get(0), is("PARTITION"));
        assertThat(sqlInsertStatement.getIdentifiersBetweenTableAndValues().get(1), is("("));
        assertThat(sqlInsertStatement.getIdentifiersBetweenTableAndValues().get(2), is("partition1"));
        assertThat(sqlInsertStatement.getIdentifiersBetweenTableAndValues().get(3), is(","));
        assertThat(sqlInsertStatement.getIdentifiersBetweenTableAndValues().get(4), is("partition2"));
        assertThat(sqlInsertStatement.getIdentifiersBetweenTableAndValues().get(5), is(")"));
        assertThat(sqlInsertStatement.toString(), is("INSERT LOW_PRIORITY IGNORE INTO TABLE_XXX PARTITION ( partition1 , partition2 ) (`field1`, `field2`)\nVALUES (1, 'value_char')"));
    }
    
    @Test
    public void parseStatementWithInsertSelect() {
        MySqlStatementParser statementParser = new MySqlStatementParser("INSERT INTO TABLE_XXX (field1, field2) SELECT field1, field2 FROM TABLE_XXX2");
        SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) statementParser.parseStatement();
        assertThat(sqlInsertStatement.getDbType(), is(JdbcConstants.MYSQL));
        assertThat(sqlInsertStatement.getTableName().getSimpleName(), is("TABLE_XXX"));
        assertNull(sqlInsertStatement.getTableSource().getAlias());
        assertNull(sqlInsertStatement.getAlias());
        assertThat(sqlInsertStatement.getColumns().size(), is(2));
        assertThat(((SQLIdentifierExpr) sqlInsertStatement.getColumns().get(0)).getSimpleName(), is("field1"));
        assertThat(((SQLIdentifierExpr) sqlInsertStatement.getColumns().get(1)).getSimpleName(), is("field2"));
        assertThat(((SQLSelectQueryBlock) sqlInsertStatement.getQuery().getQuery()).getSelectList().size(), is(2));
        assertThat(((SQLSelectQueryBlock) sqlInsertStatement.getQuery().getQuery()).getSelectList().get(0).toString(), is("field1"));
        assertThat(((SQLSelectQueryBlock) sqlInsertStatement.getQuery().getQuery()).getSelectList().get(1).toString(), is("field2"));
        assertThat(((SQLSelectQueryBlock) sqlInsertStatement.getQuery().getQuery()).getFrom().toString(), is("TABLE_XXX2"));
        assertThat(sqlInsertStatement.getQuery().getParent(), is((SQLObject) sqlInsertStatement));
        assertThat(sqlInsertStatement.toString(), is("INSERT INTO TABLE_XXX (field1, field2)\nSELECT field1, field2\nFROM TABLE_XXX2"));
    }
}
