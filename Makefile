GRADLE := ./gradlew
PASSWD := huber

.PHONY: all clean compile run wrapper pass

all: run

wrapper:
	gradle wrapper --gradle-version 8.7

pass:
	$(GRADLE) hashPassword -Ppassword='$(PASSWD)'

clean:
	$(GRADLE) clean

compile:
	$(GRADLE) compileJava compileGroovy

run:
	$(GRADLE) bootRun
