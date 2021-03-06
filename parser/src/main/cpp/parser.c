#include "cooklang-c/include/CooklangParser.h"
#include "cooklang-c/parserFiles/Cooklang.tab.h"

#include "parser.h"


JNIEXPORT jobject JNICALL
Java_org_cooklang_parser_Parser_parseRecipe(JNIEnv *env, jclass clazz, jstring content) {
  jclass    recipeClass = (*env)->FindClass(env, "org/cooklang/parser/Recipe");
  jmethodID recipeConstructor = (*env)->GetMethodID(env, recipeClass, "<init>", "()V");;

  jclass    stepClass = (*env)->FindClass(env, "org/cooklang/parser/Step");
  jmethodID stepConstructor = (*env)->GetMethodID(env, stepClass, "<init>", "()V");

  jclass    textItemClass = (*env)->FindClass(env, "org/cooklang/parser/TextItem");
  jmethodID textItemConstructor = (*env)->GetMethodID(env, textItemClass, "<init>", "()V");

  jclass    ingredientClass = (*env)->FindClass(env, "org/cooklang/parser/Ingredient");
  jmethodID ingredientConstructor = (*env)->GetMethodID(env, ingredientClass, "<init>", "()V");

  jclass    cookwareClass = (*env)->FindClass(env, "org/cooklang/parser/Cookware");
  jmethodID cookwareConstructor = (*env)->GetMethodID(env, cookwareClass, "<init>", "()V");

  jclass    timerClass = (*env)->FindClass(env, "org/cooklang/parser/Timer");
  jmethodID timerConstructor = (*env)->GetMethodID(env, timerClass, "<init>", "()V");

  jmethodID addStep = (*env)->GetMethodID(env, recipeClass, "addStep", "(Lorg/cooklang/parser/Step;)V");
  jmethodID addMetadata = (*env)->GetMethodID(env, recipeClass, "addMetadata", "(Ljava/lang/String;Ljava/lang/String;)V");

  jmethodID addTextItem = (*env)->GetMethodID(env, stepClass, "addTextItem", "(Lorg/cooklang/parser/TextItem;)V");
  jmethodID addIngredient = (*env)->GetMethodID(env, stepClass, "addIngredient", "(Lorg/cooklang/parser/Ingredient;)V");
  jmethodID addTimer = (*env)->GetMethodID(env, stepClass, "addTimer", "(Lorg/cooklang/parser/Timer;)V");
  jmethodID addCookware = (*env)->GetMethodID(env, stepClass, "addCookware", "(Lorg/cooklang/parser/Cookware;)V");

  jmethodID setTextItemValue = (*env)->GetMethodID(env, textItemClass, "setValue", "(Ljava/lang/String;)V");

  jmethodID setIngredientName = (*env)->GetMethodID(env, ingredientClass, "setName", "(Ljava/lang/String;)V");
  jmethodID setIngredientQuantityString = (*env)->GetMethodID(env, ingredientClass, "setQuantityString", "(Ljava/lang/String;)V");
  jmethodID setIngredientQuantityFloat = (*env)->GetMethodID(env, ingredientClass, "setQuantityFloat", "(Ljava/lang/Float;)V");
  jmethodID setIngredientUnits = (*env)->GetMethodID(env, ingredientClass, "setUnits", "(Ljava/lang/String;)V");

  jmethodID setTimerName = (*env)->GetMethodID(env, timerClass, "setName", "(Ljava/lang/String;)V");
  jmethodID setTimerQuantityString = (*env)->GetMethodID(env, timerClass, "setQuantityString", "(Ljava/lang/String;)V");
  jmethodID setTimerQuantityFloat = (*env)->GetMethodID(env, timerClass, "setQuantityFloat", "(Ljava/lang/Float;)V");
  jmethodID setTimerUnits = (*env)->GetMethodID(env, timerClass, "setUnits", "(Ljava/lang/String;)V");

  jmethodID setCookwareName = (*env)->GetMethodID(env, cookwareClass, "setName", "(Ljava/lang/String;)V");
  jmethodID setCookwareQuantityString = (*env)->GetMethodID(env, cookwareClass, "setQuantityString", "(Ljava/lang/String;)V");
  jmethodID setCookwareQuantityFloat = (*env)->GetMethodID(env, cookwareClass, "setQuantityFloat", "(Ljava/lang/Float;)V");

  jobject step;
  jobject textItem;
  jobject ingredient;
  jobject timer;
  jobject cookware;
  jstring _string;
  jstring _string2;
  jfloat _float;

  jobject recipe = (*env)->NewObject(env, recipeClass, recipeConstructor);

  char *recipeString = (*env)->GetStringUTFChars(env, content, NULL);

  // TODO errors handling
  Recipe *parsedRecipe = parseRecipeString(recipeString);

  ListIterator stepIterator;
  Step *currentStep;

  ListIterator directionIterator;
  Direction *currentDirection;

  ListIterator metaDataIterator;
  Metadata *currentMetaData;

  metaDataIterator = createIterator(parsedRecipe->metaData);
  currentMetaData = nextElement(&metaDataIterator);

  while (currentMetaData != NULL) {
    _string=(*env)->NewStringUTF(env, currentMetaData->identifier);
    _string2=(*env)->NewStringUTF(env, currentMetaData->content);
    (*env)->CallVoidMethod(env, recipe, addMetadata, _string, _string2);

    // get the next one
    currentMetaData = nextElement(&metaDataIterator);
  }

  stepIterator = createIterator(parsedRecipe->stepList);
  currentStep = nextElement(&stepIterator);

  int directionsTotal;

  while (currentStep != NULL) {
    directionsTotal = getLength(currentStep->directions);
    if (directionsTotal > 0) {
      // loop through every direction
      directionIterator = createIterator(currentStep->directions);
      currentDirection = nextElement(&directionIterator);
      step = (*env)->NewObject(env, stepClass, stepConstructor);

      while (currentDirection != NULL) {

        // TEXT
        if (strcmp(currentDirection->type, "text") == 0) {
          textItem = (*env)->NewObject(env, textItemClass, textItemConstructor);

          _string=(*env)->NewStringUTF(env, currentDirection->value);
          (*env)->CallVoidMethod(env, textItem, setTextItemValue, _string);

          (*env)->CallVoidMethod(env, step, addTextItem, textItem);

        // INGREDIENT
        } else if (strcmp(currentDirection->type, "ingredient") == 0) {
          ingredient = (*env)->NewObject(env, ingredientClass, ingredientConstructor);

          // name
          _string=(*env)->NewStringUTF(env, currentDirection->value);
          (*env)->CallVoidMethod(env, ingredient, setIngredientName, _string);

          // quantity
          if (currentDirection->quantityString == NULL) {
            if (currentDirection->quantity != -1) {
              (*env)->CallVoidMethod(env, ingredient, setIngredientQuantityFloat, currentDirection->quantity);
            } else {
              _string=(*env)->NewStringUTF(env, "");
              (*env)->CallVoidMethod(env, ingredient, setIngredientQuantityString, _string);
            }
          } else {
            _string=(*env)->NewStringUTF(env, currentDirection->quantityString);
            (*env)->CallVoidMethod(env, ingredient, setIngredientQuantityString, _string);
          }

          // units
          if (currentDirection->unit == NULL) {
            _string=(*env)->NewStringUTF(env, "");
          } else {
            _string=(*env)->NewStringUTF(env, currentDirection->unit);
          }
          (*env)->CallVoidMethod(env, ingredient, setIngredientUnits, _string);

          (*env)->CallVoidMethod(env, step, addIngredient, ingredient);

        // TIMER
        } else if (strcmp(currentDirection->type, "timer") == 0) {
          timer = (*env)->NewObject(env, timerClass, timerConstructor);

          // name
          if (currentDirection->value != NULL) {
            _string=(*env)->NewStringUTF(env, currentDirection->value);
            (*env)->CallVoidMethod(env, timer, setTimerName, _string);
          }

          // quantity
          if (currentDirection->quantityString == NULL) {
            if (currentDirection->quantity != -1) {
              (*env)->CallVoidMethod(env, timer, setTimerQuantityFloat, currentDirection->quantity);
            } else {
              _string=(*env)->NewStringUTF(env, "");
              (*env)->CallVoidMethod(env, timer, setTimerQuantityString, _string);
            }
          } else {
            _string=(*env)->NewStringUTF(env, currentDirection->quantityString);
            (*env)->CallVoidMethod(env, timer, setTimerQuantityString, _string);
          }

          // units
          if (currentDirection->unit == NULL) {
            _string=(*env)->NewStringUTF(env, "");
          } else {
            _string=(*env)->NewStringUTF(env, currentDirection->unit);
          }
          (*env)->CallVoidMethod(env, timer, setTimerUnits, _string);

          (*env)->CallVoidMethod(env, step, addTimer, timer);

        // COOKWARE
        } else if (strcmp(currentDirection->type, "cookware") == 0) {
          cookware = (*env)->NewObject(env, cookwareClass, cookwareConstructor);

          // name
          _string=(*env)->NewStringUTF(env, currentDirection->value);
          (*env)->CallVoidMethod(env, cookware, setCookwareName, _string);

          // quantity
          if (currentDirection->quantityString == NULL) {
            if (currentDirection->quantity != -1) {
              (*env)->CallVoidMethod(env, cookware, setCookwareQuantityFloat, currentDirection->quantity);
            } else {
              _string=(*env)->NewStringUTF(env, "");
              (*env)->CallVoidMethod(env, cookware, setCookwareQuantityString, _string);
            }
          } else {
            _string=(*env)->NewStringUTF(env, currentDirection->quantityString);
            (*env)->CallVoidMethod(env, cookware, setCookwareQuantityString, _string);
          }

          (*env)->CallVoidMethod(env, step, addCookware, cookware);

        // UH, OH...
        } else {
          printf("Unsupported type");
        }

        currentDirection = nextElement(&directionIterator);
      }

      (*env)->CallVoidMethod(env, recipe, addStep, step);
    }

    currentStep = nextElement(&stepIterator);
  }

  return recipe;
}
