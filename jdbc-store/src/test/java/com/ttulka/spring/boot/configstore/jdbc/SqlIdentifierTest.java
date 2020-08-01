package com.ttulka.spring.boot.configstore.jdbc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlIdentifierTest {

    @Test
    void null_value_is_empty() {
        var sqlIdentifier = new SqlIdentifier(null);
        assertThat(sqlIdentifier.isEmpty()).isTrue();
    }

    @Test
    void empty_value_is_empty() {
        var sqlIdentifier = new SqlIdentifier("");
        assertThat(sqlIdentifier.isEmpty()).isTrue();
    }

    @Test
    void blank_value_is_empty() {
        var sqlIdentifier = new SqlIdentifier(" \t \n ");
        assertThat(sqlIdentifier.isEmpty()).isTrue();
    }

    @Test
    void sql_identifier_is_stripped() {
        var sqlIdentifier = new SqlIdentifier(" \t test \n ");
        assertThat(sqlIdentifier.toString()).isEqualTo("test");
    }

    @Test
    void sql_identifier_is_valid() {
        assertAll(
                () -> assertThat(new SqlIdentifier("t").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("T").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("_").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("TEST01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("testTEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test01TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("testTEST01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("TESTtest01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("TEST01test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test_test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test_TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test01_test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test_01test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("TEST_TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("TEST01_TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("TEST_01TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("TEST_test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test_TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test01_TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test_01TEST").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("_test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("_Test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("_Test01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test_").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("Test_").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("_Test01_").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("_Test_01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("_Test.t01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("T.t01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test.t01").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test.test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("test.Test").toString()).isNotBlank(),
                () -> assertThat(new SqlIdentifier("t01.01").toString()).isNotBlank()
        );
    }

    @Test
    void sql_identifier_is_invalid() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new SqlIdentifier("1").toString()),
                () -> assertThrows(IllegalArgumentException.class, () -> new SqlIdentifier(".").toString()),
                () -> assertThrows(IllegalArgumentException.class, () -> new SqlIdentifier("1test").toString()),
                () -> assertThrows(IllegalArgumentException.class, () -> new SqlIdentifier(".test").toString()),
                () -> assertThrows(IllegalArgumentException.class, () -> new SqlIdentifier("#").toString()),
                () -> assertThrows(IllegalArgumentException.class, () -> new SqlIdentifier("test#test").toString()),
                () -> assertThrows(IllegalArgumentException.class, () -> new SqlIdentifier("test@test").toString())
        );
    }
}
