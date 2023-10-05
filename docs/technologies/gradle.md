# Introduction to Gradle
## Overview
Gradle is a build automation tool.
It can build the project, test the resolution, generate the javadoc and run the
project.
To do that, gradle creates a task tree, so only necessary tasks are executed.

## Most important Concepts / Tools
### Tasks:
It is also possible to create tasks by yourself.
To do that, one possibility is, that you go into the build.gradle file
and type there 
##### tasks.register([Name] (, [default operation])) { Do some stuff here }.
Tasks can be given a description and group, so that they are grouped in the task 
overview and also have a description there.

### Commands:
There are multiple Commands you can use with Gradle.
#### Frequently used are listed here:

##### tasks:
Lists all commands you can use with in this project.
##### test:
Executes all tests and shows the result.
##### clean:
Removes all previous build outputs
##### build:
Builds the project

It is possible to chain commands, so f.e.
clean build . That causes gradle to first, remove all previous output files and 
create new ones (from scratch).

### Parameters:
They can also set by default in the gradle config (gradle.properties).

--parallel: Causes the gradle process to execute the tasks (if possible) 
parallel

--build-cache: Saves some data from previous output to improve the build speed

--configuration-cache: Saves the configuration as caches

--daemon: enables the daemon process, which can reduce the build time

--no-daemon: disables the daemon

## Resources (Links)
Tasks:
https://docs.gradle.org/current/userguide/part2_gradle_tasks.html
https://docs.gradle.org/current/userguide/tutorial_using_tasks.html

Parameters:
https://docs.gradle.org/current/userguide/performance.html




