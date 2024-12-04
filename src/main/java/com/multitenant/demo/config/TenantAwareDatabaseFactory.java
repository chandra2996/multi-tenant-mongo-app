package com.multitenant.demo.config;

import com.mongodb.ClientSessionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.SessionAwareMethodInterceptor;
import org.springframework.data.mongodb.core.MongoExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class TenantAwareDatabaseFactory implements MongoDatabaseFactory {
    private final TenantMongoClientProvider mongoClientProvider;
    private final PersistenceExceptionTranslator exceptionTranslator;

    private @Nullable WriteConcern writeConcern;

    protected TenantAwareDatabaseFactory(TenantMongoClientProvider mongoClientProvider) {

        this.mongoClientProvider = mongoClientProvider;
        this.exceptionTranslator = new MongoExceptionTranslator();
    }

    @Override
    public MongoDatabase getMongoDatabase() throws DataAccessException {
        return this.mongoClientProvider.getTenantMongoClient().getDatabase(mongoClientProvider.getDefaultDatabase());
    }

    @Override
    public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
        MongoDatabase db = this.mongoClientProvider.getTenantMongoClient().getDatabase(dbName);
        if (writeConcern == null) {
            return db;
        }

        return db.withWriteConcern(writeConcern);
    }

    @Override
    public PersistenceExceptionTranslator getExceptionTranslator() {
        return this.exceptionTranslator;
    }

    @Override
    public ClientSession getSession(ClientSessionOptions options) {
        return this.mongoClientProvider.getTenantMongoClient().startSession(options);
    }

    @Override
    public MongoDatabaseFactory withSession(ClientSession session) {
        return new ClientSessionBoundMongoDbFactory(session, this);
    }

    static final class ClientSessionBoundMongoDbFactory implements MongoDatabaseFactory {

        private final ClientSession session;
        private final MongoDatabaseFactory delegate;

        public ClientSessionBoundMongoDbFactory(ClientSession session, MongoDatabaseFactory delegate) {
            this.session = session;
            this.delegate = delegate;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.mongodb.MongoDbFactory#getMongoDatabase()
         */
        @Override
        public MongoDatabase getMongoDatabase() throws DataAccessException {
            return proxyMongoDatabase(delegate.getMongoDatabase());
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.mongodb.MongoDbFactory#getMongoDatabase(java.lang.String)
         */
        @Override
        public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
            return proxyMongoDatabase(delegate.getMongoDatabase(dbName));
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.mongodb.MongoDbFactory#getExceptionTranslator()
         */
        @Override
        public PersistenceExceptionTranslator getExceptionTranslator() {
            return delegate.getExceptionTranslator();
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.mongodb.MongoDbFactory#getSession(com.mongodb.ClientSessionOptions)
         */
        @Override
        public ClientSession getSession(ClientSessionOptions options) {
            return delegate.getSession(options);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.mongodb.MongoDbFactory#withSession(com.mongodb.session.ClientSession)
         */
        @Override
        public MongoDatabaseFactory withSession(ClientSession session) {
            return delegate.withSession(session);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.mongodb.MongoDbFactory#isTransactionActive()
         */
        @Override
        public boolean isTransactionActive() {
            return session != null && session.hasActiveTransaction();
        }

        private MongoDatabase proxyMongoDatabase(MongoDatabase database) {
            return createProxyInstance(session, database, MongoDatabase.class);
        }

        private MongoDatabase proxyDatabase(com.mongodb.session.ClientSession session, MongoDatabase database) {
            return createProxyInstance(session, database, MongoDatabase.class);
        }

        private MongoCollection<?> proxyCollection(com.mongodb.session.ClientSession session,
                                                   MongoCollection<?> collection) {
            return createProxyInstance(session, collection, MongoCollection.class);
        }

        private <T> T createProxyInstance(com.mongodb.session.ClientSession session, T target, Class<T> targetType) {

            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(target);
            factory.setInterfaces(targetType);
            factory.setOpaque(true);

            factory.addAdvice(new SessionAwareMethodInterceptor<>(session, target, ClientSession.class, MongoDatabase.class,
                    this::proxyDatabase, MongoCollection.class, this::proxyCollection));

            return targetType.cast(factory.getProxy(target.getClass().getClassLoader()));
        }

        public ClientSession getSession() {
            return this.session;
        }

        public MongoDatabaseFactory getDelegate() {
            return this.delegate;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            ClientSessionBoundMongoDbFactory that = (ClientSessionBoundMongoDbFactory) o;

            if (!ObjectUtils.nullSafeEquals(this.session, that.session)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.delegate, that.delegate);
        }

        @Override
        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.session);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.delegate);
            return result;
        }

        public String toString() {
            return "MongoDatabaseFactorySupport.ClientSessionBoundMongoDbFactory(session=" + this.getSession() + ", delegate="
                    + this.getDelegate() + ")";
        }
    }
}
