SEED ?= 1
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

FILES := $(FILE) Mutation Population Recombination Selection Island IslandList $(FFILES)
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

fit:
	java $(FMAIN)

test:
#	make -s testo
	make -s testc
	make -s testk
	make -s tests

islandstats:
	make -s testc | grep -Ev 'java|Score|Run' > stats_bentcigar.csv
	make -s testk | grep -Ev 'java|Score|Run' > stats_katsuura.csv
	make -s tests | grep -Ev 'java|Score|Run' > stats_schaffers.csv	

testo:
	java $(PARAMS) -jar testrun.jar -submission=$(FILE) -evaluation=SphereEvaluation -seed=$(SEED)

testc:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java $(PARAMS) -jar testrun.jar -submission=$(FILE) -evaluation=BentCigarFunction -seed=$(SEED)

testk:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java $(PARAMS) -jar testrun.jar -submission=$(FILE) -evaluation=KatsuuraEvaluation -seed=$(SEED)

tests:
	export LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD; \
	java $(PARAMS) -jar testrun.jar -submission=$(FILE) -evaluation=SchaffersEvaluation -seed=$(SEED)

clean:
	rm -rf *~ submission.jar tmp $(CFILES) $(CFFILES)
