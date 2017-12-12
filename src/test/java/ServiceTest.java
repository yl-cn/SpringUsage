import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertEquals;

import javax.validation.ConstraintViolationException;

import com.spring.service.IUserService;
import com.spring.service.impl.UserServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@ContextConfiguration(classes = { ServiceTest.Config.class })
@RunWith(SpringRunner.class)
public class ServiceTest {

    @Autowired
    private IUserService userService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFindByNameUsingAssertj() {
        assertThatThrownBy(()->userService.findByName(null))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void testFindByName() {
        expectedException.expect(ConstraintViolationException.class);
        userService.findByName(null);
    }

    @Test
    public void test() {
        assertEquals("password", userService.findByName("xxx").getPassword());
    }

    @Configuration
    public static class Config {
        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }

        @Bean
        public IUserService decisionDao() {
            return new UserServiceImpl();
        }
    }
}
