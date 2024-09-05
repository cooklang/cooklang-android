# cooklang-android

Generated [Cooklang parser bindings](https://github.com/cooklang/cooklang-rs/tree/main/bindings) packed as Android library. Also in that project there's a sample app which uses Cooklang parser.

## Including into your project

Step 1. Include jitpack.io into your repositories in root `settings.gradle`:

    dependencyResolutionManagement {
        ...
        repositories {
	    maven {
	        url = uri("https://maven.pkg.github.com/cooklang/cooklang-android")
	        credentials {
	            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
	            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
	        }
	    }
	}
    }

Step 2. Add a dependency to relevant `build.gradle` config (change release version if required) :

   dependencies {
	implementation 'com.github.cooklang:parser:0.0.1'
	implementation 'com.github.cooklang:sync:0.0.1'
   }
    
Step 3. Use the parser and sync. See [example](https://github.com/cooklang/cooklang-android/blob/main/app/src/main/java/org/cooklang/sample_app/MainActivity.kt#L22-L76).
