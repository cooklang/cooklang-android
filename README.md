# How to run it

Clone submodules

	git submodule update --init --recursive


### Build files

Install dependencies with nix or manually (java, gcc).

```
# Enter nix-shell env with all deps preinstalled
nix-shell

#To regenerate header and class file (required after updating java file)
javac -h . org/cooklang/*.java

# Compile C sources
gcc -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin \
	org_cooklang_Parser.c \
	cooklang-c/src/CooklangParser.c \
	cooklang-c/src/LinkedListLib.c \
	cooklang-c/src/CooklangRecipe.c \
	cooklang-c/parserFiles/Cooklang.tab.c

# Make dynamic library
gcc -dynamiclib -o libcooklang.dylib \
	org_cooklang_Parser.o \
	Cooklang.tab.o \
	CooklangParser.o \
	CooklangRecipe.o \
	LinkedListLib.o -lc

```

# Run test

	java -Djava.library.path=$PWD Test.java

