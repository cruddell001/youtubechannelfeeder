### Youtube Channel Feeder
This is a ktor application that provides RSS feeds for YouTube channels.  
The resulting RSS feed will provide links to transcripts and summaries of the videos, allowing for easy consumption.

### Tech stack
- Kotlin
- Ktor
- MySql
- OpenAI
- youtubetranscript.com

### Running instance
Visit [https://youtubechannelfeeder.com](https://youtubechannelfeeder.com) to see the application in action.

### Running locally
1. Clone the repository
2. Create a `local.properties` file in the root of the project with the following content:
```
youtubeApiKey=<your_youtube_api_key>
databaseName=<your_mysql_database>
databaseUser=<your_mysql_user>
databasePassword=<your_mysql_password>
baseUrl=http://localhost:8080
openAiKey=<your_openai_key>
```
3. Build the application using `./gradlew build`
4. Compile the application using `./gradlew shadowJar`
4. Run the application using `java -jar build/libs/YoutubeRssFeeder-all.jar`
