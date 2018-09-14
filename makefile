INFO := MainClass.txt
FILE := $(lastword $(shell cat $(INFO)))

FILES := Population $(FILE)
JFILES := $(patsubst %,%.java,$(FILES))
NESTCS := $(foreach file,$(FILES),$(wildcard $(file)$$*.class))
CFILES := $(patsubst %,%.class,$(FILES)) $(NESTCS)


all:
	$(shell make compile)
	$(shell make submission)

compile:
	javac -cp contest.jar $(JFILES) -Xdiags:verbose

submission:
	jar cmf $(INFO) submission.jar $(subst $$,\$$,$(CFILES))

test:
	make -s testo
	make -s testc
	make -s testk
	make -s tests

testo:
	java -jar testrun.jar -submission=$(FILE) -evaluation=SphereEvaluation -seed=1

testc:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java -jar testrun.jar -submission=$(FILE) -evaluation=BentCigarFunction -seed=1

testk:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java -jar testrun.jar -submission=$(FILE) -evaluation=KatsuuraEvaluation -seed=1

tests:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java -jar testrun.jar -submission=$(FILE) -evaluation=SchaffersEvaluation -seed=1