package com.hiring.somanath_task.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hiring.somanath_task.data.local.database.AppDatabase
import com.hiring.somanath_task.data.local.database.DatabaseHelper
import com.hiring.somanath_task.data.local.database.dao.HoldingsDao
import com.hiring.somanath_task.data.remote.ApiService
import com.hiring.somanath_task.data.repository.PortfolioRepositoryImpl
import com.hiring.somanath_task.domain.Interactor
import com.hiring.somanath_task.domain.repository.LocalDataSource
import com.hiring.somanath_task.domain.repository.PortfolioRepository
import com.hiring.somanath_task.domain.repository.RemoteDataSource
import com.hiring.somanath_task.domain.usecase.CalculatePortfolioUseCase
import com.hiring.somanath_task.domain.usecase.GetCachedHoldingUseCase
import com.hiring.somanath_task.domain.usecase.GetHoldingUseCase
import com.hiring.somanath_task.domain.usecase.RefreshHoldingUseCase
import com.hiring.somanath_task.presentation.viewmodel.PortfolioViewModel
import com.hiring.somanath_task.util.logging.AndroidLogger
import com.hiring.somanath_task.util.logging.Logger

interface AppContainer {
    val portfolioViewModelFactory: PortfolioViewModelFactory
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val logger: Logger by lazy {
        AndroidLogger()
    }
    private val databaseHelper: DatabaseHelper by lazy {
        DatabaseHelper(context)
    }

    private val appDatabase: AppDatabase by lazy {
        AppDatabase(databaseHelper)
    }

    private val localDataSource: LocalDataSource by lazy {
        HoldingsDao(appDatabase)
    }

    private val remoteDataSource: RemoteDataSource by lazy {
        ApiService(logger)
    }

    private val portfolioRepository: PortfolioRepository by lazy {
        PortfolioRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource
        )
    }
    private val calculatePortfolioUseCase: CalculatePortfolioUseCase by lazy {
        CalculatePortfolioUseCase()
    }

    private val getHoldingUseCase : GetHoldingUseCase by lazy {
        GetHoldingUseCase(portfolioRepository)
    }

    private val getCachedHoldingUseCase: GetCachedHoldingUseCase by lazy {
        GetCachedHoldingUseCase(portfolioRepository)
    }

    private val refreshHoldingUseCase: RefreshHoldingUseCase by lazy {
        RefreshHoldingUseCase(portfolioRepository)
    }

    private val interactor: Interactor by lazy {
        Interactor(getHoldingUseCase,refreshHoldingUseCase, calculatePortfolioUseCase, getCachedHoldingUseCase)
    }
    override val portfolioViewModelFactory: PortfolioViewModelFactory by lazy {
        PortfolioViewModelFactory(
            interactor
        )
    }
}

class PortfolioViewModelFactory(
    private val interactor: Interactor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PortfolioViewModel::class.java)) {
            return PortfolioViewModel(
                interactor
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}