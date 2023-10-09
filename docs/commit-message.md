
# Template for commit message

Please use this template when committing to GitHub:

```
<qualifier>[(<module>)]: change as imperative sentence

[more details and rationales]

https://jira-se.ifi.uni-heidelberg.de/browse/<jira-id>

Co-authored-by: <git user> <<git email>>
```


## Qualifier 

one of 
   
 * `build` 
 * `doc` 
 * `feature` 
 * `test` 
 * `refactor` 

## Module 

one of 

  * `flinkjobjar` 
  * `flinkservice` 
  * `webapp` 
  * `heroku-flink` 


## Example

a hypothetical example 
concerning the _build_ of the component `webapp`
for the (hypothetical) Jira issue `ISE23-4444`: 

```
build(webapp): run build tasks in parallel


https://jira-se.ifi.uni-heidelberg.de/browse/ISE23-4444

Co-authored-by: Johannes Link <business@johanneslink.net>
```


