package com.example.agrigenius360

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt


data class MetricStats(
    val mean: Double,
    val median: Double,
    val mode: Double?
)

data class PlantComparisonStats(
    val plantName: String,
    val heightStats: MetricStats,
    val growthRateStats: MetricStats
)

data class ChartPoint(
    val plantName: String,
    val height: Double,
    val growthRate: Double
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _availablePlantNames = MutableStateFlow<List<String>>(emptyList())
    val availablePlantNames: StateFlow<List<String>> = _availablePlantNames.asStateFlow()

    private val _selectedPlantNames = MutableStateFlow<List<String>>(emptyList())
    val selectedPlantNames: StateFlow<List<String>> = _selectedPlantNames.asStateFlow()

    private val _comparisonResults = MutableStateFlow<List<PlantComparisonStats>>(emptyList())
    val comparisonResults: StateFlow<List<PlantComparisonStats>> = _comparisonResults.asStateFlow()

    private val _chartData = MutableStateFlow<List<ChartPoint>>(emptyList())
    val chartData: StateFlow<List<ChartPoint>> = _chartData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            plantRepository.getAllPlantNames().collectLatest { names ->
                _availablePlantNames.value = names
            }
        }
        viewModelScope.launch {
            _selectedPlantNames.collectLatest { names ->
                if (names.isNotEmpty()) {
                    _isLoading.value = true
                    fetchAndCalculatePlantData(names)
                } else {
                    _comparisonResults.value = emptyList()
                    _chartData.value = emptyList()
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun fetchAndCalculatePlantData(plantNames: List<String>) {
        val comparisonStatsList = mutableListOf<PlantComparisonStats>()
        val chartPointsList = mutableListOf<ChartPoint>()

        for (name in plantNames) {
            val plantEntity = plantRepository.getPlantIdByName(name)
            if (plantEntity != null) {
                val growthRecords = plantRepository.getGrowthRecordsForPlant(plantEntity.id).first()

                val heights = growthRecords.map { it.heightCm }
                val growthRates = growthRecords.map { it.growthRate.toDouble() }

                if (heights.isNotEmpty() && growthRates.isNotEmpty()) {
                    val heightStats = calculateStats(heights)
                    val growthRateStats = calculateStats(growthRates)

                    comparisonStatsList.add(
                        PlantComparisonStats(
                            plantName = name,
                            heightStats = heightStats,
                            growthRateStats = growthRateStats
                        )
                    )

                    val minSize = minOf(heights.size, growthRates.size)
                    for (i in 0 until minSize) {
                        chartPointsList.add(ChartPoint(name, heights[i], growthRates[i]))
                    }
                }
            }
        }
        _comparisonResults.value = comparisonStatsList
        _chartData.value = chartPointsList
        _isLoading.value = false
    }


    fun addPlantToSelection(plantName: String) {
        if (!_selectedPlantNames.value.contains(plantName)) {
            _selectedPlantNames.value = _selectedPlantNames.value + plantName
        }
    }

    fun removePlantFromSelection(plantName: String) {
        _selectedPlantNames.value = _selectedPlantNames.value - plantName
    }

    fun clearSelection() {
        _selectedPlantNames.value = emptyList()
    }
    private fun calculateStats(data: List<Double>): MetricStats {
        if (data.isEmpty()) {
            return MetricStats(0.0, 0.0, null)
        }

        val mean = data.sum() / data.size

        val sortedData = data.sorted()
        val median: Double = if (sortedData.size % 2 == 1) {
            sortedData[sortedData.size / 2]
        } else {
            val mid1 = sortedData[sortedData.size / 2 - 1]
            val mid2 = sortedData[sortedData.size / 2]
            (mid1 + mid2) / 2.0
        }
        val mode: Double? = data.groupBy { it.roundTo(2) }
            .maxByOrNull { it.value.size }
            ?.takeIf { it.value.size > 1 }
            ?.key
        return MetricStats(mean, median, mode)
    }
    private fun Double.roundTo(numDecimalPlaces: Int): Double {
        val multiplier = 10.0.pow(numDecimalPlaces)
        return (this * multiplier).roundToInt() / multiplier
    }
}