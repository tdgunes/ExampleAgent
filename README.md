# Blank ExampleAgent with Maven dependencies


### To create a jar package:

```
mvn clean compile assembly:single  
```

Generated jar with dependencies are located at: `target/agent1-final-jar-with-dependencies.jar`

### To test the jar package:


```
cp ./target/agent1-final-jar-with-dependencies.jar ./genius/agent1.jar
cd genius/
java -cp negosimulator.jar:agent1.jar negotiator.xml.multipartyrunner.Runner multilateraltournament.xml
```


# Help
[The wiki](https://github.com/tdgunes/ExampleAgent/wiki) of this repository has some materials to help you start implementing your negotiating agent. 
 