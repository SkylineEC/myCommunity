package com.jiawen.community;

import com.jiawen.community.dao.AlphaDao;
import com.jiawen.community.service.AlphaBeanService;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private org.springframework.context.ApplicationContext applicationContext;


	//我希望Spring容器将AlphaBeanService注册为一个属性
	// Autowired 相当于把这个service注入到容器中

	@Autowired
	AlphaBeanService alphaBeanService;

	@Autowired
	@Qualifier("impl1")//声明选择的是哪个实现类
	AlphaDao alphaDao;
	@Test
	void testDI(){
		System.out.println(this.alphaBeanService);
	}

	@Test
	void contextLoads() {
		System.out.println(this.applicationContext);
	}

	@Test
	void testBeanManagement(){
		//发现这个bean只有一个实例化 说明是单例开发
		//Spring 默认
		AlphaBeanService abs = applicationContext.getBean(AlphaBeanService.class);
		System.out.println(abs);
	}

	@Test
	void differentImplTest(){
		AlphaDao alphaDao1 = applicationContext.getBean("impl1",AlphaDao.class);
		System.out.println(alphaDao1.select("test"));

		AlphaDao alphaDao = applicationContext.getBean("impl2",AlphaDao.class);
		System.out.println(alphaDao.select("test"));
	}

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		//第一个参数是代表其中一个实现类的名字，第二个是指定接口类型名字，也可以写然后
		//强转即可

	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}
	//有了Autowired,上面的代码可以简化成下面的,因为Spring可以自动注入
	@Autowired
	SimpleDateFormat simpleDateFormat;
}
