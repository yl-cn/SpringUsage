import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;

public class CommonTest {
    @Test
    public void testLeafYear() throws Exception{
        /*Date testDate = DateUtils.parseDate("19590229", "yyyyMMdd");
        System.out.println(testDate.getDay());

        Date date = DateUtils.parseDateStrictly("19590229", "yyyyMMdd");
        System.out.println(date.getDay());*/

        LocalDate localDate = LocalDate.of(1959,2,28);
        Assert.assertFalse(localDate.isLeapYear());
    }

    @Test
    public void testArrayToLinkedList() {
        String path = "x.y.z.1.2.3";
        LinkedList<String> list = Arrays.stream(path.split("\\."))
                .collect(Collectors.toCollection(LinkedList::new));

        System.out.println(list.getLast());
        Assert.assertEquals("3", list.getLast());
    }

    @Test
    public void tetToStringBuilder() {
        TempEntity entity = new TempEntity();
        entity.setId("1");
        entity.setName("user");
        entity.setPassword("password");

        System.out.println(entity);
    }

    private class TempEntity {
        private String id;

        private String name;

        private String password;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String toString() {
            //包含了完整包名
            //return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
            /*return new ToStringBuilder(this)
                    .append("id",this.id)
                    .append("name", this.name).toString();*/

            //去除包名
            //return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);

            //实体不打印password机密信息使用方法
            return (new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) {
                     protected boolean accept(Field f) {
                         return super.accept(f) && !f.getName().equals("password");
                     }
            }).toString();
        }
    }
}
