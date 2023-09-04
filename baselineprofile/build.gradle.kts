import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
  id("com.android.test")
  id("org.jetbrains.kotlin.android")
  id("androidx.baselineprofile")
}

android {
  namespace = "com.example.baselineprofile"
  compileSdk = 34

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlinOptions {
    jvmTarget = "17"
  }

  kotlin{
    jvmToolchain(17)
  }

  defaultConfig {
    minSdk = 28
    targetSdk = 34

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":app"
  experimentalProperties["android.experimental.self-instrumenting"] = true

  flavorDimensions += listOf("codeLoading")
  productFlavors {
    create("reflect") { dimension = "codeLoading" }
    create("serviceLoader") { dimension = "codeLoading" }
    create("dagger") { dimension = "codeLoading" }
  }
  buildFeatures {
    buildConfig = true
  }

  buildTypes {
    // This benchmark buildType is used for benchmarking, and should function like your
    // release build (for example, with minification on). It's signed with a debug key
    // for easy local/CI testing.
    create("benchmark") {
      // Keep the build type debuggable so we can attach a debugger if needed.
      isDebuggable = true
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks.add("release")
    }
  }

  testOptions.managedDevices.devices {
    create<ManagedVirtualDevice>("pixel3aApi33") {
      device = "Pixel 3a"
      apiLevel = 33
      systemImageSource = "google"
    }
  }
}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
  managedDevices += "pixel3aApi33"
  useConnectedDevices = false
}

dependencies {
  implementation("androidx.test.ext:junit:1.1.5")
  implementation("androidx.test.espresso:espresso-core:3.5.1")
  implementation("androidx.test.uiautomator:uiautomator:2.2.0")
  implementation("androidx.benchmark:benchmark-macro-junit4:1.2.0-beta05")
}

androidComponents {
  beforeVariants {
    it.enable = it.buildType == "benchmark"
  }
}
