/**
 * Copyright 2009-2018 the original author or authors.
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
package net.javacrumbs.shedlock.spring.aop;

import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

import java.time.Duration;

abstract class AbstractProxyScheduledLockAopBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor implements BeanPostProcessor, EmbeddedValueResolverAware {
    private final String defaultLockAtMostFor;
    private final String defaultLockAtLeastFor;
    private final LockProvider lockProvider;
    private StringValueResolver resolver;

    AbstractProxyScheduledLockAopBeanPostProcessor(String defaultLockAtMostFor, String defaultLockAtLeastFor, LockProvider lockProvider) {
        this.defaultLockAtMostFor = defaultLockAtMostFor;
        this.defaultLockAtLeastFor = defaultLockAtLeastFor;
        this.lockProvider = lockProvider;
    }

    /**
     * Set the StringValueResolver to use for resolving embedded definition values.
     */
    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // this code can not be in afterPropertiesSet() since resolver is not initialized yet
        this.advisor = getAdvisor(lockProvider);
        return super.postProcessAfterInitialization(bean, beanName);
    }

    protected abstract AbstractPointcutAdvisor getAdvisor(LockProvider lockProvider);

    protected Duration defaultLockAtLeastForDuration() {
        return toDuration(defaultLockAtLeastFor);
    }

    protected Duration defaultLockAtMostForDuration() {
        return toDuration(defaultLockAtMostFor);
    }

    protected StringValueResolver getResolver() {
        return resolver;
    }


    private Duration toDuration(String string) {
        return Duration.parse(resolver.resolveStringValue(string));
    }

    public LockProvider getLockProvider() {
        return lockProvider;
    }
}
