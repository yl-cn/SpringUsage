import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Date;

public class CommonTest {

    @Test
    public void testLeafYear() throws Exception{
        Date testDate = DateUtils.parseDate("19590229", "yyyyMMdd");
        System.out.println(testDate.getDay());

        Date date = DateUtils.parseDateStrictly("19590229", "yyyyMMdd");
        System.out.println(date.getDay());


    }
}
