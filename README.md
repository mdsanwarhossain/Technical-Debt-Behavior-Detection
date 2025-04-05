# Technical-Debt-Behavior-Detection
Technical-Debt-Behavior-Detection is a code quality assessment tool for code written in Java. It detects numerous design and implementation smells and many metrics to identify the technical debt behavioral metrics. It also computes many commonly used object-oriented metrics.

## Features
* Detects 17 design smells
	- Imperative Abstraction
	- Multifaceted Abstraction
	- Unnecessary Abstraction
	- Unutilized Abstraction
	- Deficient Encapsulation
	- Unexploited Encapsulation
	- Broken Modularization
	- Cyclic-Dependent Modularization
	- Insufficient Modularization
	- Hub-like Modularization
	- Broken Hierarchy
	- Cyclic Hierarchy
	- Deep Hierarchy
	- Missing Hierarchy
	- Multipath Hierarchy
	- Rebellious Hierarchy
	- Wide Hierarchy
* Detects 10 implementation smells
	- Abstract Function Call From Constructor
	- Complex Conditional
	- Complex Method
	- Empty catch clause
	- Long Identifier
	- Long Method
	- Long Parameter List
	- Long Statement
	- Magic Number
	- Missing default
* Computes following object-oriented metrics
	- LOC (Lines Of Code - at method and class granularity)
	- CC (Cyclomatic Complexity - Method)
	- PC (Parameter Count - Method)
	- NOF (Number of Fields - Class)
	- NOPF (Number of Public Fields - Class)
	- NOM (Number of Methods - Class)
	- NOPM (Number of Public Methods - Class)
	- WMC (Weighted Methods per Class - Class)
	- NC (Number of Children - Class)
	- DIT (Depth of Inheritance Tree - Class)
	- LCOM (Lack of Cohesion in Methods - Class)
	- FANIN (Fan-in - Class)
	- FANOUT (Fan-out - Class)
	
## Integrate with ML soon, now its rule based

## Compilation
We use maven to develop and build this application with the help of Intellji IDE and libraries.
run the following command
```text
mvn clean install
```

## Notes
The implemented LCOM is a custom implementation to avoid the problems of existing LCOM alternatives. Traditional, LCOM value may range only between 0 and 1. However, there are many cases, when computing LCOM is not feasible and traditional implementations give value 0 giving us a false sense of satisfaction. So, when you find -1 as LCOM value for a class, this means we do not have enough information or LCOM is not applicable (for instance, for an interface). 
