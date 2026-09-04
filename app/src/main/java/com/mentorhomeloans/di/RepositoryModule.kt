package com.mentorhomeloans.di

import com.mentorhomeloans.data.repository.RemoteAuthRepository
import com.mentorhomeloans.data.repository.RemoteLoanRepository
import com.mentorhomeloans.data.repository.RemoteProfileRepository
import com.mentorhomeloans.data.repository.MockDocumentRepository
import com.mentorhomeloans.data.repository.MockLoanRepository
import com.mentorhomeloans.data.repository.MockNotificationRepository
import com.mentorhomeloans.data.repository.MockProfileRepository
import com.mentorhomeloans.data.repository.MockRepaymentRepository
import com.mentorhomeloans.data.repository.RemoteRepaymentRepository
import com.mentorhomeloans.data.repository.RemoteStatementRepository
import com.mentorhomeloans.data.repository.MockStatementRepository
import com.mentorhomeloans.data.repository.MockSupportRepository
import com.mentorhomeloans.data.repository.MockTransactionRepository
import com.mentorhomeloans.data.repository.RemoteRegistrationRepository
import com.mentorhomeloans.domain.repository.AuthRepository
import com.mentorhomeloans.domain.repository.DocumentRepository
import com.mentorhomeloans.domain.repository.LoanRepository
import com.mentorhomeloans.domain.repository.NotificationRepository
import com.mentorhomeloans.domain.repository.RegistrationRepository
import com.mentorhomeloans.domain.repository.ProfileRepository
import com.mentorhomeloans.domain.repository.RepaymentRepository
import com.mentorhomeloans.domain.repository.StatementRepository
import com.mentorhomeloans.domain.repository.SupportRepository
import com.mentorhomeloans.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding domain Repository interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: RemoteAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLoanRepository(impl: RemoteLoanRepository): LoanRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: MockTransactionRepository): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindStatementRepository(impl: RemoteStatementRepository): StatementRepository


    @Binds
    @Singleton
    abstract fun bindDocumentRepository(impl: MockDocumentRepository): DocumentRepository

    @Binds
    @Singleton
    abstract fun bindRepaymentRepository(impl: RemoteRepaymentRepository): RepaymentRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: RemoteProfileRepository): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindSupportRepository(impl: MockSupportRepository): SupportRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: MockNotificationRepository): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindRegistrationRepository(impl: RemoteRegistrationRepository): RegistrationRepository
}
