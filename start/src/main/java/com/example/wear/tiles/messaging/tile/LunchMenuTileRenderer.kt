/*
 * Copyright 2022 The Android Open Source Project
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
@file:OptIn(ExperimentalHorologistApi::class)

package com.example.wear.tiles.messaging.tile

import android.content.Context
import android.util.Log
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import java.time.DayOfWeek
import java.time.LocalDate

class LunchMenuTileRenderer(context: Context) :
    SingleTileLayoutRenderer<LunchMenuTileState, Unit>(context) {

    companion object {
        private const val TAG = "LunchMenuTileRenderer"
    }

    override fun renderTile(
        state: LunchMenuTileState,
        deviceParameters: DeviceParametersBuilders.DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        Log.d(TAG, "renderTile: Rendering tile with state: $state")
        return lunchMenuTileLayout(
            context = context,
            deviceParameters = deviceParameters,
            state = state
        ).also {
            Log.d(TAG, "renderTile: Tile rendering completed")
        }
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceState: Unit,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: List<String>
    ) {
        Log.d(TAG, "produceRequestedResources: Called with resourceIds: $resourceIds")
        // No additional resources needed for the lunch menu tile
        Log.d(TAG, "produceRequestedResources: No resources produced")
    }

    // Remove the produceRequestedResources method as it's no longer needed
}

fun getCurrentWeekday(): String {
    val dayOfWeek: DayOfWeek = LocalDate.now().dayOfWeek
    Log.d("LunchMenuTile", "getCurrentWeekday: Current day is $dayOfWeek")
    return dayOfWeek.toString()
}

private fun lunchMenuTileLayout(
    context: Context,
    deviceParameters: DeviceParametersBuilders.DeviceParameters,
    state: LunchMenuTileState
): LayoutElementBuilders.LayoutElement {
    Log.d("LunchMenuTile", "lunchMenuTileLayout: Starting layout creation for state: $state")
    
    return PrimaryLayout.Builder(deviceParameters)
        .setPrimaryLabelTextContent(
            Text.Builder(context, getCurrentWeekday())
                .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                .setColor(argb(MessagingTileTheme.colors.primary))
                .build()
                .also { Log.d("LunchMenuTile", "lunchMenuTileLayout: Set primary label to ${getCurrentWeekday()}") }
        )
        .setContent(
            createLayout(state, context)
                .also { Log.d("LunchMenuTile", "lunchMenuTileLayout: Content layout created") }
        )
        .setPrimaryChipContent(
            CompactChip.Builder(
                context,
                "See more",
                launchActivityClickable("new_button", openNewConversation()),
                deviceParameters
            )
                .setChipColors(ChipColors.primaryChipColors(MessagingTileTheme.colors))
                .build()
                .also { Log.d("LunchMenuTile", "lunchMenuTileLayout: Primary chip content set") }
        )
        .build()
        .also { Log.d("LunchMenuTile", "lunchMenuTileLayout: Layout creation completed") }
}

private fun createHeadingComponent(context: Context, str: String): Text {
    return Text.Builder(
        context,
        str
    )
        .setTypography(Typography.TYPOGRAPHY_BODY1)
        .setMaxLines(1)
        .setColor(argb(MessagingTileTheme.colors.primary))
        .build()
}

private fun createTextComponent(context: Context, str: String): Text {
    return Text.Builder(
        context,
        str
    )
        .setTypography(Typography.TYPOGRAPHY_BODY2)
        .setMaxLines(1)
        .setColor(argb(MessagingTileTheme.colors.primary))
        .build()
}

private fun createLayout(
    state: LunchMenuTileState,
    context: Context
): LayoutElementBuilders.LayoutElement {
    Log.d("LunchMenuTile", "createLayout: Creating layout for menu items: ${state.todayMenu}")
    return LayoutElementBuilders.Column.Builder().apply {
        state.todayMenu.forEach { menuItem ->
            Log.d("LunchMenuTile", "createLayout: Adding menu item: $menuItem")
            addContent(createHeadingComponent(context, menuItem.name))
            menuItem.description?.let { description ->
                addContent(createTextComponent(context, description))
            }
            addContent(createTextComponent(context, "€%.2f".format(menuItem.price)))
        }
    }.build().also {
        Log.d("LunchMenuTile", "createLayout: Layout creation completed")
    }
}