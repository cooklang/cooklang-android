package org.cooklang.sample_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cooklang.sample_app.ui.theme.CooklangSampleAppTheme
import uniffi.cooklang_bindings.AisleConf
import uniffi.cooklang_bindings.CooklangRecipe
import uniffi.cooklang_bindings.combineIngredientLists
import uniffi.cooklang_bindings.parseAisleConfig
import uniffi.cooklang_bindings.parseRecipe


private val testRecipeOne = """
     >> source: https://jamieoliver.com
     Slice @bacon{1%kg} and things
     Add @bacon{2%kg} to @eggs
""".trimIndent()


private val testRecipeTwo = """
     >> source: https://jamieoliver.com
     Slice @bacon{1%kg} and things
     Add @bacon{2%kg} to @eggs
""".trimIndent()


private val testAisle = """
    [fruit and veg]
    apple gala | apples
    aubergine
    avocado | avocados

    [milk and dairy]
    butter
    egg | eggs
    curd cheese
    cheddar cheese
    feta
""".trimIndent()

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var rawRecipeText by remember { mutableStateOf(testRecipeOne) }
            var rawRecipeTextTwo by remember { mutableStateOf(testRecipeTwo) }
            var rawAisleConfText by remember { mutableStateOf(testAisle) }

            var recipeOne by remember { mutableStateOf<CooklangRecipe?>(null)}
            var recipeTwo by remember { mutableStateOf<CooklangRecipe?>(null)}
            var aisleConf by remember { mutableStateOf<AisleConf?>(null)}

            CooklangSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .verticalScroll(state = ScrollState(0))
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    TextField(
                        label = { Text(text = "Raw recipe 1") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        value = rawRecipeText,
                        onValueChange = {
                            rawRecipeText = it
                        })

                    TextField(
                        label = { Text(text = "Raw recipe 2") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        value = rawRecipeTextTwo,
                        onValueChange = {
                            rawRecipeTextTwo = it
                        })

                    TextField(
                        label = { Text(text = "Raw aisle conf") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 7,
                        value = rawAisleConfText,
                        onValueChange = {
                            rawAisleConfText = it
                        })

                    Button(onClick = {
                        recipeOne = parseRecipe(rawRecipeText)
                        recipeTwo = parseRecipe(rawRecipeTextTwo)
                        aisleConf = parseAisleConfig(rawAisleConfText)
                    }) {
                        Text(text = "Parse!")
                    }

                    Divider()

                    if (recipeOne !== null && recipeTwo !== null && aisleConf !== null) {
                        Text(text = "First step of Recipe 1: ${recipeOne!!.steps[0].toString()}: ")

                        Divider()

                        Text(text = "First step of Recipe 2: ${recipeTwo!!.steps[0].toString()}: ")

                        Divider()

                        Text(text = "Category for avocado: ${aisleConf!!.categoryFor("avocado")} ")

                        Divider()

                        //Text(text = "Merged ingredients from Recipe 1 and Recipe 2: ${combineIngredientLists(listOf(recipeOne!!.ingredients, recipeTwo!!.ingredients))} ")
                    }

                }
            }
        }
    }
}