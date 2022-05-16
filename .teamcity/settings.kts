import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.CustomChart
import jetbrains.buildServer.configs.kotlin.CustomChart.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.freeDiskSpace
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.ideaRunner
import jetbrains.buildServer.configs.kotlin.projectCustomChart
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {

    buildType(Build)

    features {
        projectCustomChart {
            id = "PROJECT_EXT_121"
            title = "New chart title"
            seriesTitle = "Serie"
            format = CustomChart.Format.DURATION
            series = listOf(
                Serie(title = "Queue wait reason: All compatible agents are outdated  waiting for upgrade", key = SeriesKey("queueWaitReason:All_compatible_agents_are_outdated__waiting_for_upgrade"), sourceBuildTypeId = "SecRepos3_Build"),
                Serie(title = "Queue wait reason: Build queue was paused", key = SeriesKey("queueWaitReason:Build_queue_was_paused"), sourceBuildTypeId = "SecRepos3_Build"),
                Serie(title = "Queue wait reason: Build settings have not been finalized", key = SeriesKey("queueWaitReason:Build_settings_have_not_been_finalized"), sourceBuildTypeId = "SecRepos3_Build"),
                Serie(title = "Queue wait reason: Waiting for the build queue distribution process", key = SeriesKey("queueWaitReason:Waiting_for_the_build_queue_distribution_process"), sourceBuildTypeId = "SecRepos3_Build")
            )
        }
    }
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        script {
            executionMode = BuildStep.ExecutionMode.RUN_ON_SUCCESS
            scriptContent = "echo test"
        }
        ideaRunner {
            enabled = false
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            pathToProject = ""
            jdk {
                name = "16"
                path = "%env.JDK_11_0%"
                patterns("jre/lib/*.jar", "jre/lib/ext/jfxrt.jar")
                extAnnotationPatterns("%teamcity.tool.idea%/lib/jdkAnnotations.jar")
            }
            pathvars {
                variable("MAVEN_REPOSITORY", "")
            }
            jvmArgs = "-Xmx256m"
            targetJdkHome = "%env.JDK_11%"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        freeDiskSpace {
            requiredSpace = "4gb"
            failBuild = false
        }
        perfmon {
        }
    }

    requirements {
        equals("teamcity.agent.name", "Agent_132_209_6", "RQ_9")
    }
    
    disableSettings("RQ_9")
})
