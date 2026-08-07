package com.mentorhomeloans.di

import com.mentorhomeloans.data.repository.MockAuthRepository
import com.mentorhomeloans.data.repository.MockDocumentRepository
import com.mentorhomeloans.data.repository.MockLoanRepository
import com.mentorhomeloans.data.repository.MockNotificationRepository
import com.mentorhomeloans.data.repository.MockProfileRepository
import com.mentorhomeloans.data.repository.MockRepaymentRepository
import com.mentorhomeloans.data.repository.MockStatementRepository
import com.mentorhomeloans.data.repository.MockSupportRepository
import com.mentorhomeloans.data.repository.MockTransactionRepository
import com.mentorhomeloans.domain.repository.AuthRepository
import com.mentorhomeloans.domain.repository.DocumentRepository
import com.mentorhomeloans.domain.repository.LoanRepository
import com.mentorhomeloans.domain.repository.NotificationRepository
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
 * Hilt module binding domain Repository interfaces to their Mock implementations.
 *
 * To switch from Mock to Remote: replace each Mock* binding with the
 * corresponding Remote* implementation class without touching the use cases
 * or ViewModels (Open/Closed Principle).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: MockAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLoanRepository(impl: MockLoanRepository): LoanRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: MockTransactionRepository): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindStatementRepository(impl: MockStatementRepository): StatementRepository

    @Binds
    @Singleton
    abstract fun bindDocumentRepository(impl: MockDocumentRepository): DocumentRepository

    @Binds
    @Singleton
    abstract fun bindRepaymentRepository(impl: MockRepaymentRepository): RepaymentRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: MockProfileRepository): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindSupportRepository(impl: MockSupportRepository): SupportRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: MockNotificationRepository): NotificationRepository
}
