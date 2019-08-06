package me.huqiao.loganlyzer.querylanguage;

import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.exception.InvalidLQLException;
import org.junit.Test;

public class QueryStringParserTest {

    @Test
    public void test() throws Exception {
        String lql = "select 0,2,3 from localhost_access_log.txt,localhost_access_log02.txt where 0 eq 'x' and (2 eq 'x'  or 3 eq 'x') order by 0 desc, 1 asc group by 0 limit 10";
        LogQueryLanguageParserStateMachine qsr = new LogQueryLanguageParserStateMachine(lql);
        qsr.start();
    }


    @Test
    public void testQuery() throws Exception {
        String lql = "select 0,2,3 from 'localhost_access_log.2019-03-14.txt' limit 10";
        LogQueryLanguageParserStateMachine qsr = new LogQueryLanguageParserStateMachine(lql);
        qsr.start();
        for(Object obj : qsr.getFileScaner().list()){
            System.out.println(obj);
        }

    }


    @Test
    public void testCompareType() throws Exception {
        String lql = "select 0,2,3 from xxx where 0 = 'x' and (2 != 'x'  or 3 >= 'x' ) and 2 regex 'test' ";
        LogQueryLanguageParserStateMachine qsr = new LogQueryLanguageParserStateMachine(lql);
        qsr.start();
    }
}
