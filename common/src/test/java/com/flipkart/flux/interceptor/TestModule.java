/*
 * Copyright 2012-2016, the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.flux.interceptor;

import com.flipkart.flux.guice.interceptor.TransactionInterceptor;
import com.flipkart.flux.persistence.SessionFactoryContext;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import org.hibernate.Session;

import javax.transaction.Transactional;

/**
 * Created by gaurav.ashok on 24/11/16.
 */
public class TestModule extends AbstractModule {

    private final SessionFactoryContext context;
    private final Session shardedReadWriteSession;
    private final Session shardedReadOnlySession;
    private final Session schedulerSession;


    public TestModule(SessionFactoryContext context, Session shardedReadWriteSession,
                      Session shardedReadOnlySession, Session schedulerSession) {
        this.context = context;
        this.shardedReadWriteSession = shardedReadWriteSession;
        this.shardedReadOnlySession = shardedReadOnlySession;
        this.schedulerSession = schedulerSession;
    }

    @Override
    protected void configure() {
        bind(SessionFactoryContext.class).toInstance(context);
        bind(Session.class).annotatedWith(Names.named("shardedReadWriteSession")).
                toInstance(shardedReadWriteSession);
        bind(Session.class).annotatedWith(Names.named("shardedReadOnlySession")).
                toInstance(shardedReadOnlySession);
        bind(Session.class).annotatedWith(Names.named("schedulerSession")).
                toInstance(schedulerSession);
        bind(InterceptedClass.class);
        bindInterceptor(Matchers.subclassesOf(InterceptedClass.class), Matchers.annotatedWith(Transactional.class), new TransactionInterceptor(() -> context));
    }
}
