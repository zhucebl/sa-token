/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token 持久层实现 [ Redis存储、fastjson序列化 ]
 * 
 * @author sikadai
 * @since 1.34.0
 */
@Component
public class SaTokenDaoRedisFastjson implements SaTokenDao {

	/**
	 * String 读写专用
	 */
	public StringRedisTemplate stringRedisTemplate;	

	/**
	 * Object 读写专用
	 */
	public StringRedisTemplate objectRedisTemplate;

	/**
	 * 标记：是否已初始化成功
	 */
	public boolean isInit;
	
	@Autowired
	public void init(RedisConnectionFactory connectionFactory) {
		// 如果已经初始化成功了，就立刻退出，不重复初始化
		if(this.isInit) {
			return;
		}

		// 重写 SaSession 生成策略 
		SaStrategy.instance.createSession = (sessionId) -> new SaSessionForFastjsonCustomized(sessionId);
		
		// 指定相应的序列化方案 
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		StringRedisSerializer valueSerializer = new StringRedisSerializer();

		// 构建StringRedisTemplate
		StringRedisTemplate stringTemplate = new StringRedisTemplate();
		stringTemplate.setConnectionFactory(connectionFactory);
		stringTemplate.afterPropertiesSet();

		// 构建RedisTemplate
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(keySerializer);
		template.setHashKeySerializer(keySerializer);
		template.setValueSerializer(valueSerializer);
		template.setHashValueSerializer(valueSerializer);
		template.afterPropertiesSet();

		// 开始初始化相关组件 
		this.stringRedisTemplate = stringTemplate;
		this.objectRedisTemplate = template;

		// 打上标记，表示已经初始化成功，后续无需再重新初始化
		this.isInit = true;
	}
	
	
	/**
	 * 获取Value，如无返空 
	 */
	@Override
	public String get(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 写入Value，并设定存活时间 (单位: 秒)
	 */
	@Override
	public void set(String key, String value, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			stringRedisTemplate.opsForValue().set(key, value);
		} else {
			stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 修改指定key-value键值对 (过期时间不变) 
	 */
	@Override
	public void update(String key, String value) {
		long expire = getTimeout(key);
		// -2 = 无此键 
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		this.set(key, value, expire);
	}
	
	/**
	 * 删除Value 
	 */
	@Override
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}

	/**
	 * 获取Value的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public long getTimeout(String key) {
		return stringRedisTemplate.getExpire(key);
	}

	/**
	 * 修改Value的剩余存活时间 (单位: 秒) 
	 */
	@Override
	public void updateTimeout(String key, long timeout) {
		// 判断是否想要设置为永久
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			long expire = getTimeout(key);
			if(expire == SaTokenDao.NEVER_EXPIRE) {
				// 如果其已经被设置为永久，则不作任何处理 
			} else {
				// 如果尚未被设置为永久，那么再次set一次
				this.set(key, this.get(key), timeout);
			}
			return;
		}
		stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}
	
	
	/**
	 * 获取Object，如无返空 
	 */
	@Override
	public Object getObject(String key) {
		return objectRedisTemplate.opsForValue().get(key);
	}

	@Override
	public SaSession getSession(String sessionId) {
		Object obj = getObject(sessionId);
		if (obj == null) {
			return null;
		}
		return JSON.parseObject(obj.toString(), SaSessionForFastjsonCustomized.class);
	}

	/**
	 * 写入Object，并设定存活时间 (单位: 秒) 
	 */
	@Override
	public void setObject(String key, Object object, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		String toValue = JSON.toJSONString(object);
		// 判断是否为永不过期 
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			objectRedisTemplate.opsForValue().set(key, toValue);
		} else {
			objectRedisTemplate.opsForValue().set(key, toValue, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 更新Object (过期时间不变) 
	 */
	@Override
	public void updateObject(String key, Object object) {
		long expire = getObjectTimeout(key);
		// -2 = 无此键 
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		this.setObject(key, object, expire);
	}

	/**
	 * 删除Object 
	 */
	@Override
	public void deleteObject(String key) {
		objectRedisTemplate.delete(key);
	}

	/**
	 * 获取Object的剩余存活时间 (单位: 秒)
	 */
	@Override
	public long getObjectTimeout(String key) {
		return objectRedisTemplate.getExpire(key);
	}

	/**
	 * 修改Object的剩余存活时间 (单位: 秒)
	 */
	@Override
	public void updateObjectTimeout(String key, long timeout) {
		// 判断是否想要设置为永久
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			long expire = getObjectTimeout(key);
			if(expire == SaTokenDao.NEVER_EXPIRE) {
				// 如果其已经被设置为永久，则不作任何处理 
			} else {
				// 如果尚未被设置为永久，那么再次set一次
				this.setObject(key, this.getObject(key), timeout);
			}
			return;
		}
		objectRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}


	
	/**
	 * 搜索数据 
	 */
	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		Set<String> keys = stringRedisTemplate.keys(prefix + "*" + keyword + "*");
		List<String> list = new ArrayList<>(keys);
		return SaFoxUtil.searchList(list, start, size, sortType);
	}
	
	
}
