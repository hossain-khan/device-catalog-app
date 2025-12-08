package dev.hossain.devicecatalog.feature.statsexplorer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

@AssistedInject
class StatsExplorerPresenter
    constructor(
        @Assisted private val screen: StatsExplorerScreen,
        @Assisted private val navigator: Navigator,
        private val deviceRepository: AndroidDeviceRepository,
    ) : Presenter<StatsExplorerScreen.State> {
        @Composable
        override fun present(): StatsExplorerScreen.State {
            var selectedCategory by remember {
                mutableStateOf(screen.initialCategory ?: StatCategory.RAM)
            }

            val statData by produceState<StatData?>(
                initialValue = null,
                key1 = selectedCategory,
            ) {
                // Reset to null immediately when category changes to clear old data
                value = null
                Timber.d("Loading stats for category: $selectedCategory")
                try {
                    when (selectedCategory) {
                        StatCategory.RAM -> {
                            deviceRepository.getDeviceStats().collect { stats ->
                                val distribution =
                                    stats.ramDistribution
                                        .take(15)
                                        .associate { it.ram to it.count }
                                value =
                                    StatData(
                                        category = selectedCategory,
                                        distribution = distribution,
                                        insights = generateRamInsights(stats.ramDistribution.map { it.ram to it.count }),
                                    )
                            }
                        }

                        StatCategory.PROCESSORS -> {
                            deviceRepository.getProcessorDistribution().collect { processors ->
                                val distribution = processors.associate { it.processor to it.count }
                                value =
                                    StatData(
                                        category = selectedCategory,
                                        distribution = distribution,
                                        insights = generateProcessorInsights(processors.map { it.processor to it.count }),
                                    )
                            }
                        }

                        StatCategory.FORM_FACTORS -> {
                            deviceRepository.getDeviceStats().collect { stats ->
                                val distribution =
                                    stats.formFactorBreakdown
                                        .associate { it.formFactor.value to it.count }
                                value =
                                    StatData(
                                        category = selectedCategory,
                                        distribution = distribution,
                                        insights =
                                            generateFormFactorInsights(
                                                stats.formFactorBreakdown.map { it.formFactor.value to it.count },
                                            ),
                                    )
                            }
                        }

                        StatCategory.MANUFACTURERS -> {
                            deviceRepository.getDeviceStats().collect { stats ->
                                val distribution =
                                    stats.topManufacturers
                                        .associate { it.manufacturer to it.count }
                                value =
                                    StatData(
                                        category = selectedCategory,
                                        distribution = distribution,
                                        insights =
                                            generateManufacturerInsights(
                                                stats.topManufacturers.map { it.manufacturer to it.count },
                                            ),
                                    )
                            }
                        }

                        StatCategory.SDK_VERSIONS -> {
                            deviceRepository.getDeviceStats().collect { stats ->
                                val distribution =
                                    stats.sdkVersionDistribution
                                        .take(15)
                                        .associate { "API ${it.sdkVersion}" to it.count }
                                value =
                                    StatData(
                                        category = selectedCategory,
                                        distribution = distribution,
                                        insights =
                                            generateSdkInsights(
                                                stats.sdkVersionDistribution.map { it.sdkVersion to it.count },
                                            ),
                                    )
                            }
                        }

                        StatCategory.OPENGL -> {
                            deviceRepository.getOpenGlDistribution().collect { openGl ->
                                val distribution = openGl.associate { it.version to it.count }
                                value =
                                    StatData(
                                        category = selectedCategory,
                                        distribution = distribution,
                                        insights = generateOpenGlInsights(openGl.map { it.version to it.count }),
                                    )
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error loading stats for category: $selectedCategory")
                }
            }

            return StatsExplorerScreen.State(
                selectedCategory = selectedCategory,
                statData = statData,
                isLoading = statData == null,
            ) { event ->
                when (event) {
                    is StatsExplorerScreen.Event.SelectCategory -> {
                        Timber.d("Category selected: ${event.category}")
                        selectedCategory = event.category
                    }

                    is StatsExplorerScreen.Event.Refresh -> {
                        Timber.d("Refresh requested")
                    }
                }
            }
        }

        private fun generateRamInsights(distribution: List<Pair<String, Int>>): List<String> {
            val insights = mutableListOf<String>()
            if (distribution.isNotEmpty()) {
                val mostCommon = distribution.maxByOrNull { it.second }
                mostCommon?.let {
                    insights.add("Most common RAM: ${it.first} (${it.second} devices)")
                }
                val total = distribution.sumOf { it.second }
                insights.add("Total configurations: ${distribution.size}")
                insights.add("Total devices: $total")
            }
            return insights
        }

        private fun generateProcessorInsights(distribution: List<Pair<String, Int>>): List<String> {
            val insights = mutableListOf<String>()
            if (distribution.isNotEmpty()) {
                val topProcessor = distribution.maxByOrNull { it.second }
                topProcessor?.let {
                    insights.add("Most used processor: ${it.first.take(30)}")
                }
                insights.add("Unique processors: ${distribution.size}")
            }
            return insights
        }

        private fun generateFormFactorInsights(distribution: List<Pair<String, Int>>): List<String> {
            val insights = mutableListOf<String>()
            if (distribution.isNotEmpty()) {
                val phoneCount =
                    distribution
                        .find {
                            it.first.contains("phone", ignoreCase = true)
                        }?.second ?: 0
                val total = distribution.sumOf { it.second }
                if (total > 0 && phoneCount > 0) {
                    val percentage = (phoneCount.toFloat() / total * 100).toInt()
                    insights.add("Phones make up $percentage% of devices")
                }
                insights.add("${distribution.size} form factor types")
            }
            return insights
        }

        private fun generateManufacturerInsights(distribution: List<Pair<String, Int>>): List<String> {
            val insights = mutableListOf<String>()
            if (distribution.isNotEmpty()) {
                val topManufacturer = distribution.maxByOrNull { it.second }
                topManufacturer?.let {
                    insights.add("Top manufacturer: ${it.first} (${it.second} devices)")
                }
                val total = distribution.sumOf { it.second }
                val top3 = distribution.take(3).sumOf { it.second }
                if (total > 0) {
                    val percentage = (top3.toFloat() / total * 100).toInt()
                    insights.add("Top 3 make up $percentage% of market")
                }
            }
            return insights
        }

        private fun generateSdkInsights(distribution: List<Pair<Int, Int>>): List<String> {
            val insights = mutableListOf<String>()
            if (distribution.isNotEmpty()) {
                val maxSdk = distribution.maxByOrNull { it.first }
                maxSdk?.let {
                    insights.add("Latest supported: API ${it.first}")
                }
                val minSdk = distribution.minByOrNull { it.first }
                minSdk?.let {
                    insights.add("Oldest supported: API ${it.first}")
                }
                insights.add("SDK range: ${distribution.size} versions")
            }
            return insights
        }

        private fun generateOpenGlInsights(distribution: List<Pair<String, Int>>): List<String> {
            val insights = mutableListOf<String>()
            if (distribution.isNotEmpty()) {
                val mostCommon = distribution.maxByOrNull { it.second }
                mostCommon?.let {
                    insights.add("Most common: OpenGL ${it.first}")
                }
                insights.add("${distribution.size} OpenGL versions supported")
            }
            return insights
        }

        @CircuitInject(StatsExplorerScreen::class, AppScope::class)
        @AssistedFactory
        interface Factory {
            fun create(
                screen: StatsExplorerScreen,
                navigator: Navigator,
            ): StatsExplorerPresenter
        }
    }
