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
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cooklang.sample_app.ui.theme.CooklangSampleAppTheme
import uniffi.cooklang_bindings.CooklangRecipe
import uniffi.cooklang_bindings.parse


private val testRecipe = ">> source: https://jamieoliver.com" + "\n" +
        "Slice @bacon{1%kg} and things" + "\n" +
        "Add @bacon{2%kg} to @eggs"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var rawRecipeText by remember { mutableStateOf(testRecipe) }

//            var parsedRecipeString by remember { mutableStateOf<String>("") }
            var recipe by remember { mutableStateOf<CooklangRecipe?>(null)}
//            System.loadLibrary("cooklang")

            CooklangSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .verticalScroll(state = ScrollState(0))
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    TextField(
                        label = { Text(text = "Raw recipe") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        value = rawRecipeText,
                        onValueChange = {
                            rawRecipeText = it
                        })
                    Button(onClick = {
                        recipe = parse(rawRecipeText)
                    }) {
                        Text(text = "Parse!")
                    }
                    recipe?.let {
                        Text(text = "${recipe!!.toString()}: ")
//                        it.ingredients.map {
//                            Text(text = "${it.name} ${it.quantityFloat ?: it.quantityString}${it.units}")
//                        }
//                        Spacer(modifier = Modifier.size(8.dp))
//                        Text(text = "Instructions: ")
//                        Text(text = it.getDisplayString())
                    }

                }
            }
        }
    }
}