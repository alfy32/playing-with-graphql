# playing-with-graphql
This is my test to see how hard it is to work with graphql. 

### Running locally
mvn clean install
Run Application.java from Intellij
POST to /graphql with the following body: 
```
{
 	"query": "query PersonQuery ($id:String) {person(id: $id){id name sex portraitUrl}}",
	"variables": {
		"id": "KWJH-9QS"
	} 
}
```
