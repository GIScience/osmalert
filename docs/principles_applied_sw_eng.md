
# Principles of Applied Software Engineering 

🍍🍍 PrINciplEs of APPLiEd Software Engineering 🍍🍍



> [!NOTE]  
> This is work in progress.



## Text-based

* all artifacts are text
  * source code
  * (architecture) diagrams: e.g. PlantUML 
  * graphics: SVG  
  * all documents (e.g. requirements, architecture): Markdown
* enables diffs, merging, global search and replace, and line-by-line traceability


## Versioned

* **everything** is under version control
* exceptions: 
  * issues and backlog
  * secrets (i.e., usernames and passwords)


## Test-centered

* **developer-testing** is central
* test pyramid
  * unit
  * integration
  * system/e2e
  * performance
* all tests are versioned 
* all tests are automatically executed during build  
* TDD when feasible
* developer testing, especially TDD, positively influences design
* developer testing is not (so much) about finding bugs (see "Weinberg's law")
* good test coverage enables safe refactorings

## Always integrated

* **trunk-based** development integrates all sources several times a day
* continuous integration validates the integration
* all pushes to `main` can always be safely integrated with other ongoing work 
* `main` is **always** releasable
* goal: continuous deployment


## Feedback-oriented

* **fast** feedback is paramount
* *build* feedback through **continuous integration**
* fast *integration* feedback through trunk-based development
* fast *reality* feedback through continuous/frequent deployments (e.g. unexpected data in incoming streams, network situations etc.)
* *customer* feedback


## Automated

* test execution
* application deployment
* infrastructure deployment, IaC
* automatic source code quality checks during build for select metrics
  * method length (17)
  * cyclomatic complexity (4)
  * test coverage (80% per module)


## Traceable

* all artifacts linkable with permanent URL
* commit messages
  * relevant issue is referenced (exception: 'general improvement' commits) 
  * all collaborators are referenced ( using `Co-authored-by`)
* all (design/implementation) decisions are documented
  * ADRs
  * commit messages
  * comments (last resort)


## Quality-focused

* structural integrity and readability are paramount
* continuous refactoring to improve internal quality
* little relation to ISO or similar norms
    


