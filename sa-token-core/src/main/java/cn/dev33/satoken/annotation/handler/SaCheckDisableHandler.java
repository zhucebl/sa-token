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
package cn.dev33.satoken.annotation.handler;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.stp.StpLogic;

import java.lang.reflect.Method;

/**
 * 注解 SaCheckDisable 的处理器
 *
 * @author click33
 * @since 2024/8/2
 */
public class SaCheckDisableHandler implements SaAnnotationHandlerInterface<SaCheckDisable> {

    @Override
    public Class<SaCheckDisable> getHandlerAnnotationClass() {
        return SaCheckDisable.class;
    }

    @Override
    public void checkMethod(SaCheckDisable at, Method method) {
        _checkMethod(at.type(), at.value(), at.level());
    }

    public static void _checkMethod(String type, String[] value, int level) {
        StpLogic stpLogic = SaManager.getStpLogic(type, false);

        Object loginId = stpLogic.getLoginId();
        for (String service : value) {
            stpLogic.checkDisableLevel(loginId, service, level);
        }
    }

}