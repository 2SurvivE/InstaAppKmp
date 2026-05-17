import java.io.File

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("com.tencent.kuiklybase.knoi.plugin")
}

// ===== 新增：统一管理 KNOI 生成的 so / header 名称开始 =====
// baseName = "kn" 时，最终生成：
// 1. libkn.so
// 2. libkn_api.h
//
// 后续如果要改 so 名称，只需要改 knoiSharedLibBaseName。
val knoiSharedLibBaseName = "kn"
val knoiSharedLibFileName = "lib${knoiSharedLibBaseName}.so"
val knoiHeaderFileName = "lib${knoiSharedLibBaseName}_api.h"
// ===== 新增：统一管理 KNOI 生成的 so / header 名称结束 =====

kotlin {
    ohosArm64 {
        binaries {
            sharedLib {
                // ===== 修改：使用统一变量，不再写死 baseName =====
                baseName = knoiSharedLibBaseName
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.tencent.kuiklybase:knoi:0.0.4")
            }
        }

        val ohosArm64Main by getting {
            dependencies {
                // 如果鸿蒙侧有单独依赖，可以放这里
            }
        }
    }
}

knoi {
    tsGenDir = projectDir.absolutePath + "/ts-api/"
}

// ===== 新增：读取 HarmonyOS 工程路径参数开始 =====
// 使用方式：
// ./gradlew :shared:publishDebugOhosToHarmony -PharmonyProjectDir=/path/to/InstaAppHarmony -PharmonyModuleName=entry
val harmonyProjectDirProvider = providers.gradleProperty("harmonyProjectDir")
val harmonyModuleNameProvider = providers.gradleProperty("harmonyModuleName").orElse("entry")

fun requireHarmonyProjectDir(): File {
    val harmonyProjectDir = harmonyProjectDirProvider.orNull

    require(!harmonyProjectDir.isNullOrBlank()) {
        "缺少参数：-PharmonyProjectDir=/path/to/HarmonyProject"
    }

    val harmonyDir = File(harmonyProjectDir)

    require(harmonyDir.exists()) {
        "HarmonyOS 工程目录不存在：${harmonyDir.absolutePath}"
    }

    return harmonyDir
}
// ===== 新增：读取 HarmonyOS 工程路径参数结束 =====

// ===== 新增：注册 Debug / Release 发布任务开始 =====
// 作用：
// 1. 编译 KMP shared 的 ohosArm64 动态库。
// 2. 复制 ${knoiSharedLibFileName} 到 HarmonyOS 工程 entry/libs/arm64-v8a/。
// 3. 复制 ${knoiHeaderFileName} 到 entry/src/main/cpp/include/。
// 4. 复制 ts-api 到 entry/src/main/ets/knoi/generated/。
fun registerPublishOhosTask(
    taskName: String,
    linkTaskName: String,
    nativeOutputDirName: String
) {
    tasks.register(taskName) {
        group = "knoi"

        // ===== 修改：description 使用动态 so 文件名，不再写死 libkn.so =====
        description = "Build $nativeOutputDirName $knoiSharedLibFileName and copy KNOI outputs to HarmonyOS project."

        dependsOn(linkTaskName)

        doLast {
            val harmonyDir = requireHarmonyProjectDir()
            val harmonyModuleName = harmonyModuleNameProvider.get()

            val nativeOutputDir = layout.buildDirectory
                .dir("bin/ohosArm64/$nativeOutputDirName")
                .get()
                .asFile

            // ===== 修改：使用统一变量，不再写死 libkn.so / libkn_api.h =====
            val soFile = File(nativeOutputDir, knoiSharedLibFileName)
            val headerFile = File(nativeOutputDir, knoiHeaderFileName)

            // ===== 修改：错误提示使用动态 so 文件名 =====
            require(soFile.exists()) {
                "未找到 $knoiSharedLibFileName：${soFile.absolutePath}"
            }

            // ===== 新增：复制 so 到 HarmonyOS 工程 =====
            val soTargetDir = File(harmonyDir, "$harmonyModuleName/libs/arm64-v8a")
            soTargetDir.mkdirs()

            copy {
                from(soFile)
                into(soTargetDir)
            }

            // ===== 新增：复制 header 到 HarmonyOS 工程 =====
            if (headerFile.exists()) {
                val headerTargetDir = File(harmonyDir, "$harmonyModuleName/src/main/cpp/include")
                headerTargetDir.mkdirs()

                copy {
                    from(headerFile)
                    into(headerTargetDir)
                }
            }

            // ===== 新增：复制 ts-api 到 HarmonyOS 工程 =====
            val tsApiDir = File(projectDir, "ts-api")
            if (tsApiDir.exists()) {
                val tsApiTargetDir = File(harmonyDir, "$harmonyModuleName/src/main/ets/knoi/generated")

                // ===== 修改：每次复制前清空旧生成物，避免旧接口残留 =====
                if (tsApiTargetDir.exists()) {
                    tsApiTargetDir.deleteRecursively()
                }

                tsApiTargetDir.mkdirs()

                copy {
                    from(tsApiDir)
                    into(tsApiTargetDir)
                }
            }

            println("KNOI $nativeOutputDirName 产物已复制到 HarmonyOS 工程：${harmonyDir.absolutePath}")

            // ===== 修改：日志使用动态 so 文件名，不再写死 libkn.so =====
            println("$knoiSharedLibFileName -> ${soTargetDir.absolutePath}")
        }
    }
}

// ===== 新增：Debug 发布任务 =====
registerPublishOhosTask(
    taskName = "publishDebugOhosToHarmony",
    linkTaskName = "linkDebugSharedOhosArm64",
    nativeOutputDirName = "debugShared"
)

// ===== 新增：Release 发布任务 =====
registerPublishOhosTask(
    taskName = "publishReleaseOhosToHarmony",
    linkTaskName = "linkReleaseSharedOhosArm64",
    nativeOutputDirName = "releaseShared"
)
// ===== 新增：注册 Debug / Release 发布任务结束 =====