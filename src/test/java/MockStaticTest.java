import com.mysql.jdbc.StringUtils;
import com.spring.util.IdCardValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.Pattern;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.anyString;

@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest({IdCardValidator.class})
public class MockStaticTest {

    @Before
    public void init() throws Exception{
        PowerMockito.mockStatic(IdCardValidator.class);
        PowerMockito.when(IdCardValidator.validateIdCardStrictly(anyString())).thenReturn(true);
        PowerMockito.when(IdCardValidator.convertToIdCard(anyString())).thenReturn(new IdCardValidator.IdCard());

        PowerMockito.when(IdCardValidator.is15Idcard(anyString())).then(returnsFirstArg());

        PowerMockito.when(IdCardValidator.is18Idcard(anyString())).thenAnswer((
                (invocation) -> {

                    Object[]args = invocation.getArguments();
                    String arg = (String)args[0];

                    return Pattern
                            .matches(
                                    "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$",
                                    arg);
                }
        ));
    }

    @Test
    public void test() {
        Assert.fail();
    }
}
