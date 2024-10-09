/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.wear.tiles.messaging.tile

import androidx.lifecycle.lifecycleScope
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.wear.tiles.lunch.LunchMenuRepo
import com.example.wear.tiles.lunch.MenuItem
import android.util.Log

@OptIn(ExperimentalHorologistApi::class)
class LunchMenuTileService : SuspendingTileService() {

    private lateinit var repo: LunchMenuRepo
    private lateinit var renderer: LunchMenuTileRenderer
    
    private lateinit var tileStateFlow: StateFlow<LunchMenuTileState?>


    override fun onCreate() {
        Log.d("MessagingTileService", "onCreate: Initializing service")
        super.onCreate()
        repo = LunchMenuRepo(this)
        renderer = LunchMenuTileRenderer(this)
        tileStateFlow = repo.getLunchMenus()
            .map { menus -> LunchMenuTileState(getTodayMenu(menus)) }
            .stateIn(
                lifecycleScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        Log.d("MessagingTileService", "tileRequest: Processing tile request")
        val tileState = latestTileState()
        return renderer.renderTimeline(tileState, requestParams)
    }

    private suspend fun latestTileState(): LunchMenuTileState {
        Log.d("MessagingTileService", "latestTileState: Fetching latest tile state")
        var tileState = tileStateFlow.filterNotNull().first()

        if (tileState.todayMenu.isEmpty()) {
            Log.d("MessagingTileService", "latestTileState: Today's menu is empty, refreshing data")
            refreshData()
            tileState = tileStateFlow.filterNotNull().first()
        }
        return tileState
    }

    private suspend fun refreshData() {
        Log.d("MessagingTileService", "refreshData: Updating lunch menus with sample data")
        repo.updateLunchMenus(LunchMenuRepo.sampleLunchMenus)
    }

    private fun getTodayMenu(menus: Map<String, List<MenuItem>>): List<MenuItem> {
        Log.d("MessagingTileService", "getTodayMenu: Fetching menu for today")
        val today = java.time.LocalDate.now().dayOfWeek.name.lowercase()
        return menus[today] ?: emptyList()
    }


    override suspend fun resourcesRequest(requestParams: ResourcesRequest): ResourceBuilders.Resources {
        Log.d("MessagingTileService", "resourcesRequest: Processing resources request")
        // We don't need a specific resource state for the lunch menu, so we pass Unit
        return renderer.produceRequestedResources(
            Unit,requestParams
        ).also {
            Log.d("LunchMenuTileService", "resourcesRequest: Resources produced")
        }
    }
}
