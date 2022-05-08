#include "cooklang-c/include/CooklangParser.h"
#include "cooklang-c/parserFiles/Cooklang.tab.h"

#include "org_cooklang_jni_Parser.h"

#include <unistd.h>

JNIEXPORT jstring JNICALL Java_org_cooklang_jni_Parser_parse
  (JNIEnv * env, jclass, jstring recipe) {
    int dirLength;

    ListIterator stepIter;
    Step * curStep;
    ListIterator dirIter;
    Direction * curDir;

    char * recipeString = ( * env) -> GetStringUTFChars(env, recipe, NULL);

    Recipe * parsedRecipe = parseRecipeString(recipeString);
    stepIter = createIterator(parsedRecipe -> stepList);
    curStep = nextElement( & stepIter);

    while (curStep != NULL) {
      dirLength = getLength(curStep -> directions);
      printf("Step\n");
      if (dirLength > 0) {
        // loop through every direction
        dirIter = createIterator(curStep -> directions);
        curDir = nextElement( & dirIter);

        while (curDir != NULL) {
          printf("Direction \"%s\"(%s)\n", curDir -> value, curDir -> type);
          curDir = nextElement( & dirIter);
        }
      }

      curStep = nextElement( & stepIter);
    }

    char * name = ttyname(STDOUT_FILENO);

    return ( * env) -> NewStringUTF(env, name);
  }
