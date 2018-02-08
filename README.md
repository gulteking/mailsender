# mailsender
simple mail sender for birthday cards, seniority dates, etc. Written with Spring boot 

1- Edit smtp account parameters and other server configurations from application.properties file like this or skip step 3

spring.mail.host=smtp.gmail.com

spring.mail.port=587

spring.mail.username=testemail@gmail.com

spring.mail.password=verysecretpassword

spring.mail.properties.mail.smtp.auth=true

spring.mail.properties.mail.smtp.starttls.enable=true

logging.file=mail-application.log

server.port=8080

2- Compile application: mvn clean install

3- if you edit application.properties before compile, just run the application with following command; java -jar mailsender-0.0.1-SNAPSHOT.jar

if you don't want to edit application.properties, you need to pass all smtp&application parameters from command line; 

java -jar -Dserver.port=8080 -Dspring.mail.host=smtp.gmail.com -Dspring.mail.username=testemail@gmail.com -Dspring.mail.password=verysecretpassword -Dspring.mail.port=587 -Dspring.mail.properties.mail.smtp.auth=true -Dspring.mail.properties.mail.smtp.starttls.enable=true -Dlogging.file=mail-application.log mailsender-0.0.1-SNAPSHOT.jar

