package hu.droth.kristof.mindlearning.util

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.droth.kristof.mindlearning.util.FileReaderUtil
import hu.droth.kristof.mindlearning.util.NetworkUtil
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil


@InstallIn(SingletonComponent::class)
@Module
object UtilProviders {
    @Provides
    fun provideSharedPreferencesUtil(@ApplicationContext applicationContext: Context) = SharedPreferencesUtil(applicationContext)

    @Provides
    fun provideFileReaderUtil(@ApplicationContext applicationContext: Context) = FileReaderUtil(applicationContext)

    @Provides
    fun provideNetworkUtil(@ApplicationContext applicationContext: Context) = NetworkUtil(applicationContext)
}