package com.coffer.core.common.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 
 * @author LLF
 * 容器加载事件监听
 * 
 * @version 2018-05-23
 *
 */
@Component("BeanContextRefreshedEvent")
public class BeanContextRefreshedEvent implements ApplicationListener<ContextRefreshedEvent>{
	public static int count = 0;
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() != null) {
			count = count + 1;
			System.out.println("spring容易初始化完毕================================================" + count);
		}
	}
}
