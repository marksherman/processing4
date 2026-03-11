package processing.data;

import org.mockito.*;
import java.sql.*;
import java.sql.ResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TableTest {

    class Person {
        public String name;
        public int age;

        public Person() {
            name = "";
            age = -1;
        }
    }


    Person[] people;

    @Test
    public void parseInto() {
        Table table = new Table();
        table.addColumn("name");
        table.addColumn("age");

        TableRow row = table.addRow();
        row.setString("name", "Person1");
        row.setInt("age", 30);

        table.parseInto(this, "people");

        Assert.assertEquals(people[0].name, "Person1");
        Assert.assertEquals(people[0].age, 30);
    }

//    @Before
//    public void setUp() throws Exception {
//        Create mock results set to test initialization of Table from a resultSet
//
//    }

    @Mock
    private ResultSet rs;
    private ResultSetMetaData rsmd;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(rs.getMetaData()).thenReturn(rsmd);
    }

    interface rsRowSetup {
        void setup(ResultSet rs) throws SQLException;
    }

    private ResultSet makeMockCols(String colName, int sqlType, rsRowSetup rowSetup) throws SQLException {
        try {
            // Creates result set data framework
            when(rsmd.getColumnCount()).thenReturn(1);
            when(rsmd.getColumnName(1)).thenReturn(colName);
            when(rsmd.getColumnType(1)).thenReturn(sqlType);

            // Creates one row with TRUE, stops loop from creating a second with FALSE
            when(rs.next()).thenReturn(true, false);
            rowSetup.setup(rs);

            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void varcharIsString() throws SQLException {
        ResultSet mockRS = makeMockCols("name", Types.VARCHAR, r -> when(r.getString(1)).thenReturn("hello"));

        Table t = new Table(mockRS);

        assertEquals(Table.STRING, t.getColumnType(0));
    }

}
