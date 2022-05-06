package com.jiawen.community;

import com.jiawen.community.dao.AlphaDao;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;



@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private org.springframework.context.ApplicationContext applicationContext;
	@Test
	void contextLoads() {
		System.out.println(this.applicationContext);
	}


	@Test
	void differentImplTest(){
		AlphaDao alphaDao1 = applicationContext.getBean("impl1",AlphaDao.class);
		System.out.println(alphaDao1.select());

		AlphaDao alphaDao = applicationContext.getBean("impl2",AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		//第一个参数是代表其中一个实现类的名字，第二个是指定接口类型名字，也可以写然后
		//强转即可

	}
}
