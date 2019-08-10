# Query Visualization

## Steps to run locally:

1. Install golang (https://golang.org/doc/install)
2. Clone the project and place it in src folder created during golang installation
3. Go to project directory using "cd" command
4. Run command "go run main.go" to run the server
5. Open browser, go to localhost:4000 (default port)

## Steps for real time update:

1. Select the first operator (because streaming data will be sent for first operator)
2. Execute streaming.sh script using command "sh streaming.sh" to push new data to server and see real time update
