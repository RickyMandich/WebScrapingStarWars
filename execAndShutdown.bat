curl -o "./collezione.json" "ftp://swudb:Minecraft35%%3F@ftp.swudb.altervista.org:21/collezione.json" --ftp-pasv
java -jar C:/Users/Public/WebScrapingStarWars.jar
curl -T "./collezione.json" "ftp://swudb:Minecraft35%%3F@ftp.swudb.altervista.org:21/collezione.json" --ftp-pasv --ftp-create-dirs
del collezione.json
start iexplore "www.swudb.altervista.org/login?from=inputJsonDB&userID=ssppoocckk&password=Minecraft35%%3F"
shutdown /s /t 30