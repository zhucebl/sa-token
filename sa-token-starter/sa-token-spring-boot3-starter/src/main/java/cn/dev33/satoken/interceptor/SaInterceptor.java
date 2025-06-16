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
package cn.dev33.satoken.interceptor;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * Sa-Token 综合拦截器，提供注解鉴权和路由拦截鉴权能力 
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaInterceptor implements HandlerInterceptor {

	/**
	 * 是否打开注解鉴权 
	 */
	public boolean isAnnotation = true;
	
	/**
	 * 认证函数：每次请求执行 
	 * <p> 参数：路由处理函数指针 
	 */
	public SaParamFunction<Object> auth = handler -> {};

	/**
	 * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力 
	 */
	public SaInterceptor() {
	}

	/**
	 * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力 
	 * @param auth 认证函数，每次请求执行 
	 */
	public SaInterceptor(SaParamFunction<Object> auth) {
		this.auth = auth;
	}

	/**
	 * 设置是否打开注解鉴权 
	 * @param isAnnotation /
	 * @return 对象自身 
	 */
	public SaInterceptor isAnnotation(boolean isAnnotation) {
		this.isAnnotation = isAnnotation;
		return this;
	}
	
	/**
	 * 写入[认证函数]: 每次请求执行 
	 * @param auth / 
	 * @return 对象自身 
	 */
	public SaInterceptor setAuth(SaParamFunction<Object> auth) {
		this.auth = auth;
		return this;
	}
	
	
	// ----------------- 验证方法 ----------------- 

	/**
	 * 每次请求之前触发的方法 
	 */
	@Override
	@SuppressWarnings("all")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		try {

			// 这里必须确保 handler 是 HandlerMethod 类型时，才能进行注解鉴权
			if(isAnnotation && handler instanceof HandlerMethod) {
				Method method = ((HandlerMethod) handler).getMethod();
				SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
			}
			
			// Auth 校验  
			auth.run(handler);
			
		} catch (StopMatchException e) {
			// StopMatchException 异常代表：停止匹配，进入Controller

		} catch (BackResultException e) {
			// BackResultException 异常代表：停止匹配，向前端输出结果
			// 		请注意此处默认 Content-Type 为 text/plain，如果需要返回 JSON 信息，需要在 back 前自行设置 Content-Type 为 application/json
			// 		例如：SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
			if(response.getContentType() == null) {
				response.setContentType("text/plain; charset=utf-8"); 
			}
			response.getWriter().print(e.getMessage());
			return false;
		}
		
		// 通过验证 
		return true;
	}

}
