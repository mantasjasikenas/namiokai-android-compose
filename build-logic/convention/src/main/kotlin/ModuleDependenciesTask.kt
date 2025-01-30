@file:Suppress("unused") // Used in build-logic but IDE doesn't see it

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.TaskAction

abstract class ModuleDependenciesTask : DefaultTask() {
    init {
        description = "Prints module dependencies"
    }

    @TaskAction
    fun printModuleDependencies() {
        val dependenciesGraph = mutableMapOf<String, MutableSet<String>>()

        project.subprojects.forEach cycle@{ sourceProject ->
            val targetProjects = mutableSetOf<String>()

            if (!sourceProject.buildFile.exists()) {
                return@cycle
            }

            sourceProject.configurations.forEach { config ->
                config.dependencies.withType(ProjectDependency::class.java)
                    .map { it.dependencyProject }
                    .forEach { targetProject ->
                        if (targetProject != sourceProject) {
                            targetProjects.add(targetProject.name)
                        }
                    }
            }

            dependenciesGraph[sourceProject.name] = targetProjects
        }

        // Print the graph
        dependenciesGraph.forEach { (source, targets) ->
            println(source)
            targets.forEach { target ->
                println("\t-> $target")
            }

            if (targets.isEmpty()) {
                println("\t-> No module dependencies")
            }
            println()
        }


        /*val svg = SVG.svg(true) {
            height = "300"
            width = "300"
            style {
                body = """

                 svg .black-stroke { stroke: red; stroke-width: 6; }
                 svg .fur-color { fill: white; }

             """.trimIndent()
            }
            rect {
                x = "0"
                y = "0"
                width = "25"
                height = "100"
                fill = "#473b49"
                text {
                    x = "40"
                    y = "50"
                    body = "app"
                    fontFamily = "monospace"
                    fontSize = "40px"
                    fill = "white"
                    cssClass = "black-stroke"
                }
            }
            rect {
                x = "0"
                y = "125"
                width = "25"
                height = "100"
                fill = "#473b49"
            }
        }

        FileWriter("./dependencies.svg").use {
            svg.render(
                it,
                RenderMode.FILE
            )
        }*/
    }
}