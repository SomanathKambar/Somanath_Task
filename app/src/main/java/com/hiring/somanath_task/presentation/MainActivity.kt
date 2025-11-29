package com.hiring.somanath_task.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiring.somanath_task.R
import com.hiring.somanath_task.databinding.ActivityMainBinding
import com.hiring.somanath_task.di.DefaultAppContainer
import com.hiring.somanath_task.domain.model.PortfolioSummary
import com.hiring.somanath_task.presentation.adapter.HoldingsAdapter
import com.hiring.somanath_task.presentation.state.UiState
import com.hiring.somanath_task.presentation.viewmodel.PortfolioViewModel
import com.hiring.somanath_task.util.extensions.setOnSingleClickListener
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var holdingsAdapter: HoldingsAdapter
    private var isExpanded = false


    private val appContainer by lazy { DefaultAppContainer(applicationContext) }
    private val viewModel: PortfolioViewModel by viewModels {
        appContainer.portfolioViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.enableEdgeToEdge(window)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRoot)) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply padding only to the bottom (and potentially left/right for foldables/landscape)
            view.setPadding(
                view.paddingLeft, // keep existing left padding
                view.paddingTop, // keep existing top padding (or apply top inset here if needed)
                view.paddingRight, // keep existing right padding
                systemBarsInsets.bottom // apply bottom inset to avoid navigation bar overlap
            )
            insets // return the insets
        }
        holdingsAdapter = HoldingsAdapter(this)
        binding.holdingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = holdingsAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }

        binding.portfolioSummary.summaryHeaderCollapsed.setOnSingleClickListener {
            viewModel.toggleSummaryExpanded()
        }

        binding.portfolioSummary.summaryHeaderExpanded.setOnSingleClickListener {
            viewModel.toggleSummaryExpanded()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshHoldings()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.errorLayout.retryButton.setOnSingleClickListener {
            viewModel.clearError()
            viewModel.refreshHoldings()
        }

        binding.errorLayout.offlineRetryButton.setOnSingleClickListener {
            viewModel.retryWithOfflineData()
        }

        binding.emptyState.emptyRetryButton.setOnSingleClickListener {
            viewModel.refreshHoldings()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                updateUi(uiState)
            }
        }

        lifecycleScope.launch {
            viewModel.portfolioSummary.collect { summary ->
                summary?.let { updateSummaryView(it) }
            }
        }
    }

    private fun updateUi(uiState: UiState) {
        binding.progressBar.isVisible = uiState.isLoading && !uiState.hasData
        binding.swipeRefreshLayout.isRefreshing = uiState.isRefreshing

        binding.errorLayout.root.isVisible = uiState.errorMessage != null
        uiState.errorMessage?.let { binding.errorLayout.errorText.text = it }

        binding.holdingsRecyclerView.isVisible = uiState.hasData
        binding.emptyState.root.isVisible = !uiState.hasData && !uiState.isLoading && uiState.errorMessage == null

        holdingsAdapter.submitList(uiState.holdings)

        binding.portfolioSummary.summaryHeaderCollapsed.isVisible = uiState.hasData && !uiState.isSummaryExpanded
        binding.portfolioSummary.summaryDetails.isVisible = uiState.hasData && uiState.isSummaryExpanded
        binding.portfolioSummary.summaryHeaderExpanded.isVisible = uiState.hasData && uiState.isSummaryExpanded

        updateSummaryIcon(uiState.isSummaryExpanded)
    }

    private fun updateSummaryView(summary: PortfolioSummary) {
        binding.portfolioSummary.totalCurrentValue.text = summary.getFormattedCurrentValue()
        binding.portfolioSummary.totalInvestment.text = summary.getFormattedInvestment()
        binding.portfolioSummary.todaysPnl.text = summary.getFormattedTodaysPnl()
        binding.portfolioSummary.totalExpanded.text = summary.getFormattedTotalPnl()
        binding.portfolioSummary.totalPnlCollapsed.text = summary.getFormattedTotalPnl()

        val totalPnlColor = if (summary.isTotalProfitable) R.color.profit_green else R.color.loss_red
        val todaysPnlColor = if (summary.isTodaysProfitable) R.color.profit_green else R.color.loss_red

        binding.portfolioSummary.totalPnlCollapsed.setTextColor(getColor(totalPnlColor))
        binding.portfolioSummary.todaysPnl.setTextColor(getColor(todaysPnlColor))
        binding.portfolioSummary.totalExpanded.setTextColor(getColor(totalPnlColor))
    }

    private fun updateSummaryIcon(isExpanded: Boolean) {
        this.isExpanded = isExpanded
    }

}