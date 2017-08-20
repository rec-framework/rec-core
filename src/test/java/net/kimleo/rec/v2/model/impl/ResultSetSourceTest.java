package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.common.concept.Mapped;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ResultSetSourceTest {
    @Test
    public void shouldPass() throws Exception {
        ResultSet mockedResultSet = mock(ResultSet.class);
        when(mockedResultSet.getObject("key")).thenReturn("1").thenReturn("2");

        when(mockedResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        List<Mapped<String>> list = new ResultSetSource(mockedResultSet)
                .stream().collect(Collectors.toList());

        assertThat(list.size(), is(2));

        assertThat(list.get(0).get("key"), is("1"));
        assertThat(list.get(1).get("key"), is("2"));
    }

    @Test
    public void shouldWhenThrowException() throws Exception {
        ResultSet mockedResultSet = mock(ResultSet.class);
        when(mockedResultSet.getObject("key")).thenReturn("1").thenReturn("2");

        when(mockedResultSet.next()).thenReturn(true).thenReturn(true).thenThrow(new SQLException());

        List<Mapped<String>> list = new ResultSetSource(mockedResultSet)
                .stream().collect(Collectors.toList());

        assertThat(list.size(), is(2));

        assertThat(list.get(0).get("key"), is("1"));
        assertThat(list.get(1).get("key"), is("2"));
    }

}