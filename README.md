# zopa-mortgage

## How to build and run:
```
- mvn clean package
- Please have market_file csv in same folder of pom.xml
- ./quote <market_file.csv> <loan_amount>
```
**Note:** `quote` is just a executable bash script, if it is not executable at your end, 
give the file executable permissions or run the command as ... 
```
sh quote <market_file.csv> <loan_amount>
``` 

## Disclaimer:

1. Tests: I do not claim to have 100% test coverage, not all classes are covered with tests. Assumption is to demonstrate testability with one or more tests for main business classes.
2. I used spring framework for no specific purpose. I believe the requirements did not explicitely mention that a specific library/framework be used or not used.
