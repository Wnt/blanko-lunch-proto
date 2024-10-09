package com.example.wear.tiles.lunch

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lunch_menus")

class LunchMenuRepo(private val context: Context) {
    private val gson = Gson()

    fun getLunchMenus(): Flow<Map<String, List<MenuItem>>> = context.dataStore.data.map { preferences ->
        val menuJson = preferences[stringPreferencesKey("lunch_menus")] ?: "{}"
        gson.fromJson(menuJson, object : TypeToken<Map<String, List<MenuItem>>>() {}.type)
    }

    suspend fun updateLunchMenus(menus: Map<String, List<MenuItem>>) {
        context.dataStore.edit {
            it[stringPreferencesKey("lunch_menus")] = gson.toJson(menus)
        }
    }

    companion object {
        val sampleLunchMenus = mapOf(
            "monday" to listOf(
                MenuItem("Savulohiuuniperuna", "ja feta-omenasalaattia", 13.5),
                MenuItem("Kormabroileri thalilautanen", price = 14.5),
                MenuItem("Falafelsalaatti", "harissajugurttia ja omenabulgursalaattia", 14.5),
                MenuItem("Härän fileegrillipihvi", "suppilovahverokastiketta, paistettua yrttiperunaa ja hunajajuureksia", 16.5)
            ),
            "tuesday" to listOf(
                MenuItem("Chicken sandwich", "punakaalia, avokadoa, cheddaria ja loaded frittiperunat", 13.5),
                MenuItem("Karitsaa yrttikermassa ja tagliatellea", "paahdettua paprikaa ja parmesaania", 14.5),
                MenuItem("Paahdettu vuohenjuustosalaatti", "ja cashewpähkinää, kaneli-omenahilloketta", 14.5),
                MenuItem("Paistettua kampelaa", "tillimuusia, katkarapuremouladea ja sitrussalaattia", 15.9)
            ),
            "wednesday" to listOf(
                MenuItem("Kermainen lohikeitto", "ja saaristolaisleipää", 13.5),
                MenuItem("Broileri-appelsiini-pinaattitortellonit", price = 14.5),
                MenuItem("Tattirisotto", "timjamicreme ja punajuurisipsejä", 14.5),
                MenuItem("Härän fileelehtipihvi", "karpalo-chilivoi, kermaperunaa ja manteliparsakaalia", 16.5)
            ),
            "thursday" to listOf(
                MenuItem("Pestobroileri Ceasarsalaatti", "fetaa, marinoitua tomaattia ja krutonkeja", 13.5),
                MenuItem("Tomaattinen tiikerirapu-mozzarellaspagetti", price = 14.5),
                MenuItem("Kevätkääryleet", "thaisalaatti, nuudelia ja papaijaa", 14.5),
                MenuItem("Karitsan fileevarras", "cheddar-jalapenokastike, friteerattua lohkoperunaa ja salaattia", 16.5)
            ),
            "friday" to listOf(
                MenuItem("Katkarapu Skagen", "saaristolaisleipää ja fenkolisalaattia", 13.5),
                MenuItem("Spaghetti Carbonara", price = 14.5),
                MenuItem("Ruskajuusto red curry thalilautanen", price = 14.5),
                MenuItem("Härkä-pekoni-chipotlehampurilainen", "dippiperunaa ja cole slaw", 15.9)
            )
        )
    }
}

data class MenuItem(
    val name: String,
    val description: String? = null,
    val price: Double
)