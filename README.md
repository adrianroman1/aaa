# AAA Price page tests

## Run tests

Use simple script for run tests on the Unix systems:

$   ./run.sh
    
Use maven commands for run on the Windows:

$   mvn clean install -Djp.skip=false

## Additional parameters

1. -Djp.threads=3 			- if you want to run all tests in any threads
2. -Dconfig.browser=chrome		- if you want to run tests in other browsers (chrome or firefox)

## Test results

Test result location:

- target/jbehave/view/index.html
- target/jbehave/view/report.html
- target/jbehave/view/navigator.html (works in firefox only)
- target/jbehave/view/maps.html
