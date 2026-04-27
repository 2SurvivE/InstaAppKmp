plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("com.tencent.kuiklybase.knoi.plugin")
}

kotlin {
    ohosArm64 {
        binaries {
            sharedLib {
                baseName = "kn"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.tencent.kuiklybase:knoi:0.0.4")
            }
        }

        val ohosArm64Main by getting
    }
}

knoi {
    tsGenDir = projectDir.absolutePath + "/ts-api/"
}