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
package cn.dev33.satoken.spring.sso;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 注册 Sa-Token SSO 所需要的 Bean
 *
 * @author click33
 * @since 1.34.0
 */
@ConditionalOnClass(SaSsoManager.class)
public class SaSsoBeanRegister {

	/**
	 * 获取 SSO Server 端 配置对象
	 * @return 配置对象
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token.sso-server")
	public SaSsoServerConfig getSaSsoServerConfig() {
		return new SaSsoServerConfig();
	}

	/**
	 * 获取 SSO Client 端 配置对象
	 * @return 配置对象
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token.sso-client")
	public SaSsoClientConfig getSaSsoClientConfig() {
		return new SaSsoClientConfig();
	}

}
