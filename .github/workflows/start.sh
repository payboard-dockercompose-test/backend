# start.sh
#!/bin/bash
java -jar /app/*.jar > /app/app.log
service apache2 start
