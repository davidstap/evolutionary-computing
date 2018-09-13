```
javac -cp contest.jar player15.java
```
(This will create a player15 class file)

```
jar cmf MainClass.txt submission.jar player15.class
```
(This creates a submission)

```
java -jar testrun.jar -submission=player15 -evaluation=BentCigarFunction -seed=1
```
(This testruns on BentCigarFunction)

