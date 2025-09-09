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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Fastjson2 定制版 SaSession，重写类型转换API、忽略 timeout 字段的序列化
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaSessionForFastjson2Customized extends SaSession {

	private static final long serialVersionUID = -7600983549653130681L;

	public SaSessionForFastjson2Customized() {
		super();
	}

	/**
	 * 构建一个 SaSession 对象
	 * @param id Session 的 id
	 */
	public SaSessionForFastjson2Customized(String id) {
		super(id);
	}

	/**
	 * 取值 (指定转换类型)
	 * @param <T> 泛型
	 * @param key key 
	 * @param cs 指定转换类型 
	 * @return 值 
	 */
	@Override
	public <T> T getModel(String key, Class<T> cs) {
		if(SaFoxUtil.isBasicType(cs)) {
			return SaFoxUtil.getValueByType(get(key), cs);
		}
		return JSON.parseObject(getString(key), cs);
	}

	/**
	 * 取值 (指定转换类型, 并指定值为Null时返回的默认值)
	 * @param <T> 泛型
	 * @param key key 
	 * @param cs 指定转换类型 
	 * @param defaultValue 值为Null时返回的默认值
	 * @return 值 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getModel(String key, Class<T> cs, Object defaultValue) {
		Object value = get(key);
		if(valueIsNull(value)) {
			return (T)defaultValue;
		}
		if(SaFoxUtil.isBasicType(cs)) {
			return SaFoxUtil.getValueByType(get(key), cs);
		}
		return JSON.parseObject(getString(key), cs);
	}

	/**
	 * 忽略 timeout 字段的序列化
	 */
	@Override
	@JSONField(serialize = false)
	public long getTimeout() {
		return super.getTimeout();
	}
	
}
