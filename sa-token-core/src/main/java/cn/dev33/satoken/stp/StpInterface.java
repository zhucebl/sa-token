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
package cn.dev33.satoken.stp;

import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;

import java.util.List;

/**
 * 权限数据源加载接口
 *
 * <p>
 *     在使用权限校验 API 之前，你必须实现此接口，告诉框架哪些用户拥有哪些权限。<br>
 *     框架默认不对数据进行缓存，如果你的数据是从数据库中读取的，一般情况下你需要手动实现数据的缓存读写。
 * </p>
 * 
 * @author click33
 * @since 1.10.0
 */
public interface StpInterface {

	/**
	 * 返回指定账号id所拥有的权限码集合 
	 * 
	 * @param loginId  账号id
	 * @param loginType 账号类型
	 * @return 该账号id具有的权限码集合
	 */
	List<String> getPermissionList(Object loginId, String loginType);

	/**
	 * 返回指定账号id所拥有的角色标识集合 
	 * 
	 * @param loginId  账号id
	 * @param loginType 账号类型
	 * @return 该账号id具有的角色标识集合
	 */
	List<String> getRoleList(Object loginId, String loginType);

	/**
	 * 返回指定账号 id 是否被封禁
	 *
	 * @param loginId  账号id
	 * @param service 业务标识符
	 * @return 描述该账号是否封禁的包装信息对象
	 */
	default SaDisableWrapperInfo isDisabled(Object loginId, String service) {
		return SaDisableWrapperInfo.createNotDisabled();
	}

}
