package org.cooklang.parser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.textfield.TextInputEditText
import org.cooklang.parser.ui.theme.CooklangParserTheme
import uniffi.cooklang.parse
import uniffi.cooklang.CooklangRecipe



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

            CooklangParserTheme {
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