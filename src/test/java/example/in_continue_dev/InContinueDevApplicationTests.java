package example.in_continue_dev;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/*
> Task :test InContinueDevApplicationTests > contextLoads()
FAILED java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
Caused by: org.springframework.beans.factory.BeanCreationException at AbstractAutowireCapableBeanFactory.java:1806
Caused by: jakarta.persistence.PersistenceException at AbstractEntityManagerFactoryBean.java:421
Caused by: org.hibernate.exception.JDBCConnectionException at SQLStateConversionDelegate.java:100
Caused by: com.mysql.cj.jdbc.exceptions.CommunicationsException at SQLError.java:174
Caused by: com.mysql.cj.exceptions.CJCommunicationsException at Constructor.java:500
Caused by: java.net.UnknownHostException at InetAddress.java:801 1 test completed, 1 failed > Task :test FAILED 7 actionable
		tasks: 7 executed FAILURE: Build failed with an exception. * What went wrong: Execution failed for task ':test'. > There were failing tests.
		See the report at: file:///home/runner/work/one-day-com/one-day-com/build/reports/tests/test/index.html * Try: > Run with --scan to get full insights.
		BUILD FAILED in 43s

위와 같은 오류가 github actions에서 발생하면 gradle가 jar로 packaging하는데 test쪽에서 문제가 발생한 것이다.
따라서 springBootTest annotation에 classes 속성을 넣어줘라
이유는 찾아보자
 */
@SpringBootTest(classes = InContinueDevApplicationTests.class)
class InContinueDevApplicationTests {

	@Test
	void contextLoads() {
	}

}
