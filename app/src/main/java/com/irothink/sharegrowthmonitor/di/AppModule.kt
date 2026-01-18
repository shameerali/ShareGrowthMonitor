package com.irothink.sharegrowthmonitor.di

import android.content.Context
import androidx.room.Room
import com.irothink.sharegrowthmonitor.data.local.ShareGrowthDatabase
import com.irothink.sharegrowthmonitor.data.local.dao.TransactionDao
import com.irothink.sharegrowthmonitor.data.repository.PortfolioRepositoryImpl
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideShareGrowthDatabase(
        @ApplicationContext context: Context
    ): ShareGrowthDatabase {
        return Room.databaseBuilder(
            context,
            ShareGrowthDatabase::class.java,
            "share_growth_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: ShareGrowthDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun providePortfolioRepository(
        transactionDao: TransactionDao
    ): PortfolioRepository {
        return PortfolioRepositoryImpl(transactionDao)
    }

    @Provides
    @Singleton
    fun provideCompanyInfoDao(database: ShareGrowthDatabase): com.irothink.sharegrowthmonitor.data.local.dao.CompanyInfoDao {
        return database.companyInfoDao()
    }

    @Provides
    @Singleton
    fun provideCompanyRepository(
        dao: com.irothink.sharegrowthmonitor.data.local.dao.CompanyInfoDao
    ): com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository {
        return com.irothink.sharegrowthmonitor.data.repository.CompanyRepositoryImpl(dao)
    }
}
