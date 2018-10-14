PARAMSFILE ?= 
ifeq ($(PARAMSFILE),)
	PARAMS := 
else
	PARAMS := -Dparams="`cat $(PARAMSFILE)`"
endif

INFO := MainClass.txt
FILE := $(lastword $(shell cat $(INFO)))

FMAIN := fitting
FFILES := $(FMAIN) FitPopulation InOut
JFFILES := $(patsubst %,%.java,$(FFILES))
NESTCFS := $(foreach file,$(FFILES),$(wildcard $(file)$$*.class))
CFFILES := $(subst $$,\$$,$(patsubst %,%.class,$(FFILES)) $(NESTCFS))

FILES := $(FILE) Mutation Population Recombination Selection Island $(FFILES)
JFILES := $(patsubst %,%.java,$(FILES))
NESTCS := $(foreach file,$(FILES),$(wildcard $(file)$$*.class))
CFILES := $(subst $$,\$$,$(patsubst %,%.class,$(FILES)) $(NESTCS))

all:
	$(shell make compile)
	$(shell make submission)

compile:
	javac -cp contest.jar $(JFILES) -Xdiags:verbose

submission:
	jar cmf $(INFO) submission.jar $(CFILES)

compilef:
	javac $(JFFILES)

fit:
	java $(FMAIN)

test:
#	make -s testo
	make -s testc
	make -s testk
	make -s tests

testo:
	java $(PARAMS) -jar testrun.jar -submission=$(FILE) -evaluation=SphereEvaluation -seed=1

testc:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java $(PARAMS) -jar testrun.jar -submission=$(FILE) -evaluation=BentCigarFunction -seed=1

testk:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java $(PARAMS) -jar testrun.jar -submission=$(FILE) -evaluation=KatsuuraEvaluation -seed=1

tests:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java $(PARAMS) -jar testrun.jar -submission=$(FILE) -evaluation=SchaffersEvaluation -seed=1

clean:
	rm -rf *~ submission.jar tmp $(CFILES) $(CFFILES)
