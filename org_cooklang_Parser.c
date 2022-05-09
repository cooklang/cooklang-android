#include "cooklang-c/include/CooklangParser.h"
#include "cooklang-c/parserFiles/Cooklang.tab.h"

#include "org_cooklang_Parser.h"


JNIEXPORT jobject JNICALL
Java_org_cooklang_Parser_parseRecipe(JNIEnv *env, jclass, jstring content) {
  jclass    recipeClass = (*env)->FindClass(env, "org/cooklang/Recipe");
  jmethodID recipeConstructor = (*env)->GetMethodID(env, recipeClass, "<init>", "()V");;

  jclass    stepClass = (*env)->FindClass(env, "org/cooklang/Step");
  jmethodID stepConstructor = (*env)->GetMethodID(env, stepClass, "<init>", "()V");

  jclass    textItemClass = (*env)->FindClass(env, "org/cooklang/TextItem");
  jmethodID textItemConstructor = (*env)->GetMethodID(env, textItemClass, "<init>", "()V");

  jclass    ingredientClass = (*env)->FindClass(env, "org/cooklang/Ingredient");
  jmethodID ingredientConstructor = (*env)->GetMethodID(env, ingredientClass, "<init>", "()V");

  jclass    equipmentClass = (*env)->FindClass(env, "org/cooklang/Equipment");
  jmethodID equipmentConstructor = (*env)->GetMethodID(env, equipmentClass, "<init>", "()V");

  jclass    timerClass = (*env)->FindClass(env, "org/cooklang/Timer");
  jmethodID timerConstructor = (*env)->GetMethodID(env, timerClass, "<init>", "()V");

  jobject recipe = (*env)->NewObject(env, recipeClass, recipeConstructor);
  jmethodID addStep = (*env)->GetMethodID(env, recipeClass, "addStep", "(Lorg/cooklang/Step;)V");;
  jmethodID addTextItem = (*env)->GetMethodID(env, stepClass, "addTextItem", "(Lorg/cooklang/TextItem;)V");;
  jmethodID addIngredient = (*env)->GetMethodID(env, stepClass, "addIngredient", "(Lorg/cooklang/Ingredient;)V");;

  jobject step;
  jobject textItem;
  jobject ingredient;

  char *recipeString = (*env)->GetStringUTFChars(env, content, NULL);
  // TODO errors handling
  Recipe *parsedRecipe = parseRecipeString(recipeString);

  ListIterator stepIterator;
  Step *currentStep;

  ListIterator directionIterator;
  Direction *currentDirection;

  int directionsTotal;

  stepIterator = createIterator(parsedRecipe->stepList);
  currentStep = nextElement(&stepIterator);

  while (currentStep != NULL) {
    directionsTotal = getLength(currentStep->directions);
    if (directionsTotal > 0) {
      // loop through every direction
      directionIterator = createIterator(currentStep->directions);
      currentDirection = nextElement(&directionIterator);
      step = (*env)->NewObject(env, stepClass, stepConstructor);

      (*env)->CallVoidMethod(env, recipe, addStep, step);

      while (currentDirection != NULL) {
        printf("Direction \"%s\"(%s)\n", currentDirection->value, currentDirection->type);
        if (strcmp(currentDirection->type, "text") == 0) {
          textItem = (*env)->NewObject(env, textItemClass, textItemConstructor);

          (*env)->CallVoidMethod(env, step, addTextItem, textItem);
        } else if (strcmp(currentDirection->type, "ingredient") == 0) {
          ingredient = (*env)->NewObject(env, ingredientClass, ingredientConstructor);

          (*env)->CallVoidMethod(env, step, addIngredient, ingredient);
        } else if (strcmp(currentDirection->type, "timer") == 0) {
          printf("timer");
        } else if (strcmp(currentDirection->type, "equipment") == 0) {
          printf("equipment");
        } else {
          printf("Unsupported type");
        }

        currentDirection = nextElement(&directionIterator);
      }
    }

    currentStep = nextElement(&stepIterator);
  }

  return recipe;
}
