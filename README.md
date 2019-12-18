# zip-code-engine

- This is a Spring-Boot application responsible to consume zip-code ranges(raw) from a kafka topic and merge overlapping ranges in a given kafka message.
- The merged zip-code(transformed) to then written to a different kafka topic.
- If transformation fails an ever event is generated to an error-topic 

# Execution instructions
- Use the application.yml.sample to override the required properties and paste the file back to resources directory as application.yml
- Compile and generate executable jar by running following commands
    - Mac: `sh gradlew clean build`, Windows: `gradlew.bat clean build`
- Execute the jar by running command `gradlew run`
* note that kafka topics mentioned in the application.yml are expected to be created on Kafka Cluster prior to executing application.