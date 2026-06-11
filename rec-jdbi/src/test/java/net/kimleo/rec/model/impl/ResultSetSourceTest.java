package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ResultSetSourceTest {
    @Test
    public void shouldPass() throws Exception {
        ResultSet mockedResultSet = mock(ResultSet.class);
        ResultSetMetaData mockedMetaData = mock(ResultSetMetaData.class);

        when(mockedResultSet.getMetaData()).thenReturn(mockedMetaData);
        when(mockedMetaData.getColumnCount()).thenReturn(1);
        when(mockedMetaData.getColumnLabel(1)).thenReturn("key");

        when(mockedResultSet.getObject(1)).thenReturn("1").thenReturn("2");

        when(mockedResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        List<DataSet> list = new ResultSetSource(mockedResultSet)
                .stream().collect(Collectors.toList());

        assertThat(list.size(), is(2));

        assertThat(list.get(0).getString("key"), is("1"));
        assertThat(list.get(1).getString("key"), is("2"));
    }

    @Test
    public void shouldWhenThrowException() throws Exception {
        ResultSet mockedResultSet = mock(ResultSet.class);
        ResultSetMetaData mockedMetaData = mock(ResultSetMetaData.class);

        when(mockedResultSet.getMetaData()).thenReturn(mockedMetaData);
        when(mockedMetaData.getColumnCount()).thenReturn(1);
        when(mockedMetaData.getColumnLabel(1)).thenReturn("key");

        when(mockedResultSet.getObject(1)).thenReturn("1").thenReturn("2");

        when(mockedResultSet.next()).thenReturn(true).thenReturn(true).thenThrow(new SQLException());

        List<DataSet> list = new ResultSetSource(mockedResultSet)
                .stream().collect(Collectors.toList());

        assertThat(list.size(), is(2));

        assertThat(list.get(0).getString("key"), is("1"));
        assertThat(list.get(1).getString("key"), is("2"));
    }

}