package com.mentorhomeloans.di

import com.mentorhomeloans.core.security.captcha.CaptchaManager
import com.mentorhomeloans.core.security.captcha.RecaptchaEnterpriseManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CaptchaModule {

    @Binds
    @Singleton
    abstract fun bindCaptchaManager(
        impl: RecaptchaEnterpriseManager
    ): CaptchaManager
}
